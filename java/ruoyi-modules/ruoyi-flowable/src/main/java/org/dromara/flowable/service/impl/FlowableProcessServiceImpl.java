package org.dromara.flowable.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlowableProcessServiceImpl implements IFlowableProcessService {

    private final ProcessEngine processEngine;

    @Override
    public TableDataInfo<FlowProcessDefinitionVo> selectProcessDefinitionPage(String category, PageQuery pageQuery) {
        var repositoryService = processEngine.getRepositoryService();
        var query = repositoryService.createProcessDefinitionQuery().latestVersion();
        if (StringUtils.isNotBlank(category)) {
            query.processDefinitionCategory(category);
        }
        var page = query.orderByProcessDefinitionKey().desc()
            .listPage(pageQuery.getOffset(), pageQuery.getPageSize());
        long count = query.count();
        List<FlowProcessDefinitionVo> list = page.stream().map(this::convertToVo).collect(Collectors.toList());
        return new TableDataInfo<>(list, count);
    }

    @Override
    public List<FlowProcessDefinitionVo> selectProcessDefinitionList(String category) {
        var repositoryService = processEngine.getRepositoryService();
        var query = repositoryService.createProcessDefinitionQuery().latestVersion();
        if (StringUtils.isNotBlank(category)) {
            query.processDefinitionCategory(category);
        }
        return query.orderByProcessDefinitionKey().desc()
            .list().stream().map(this::convertToVo).collect(Collectors.toList());
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
    public String getProcessDefinitionXml(String processDefinitionId) {
        var repositoryService = processEngine.getRepositoryService();
        ProcessDefinition pd = repositoryService.getProcessDefinition(processDefinitionId);
        if (pd == null) {
            return null;
        }
        try {
            InputStream is = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName());
            if (is != null) {
                return IOUtils.toString(is, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("获取流程XML失败", e);
        }
        return null;
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
    public String deployProcessDefinitionFromXml(String xml, String name) {
        if (StringUtils.isBlank(xml)) {
            throw new ServiceException("流程XML不能为空");
        }
        try {
            var repositoryService = processEngine.getRepositoryService();
            Deployment deployment = repositoryService.createDeployment()
                .name(name)
                .addString("process.bpmn", xml)
                .deploy();
            log.info("从XML部署流程成功: {}", deployment.getId());
            return deployment.getId();
        } catch (Exception e) {
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
    public void deleteProcessDefinition(String processDefinitionId) {
        var repositoryService = processEngine.getRepositoryService();
        ProcessDefinition pd = repositoryService.getProcessDefinition(processDefinitionId);
        if (pd != null && StringUtils.isNotBlank(pd.getDeploymentId())) {
            repositoryService.deleteDeployment(pd.getDeploymentId(), true);
        }
    }

    @Override
    public TableDataInfo<FlowTaskVo> selectTodoTaskPage(String userId, PageQuery pageQuery) {
        var taskService = processEngine.getTaskService();
        var query = taskService.createTaskQuery()
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
        return runtimeService.startProcessInstanceByKey(bo.getProcessDefinitionKey(), bo.getBusinessKey(), bo.getVariables());
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
    public byte[] getProcessDiagram(String processInstanceId) {
        var runtimeService = processEngine.getRuntimeService();
        var repositoryService = processEngine.getRepositoryService();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        if (pi == null) {
            return null;
        }
        return repositoryService.getProcessDiagram(pi.getProcessDefinitionId());
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
        vo.setSuspended(pd.isSuspended());
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
        if (task.getCreateTime() != null) {
            vo.setCreateTime(new java.util.Date(task.getCreateTime().getTime()));
        }
        if (task.getDueDate() != null) {
            vo.setDueDate(task.getDueDate());
        }
        vo.setPriority(String.valueOf(task.getPriority()));
        vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        return vo;
    }
}
