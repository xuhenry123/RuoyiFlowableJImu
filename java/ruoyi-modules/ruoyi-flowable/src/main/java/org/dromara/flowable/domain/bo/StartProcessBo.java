package org.dromara.flowable.domain.bo;

import lombok.Data;

import java.util.Map;

@Data
public class StartProcessBo {
    private String processDefinitionKey;
    private String businessKey;
    private Map<String, Object> variables;
}
