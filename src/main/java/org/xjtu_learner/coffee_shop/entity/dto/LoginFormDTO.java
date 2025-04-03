package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

@Data
public class LoginFormDTO {

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 验证码
     */
    private String code;

    /**
     * 密码
     */
    private String password;
}
