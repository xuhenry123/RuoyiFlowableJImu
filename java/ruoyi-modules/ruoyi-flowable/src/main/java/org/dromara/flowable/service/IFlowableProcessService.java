package org.dromara.flowable.service;

import org.dromara.flowable.domain.bo.StartProcessBo;
import org.dromara.flowable.domain.vo.FlowProcessDefinitionVo;
import org.dromara.flowable.domain.vo.FlowTaskVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFlowableProcessService {
    TableDataInfo<FlowProcessDefinitionVo> selectProcessDefinitionPage(String category, PageQuery pageQuery);
    List<FlowProcessDefinitionVo> selectProcessDefinitionList(String category);
    FlowProcessDefinitionVo selectProcessDefinitionById(String processDefinitionId);
    String deployProcessDefinition(MultipartFile file, String name);
    void suspendOrActivateProcessDefinition(String processDefinitionId, Integer action);
    TableDataInfo<FlowTaskVo> selectTodoTaskPage(String userId, PageQuery pageQuery);
    TableDataInfo<FlowTaskVo> selectCompletedTaskPage(String userId, PageQuery pageQuery);
    ProcessInstance startProcessInstance(StartProcessBo bo);
    void completeTask(String taskId, String userId, java.util.Map<String, Object> variables);
    void claimTask(String taskId, String userId);
    byte[] getProcessDiagram(String processInstanceId);
}
