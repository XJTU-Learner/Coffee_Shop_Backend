package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

@Data
public class MemberDTO{

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

    /**
     * 头像
     */
    private String avatar;
}
