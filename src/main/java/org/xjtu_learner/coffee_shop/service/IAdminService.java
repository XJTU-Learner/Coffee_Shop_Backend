package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.dto.SignupFormDTO;
import org.xjtu_learner.coffee_shop.entity.po.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 后台管理员表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface IAdminService extends IService<Admin> {

    String login(LoginFormDTO loginForm);

    String loginByMobile(LoginFormDTO loginForm);

    void logout();

    void sendCode(String mobile);

    void signup(SignupFormDTO signupFormDTO);
}
