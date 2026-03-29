package org.dromara.report.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ReportVo {
    private String id;
    private String code;
    private String name;
    private String type;
    private String status;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
