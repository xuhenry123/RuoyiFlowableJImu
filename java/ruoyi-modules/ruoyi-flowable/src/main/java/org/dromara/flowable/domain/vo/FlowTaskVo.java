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
    private String assigneeName;
    private Date createTime;
    private Date dueDate;
    private String priority;
    private String priorityName;
    private String taskDefinitionKey;
    private String processInstanceId;
    private String executionId;
    private String processDefinitionId;
    private String processName;
    private Map<String, Object> variables;
}
