package org.xjtu_learner.coffee_shop.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantChangeApplication {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 申请商户id
     */
    private Integer merchantId;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;



}
