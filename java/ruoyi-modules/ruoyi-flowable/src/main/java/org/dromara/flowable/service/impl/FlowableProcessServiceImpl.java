package org.dromara.flowable.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.flowable.domain.bo.StartProcessBo;
import org.dromara.flowable.domain.vo.FlowProcessDefinitionVo;
import org.dromara.flowable.domain.vo.FlowTaskVo;
import org.dromara.flowable.service.IFlowableProcessService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FlowableProcessServiceImpl implements IFlowableProcessService {

    private final ProcessEngine processEngine;

    @Override
    public TableDataInfo<FlowProcessDefinitionVo> selectProcessDefinitionPage(String category, PageQuery pageQuery) {
        var repositoryService = processEngine.getRepositoryService();
        var page = repositoryService.createProcessDefinitionQuery()
            .latestVersion()
            .orderByProcessDefinitionKey().desc()
            .listPage(pageQuery.getOffset(), pageQuery.getPageSize());
        
        long count = repositoryService.createProcessDefinitionQuery()
            .latestVersion()
            .count();
        
        List<FlowProcessDefinitionVo> list = page.stream()
            .map(this::convertToVo)
            .collect(Collectors.toList());
        
        return new TableDataInfo<>(list, count);
    }

    @Override
    public List<FlowProcessDefinitionVo> selectProcessDefinitionList(String category) {
        var repositoryService = processEngine.getRepositoryService();
        var query = repositoryService.createProcessDefinitionQuery().latestVersion();
        if (StringUtils.isNotBlank(category)) {
            query.processDefinitionCategory(category);
        }
        return query.orderByProcessDefinitionKey().desc().list()
            .stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public FlowProcessDefinitionVo selectProcessDefinitionById(String processDefinitionId) {
        var repositoryService = processEngine.getRepositoryService();
        ProcessDefinition pd = repositoryService.getProcessDefinition(processDefinitionId);
        if (pd == null) {
            throw new ServiceException("流程定义不存在");
        }
        return convertToVo(pd);
    }

    @Override
    public String deployProcessDefinition(MultipartFile file, String name) {
        try {
            var repositoryService = processEngine.getRepositoryService();
            String fileName = file.getOriginalFilename();
            Deployment deployment = repositoryService.createDeployment()
                .name(name)
                .addInputStream(fileName, file.getInputStream())
                .deploy();
            log.info("部署流程成功: {}", deployment.getId());
            return deployment.getId();
        } catch (IOException e) {
            throw new ServiceException("部署流程失败: " + e.getMessage());
        }
    }

    @Override
    public void suspendOrActivateProcessDefinition(String processDefinitionId, Integer action) {
        var repositoryService = processEngine.getRepositoryService();
        if (action == 1) {
            repositoryService.suspendProcessDefinitionById(processDefinitionId);
        } else {
            repositoryService.activateProcessDefinitionById(processDefinitionId);
        }
    }

    @Override
    public TableDataInfo<FlowTaskVo> selectTodoTaskPage(String userId, PageQuery pageQuery) {
        var taskService = processEngine.getTaskService();
        TaskQuery query = taskService.createTaskQuery()
            .taskCandidateOrAssigned(userId)
            .orderByTaskCreateTime().desc();
        
        List<Task> tasks = query.listPage(pageQuery.getOffset(), pageQuery.getPageSize());
        long count = query.count();
        
        List<FlowTaskVo> list = tasks.stream().map(this::convertTaskToVo).collect(Collectors.toList());
        return new TableDataInfo<>(list, count);
    }

    @Override
    public TableDataInfo<FlowTaskVo> selectCompletedTaskPage(String userId, PageQuery pageQuery) {
        var historyService = processEngine.getHistoryService();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
            .taskAssignee(userId)
            .finished()
            .orderByHistoricTaskInstanceEndTime().desc();
        
        List<HistoricTaskInstance> tasks = query.listPage(pageQuery.getOffset(), pageQuery.getPageSize());
        long count = query.count();
        
        List<FlowTaskVo> list = tasks.stream().map(this::convertHisTaskToVo).collect(Collectors.toList());
        return new TableDataInfo<>(list, count);
    }

    @Override
    public ProcessInstance startProcessInstance(StartProcessBo bo) {
        var runtimeService = processEngine.getRuntimeService();
        return runtimeService.startProcessInstanceByKey(
            bo.getProcessDefinitionKey(),
            bo.getBusinessKey(),
            bo.getVariables()
        );
    }

    @Override
    public void completeTask(String taskId, String userId, Map<String, Object> variables) {
        var taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        taskService.complete(taskId, variables);
    }

    @Override
    public void claimTask(String taskId, String userId) {
        var taskService = processEngine.getTaskService();
        taskService.claim(taskId, userId);
    }

    @Override
    public void delegateTask(String taskId, String assignee) {
        var taskService = processEngine.getTaskService();
        taskService.delegateTask(taskId, assignee);
    }

    @Override
    public byte[] getProcessDiagram(String processInstanceId) {
        var runtimeService = processEngine.getRuntimeService();
        var repositoryService = processEngine.getRepositoryService();
        
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        
        if (pi == null) {
            return null;
        }
        
        String processDefinitionId = pi.getProcessDefinitionId();
        return repositoryService.getProcessDiagram(processDefinitionId);
    }

    private FlowProcessDefinitionVo convertToVo(ProcessDefinition pd) {
        FlowProcessDefinitionVo vo = new FlowProcessDefinitionVo();
        vo.setId(pd.getId());
        vo.setKey(pd.getKey());
        vo.setName(pd.getName());
        vo.setVersion(pd.getVersion());
        vo.setDeploymentId(pd.getDeploymentId());
        vo.setResourceName(pd.getResourceName());
        vo.setDiagramResourceName(pd.getDiagramResourceName());
        vo.setDescription(pd.getDescription());
        vo.setCategory(pd.getCategory());
        vo.setIsSuspended(pd.isSuspended());
        
        if (StringUtils.isNotBlank(pd.getDeploymentId())) {
            Deployment deployment = processEngine.getRepositoryService().getDeployment(pd.getDeploymentId());
            if (deployment != null) {
                vo.setDeploymentTime(new java.util.Date(deployment.getDeploymentTime().getTime()));
            }
        }
        return vo;
    }

    private FlowTaskVo convertTaskToVo(Task task) {
        FlowTaskVo vo = new FlowTaskVo();
        vo.setId(task.getId());
        vo.setName(task.getName());
        vo.setDescription(task.getDescription());
        vo.setOwner(task.getOwner());
        vo.setAssignee(task.getAssignee());
        vo.setCreateTime(new java.util.Date(task.getCreateTime().getTime()));
        vo.setDueDate(task.getDueDate());
        vo.setPriority(String.valueOf(task.getPriority()));
        vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setExecutionId(task.getExecutionId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        return vo;
    }

    private FlowTaskVo convertHisTaskToVo(HistoricTaskInstance task) {
        FlowTaskVo vo = new FlowTaskVo();
        vo.setId(task.getId());
        vo.setName(task.getName());
        vo.setDescription(task.getDescription());
        vo.setOwner(task.getOwner());
        vo.setAssignee(task.getAssignee());
        vo.setCreateTime(new java.util.Date(task.getCreateTime().getTime()));
        vo.setDueDate(task.getDueDate());
        vo.setPriority(String.valueOf(task.getPriority()));
        vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        return vo;
    }
}
