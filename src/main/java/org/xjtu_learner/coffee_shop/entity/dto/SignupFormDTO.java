package org.xjtu_learner.coffee_shop.entity.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static org.xjtu_learner.coffee_shop.common.constant.RegexPatterns.PHONE_REGEX;

@Data
public class SignupFormDTO {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = PHONE_REGEX,message = "非法手机号")
    private String mobile;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 昵称
     */
    private String nickname;
}
