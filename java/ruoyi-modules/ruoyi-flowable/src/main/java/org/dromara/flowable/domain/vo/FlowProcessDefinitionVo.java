package org.dromara.flowable.domain.vo;

import lombok.Data;
import java.util.Date;

@Data
public class FlowProcessDefinitionVo {
    private String id;
    private String key;
    private String name;
    private Integer version;
    private String deploymentId;
    private String resourceName;
    private String diagramResourceName;
    private String description;
    private Date deploymentTime;
    private Boolean suspended;
    private String category;
}
