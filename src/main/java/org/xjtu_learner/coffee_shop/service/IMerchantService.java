package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.dto.SignupFormDTO;
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

    String login(LoginFormDTO loginForm);

    void sendCode(String mobile);

    String loginByMobile(LoginFormDTO loginForm);

    void logout();

    void signup(SignupFormDTO signupFormDTO);
}
