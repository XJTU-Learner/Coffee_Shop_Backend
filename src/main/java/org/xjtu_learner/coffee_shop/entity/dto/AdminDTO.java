package org.xjtu_learner.coffee_shop.entity.dto;


import lombok.Data;

@Data
public class AdminDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickname;

}
