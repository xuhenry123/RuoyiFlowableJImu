package org.dromara.flowable.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class FlowTaskVo {
    private String id;
    private String name;
    private String description;
    private String owner;
    private String assignee;
    private Date createTime;
    private Date dueDate;
    String priority;
    String taskDefinitionKey;
    String processInstanceId;
    String executionId;
    String processDefinitionId;
    Map<String, Object> variables;
}
