package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequireChangeItem {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 申请商户id
     */
    private Integer merchantId;

    /**
     * 门店名称
     */
    private String nickName;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

}
