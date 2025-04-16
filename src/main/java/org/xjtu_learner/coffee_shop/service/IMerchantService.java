package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.form.LoginForm;
import org.xjtu_learner.coffee_shop.entity.form.SignupForm;
import org.xjtu_learner.coffee_shop.entity.po.Merchant;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商家账号表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface IMerchantService extends IService<Merchant> {

    String login(LoginForm loginForm);

    void sendCode(String mobile);

    String loginByMobile(LoginForm loginForm);

    void logout();

    void signup(SignupForm signupForm);
}
