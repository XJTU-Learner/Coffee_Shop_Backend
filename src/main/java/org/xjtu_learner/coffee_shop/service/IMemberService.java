package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.dto.SignupFormDTO;
import org.xjtu_learner.coffee_shop.entity.po.Member;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface IMemberService extends IService<Member> {

    void sendCode(String mobile);

    String loginByMobile(LoginFormDTO loginForm);

    void logout();

    String wxLogin(String code);
}
