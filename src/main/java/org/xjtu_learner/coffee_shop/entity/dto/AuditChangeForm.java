package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditChangeForm {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 审批成功与否
     */
    private Boolean success;

    /**
     * 审批失败原因
     */
    private String auditReason;
}
