package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

@Data
public class MerchantDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickname;
}