package org.xjtu_learner.coffee_shop.entity.form;

import lombok.Data;

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
