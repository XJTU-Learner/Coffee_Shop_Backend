package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xjtu_learner.coffee_shop.common.auth.VerificationCodeManager;
import org.xjtu_learner.coffee_shop.common.auth.session.impl.MerchantSessionManager;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.utils.HttpContext;
import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.dto.SignupFormDTO;
import org.xjtu_learner.coffee_shop.entity.po.Merchant;
import org.xjtu_learner.coffee_shop.dao.MerchantMapper;
import org.xjtu_learner.coffee_shop.service.IMerchantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.MERCHANT_SESSION_CODE_PREFIX;

/**
 * <p>
 * 商家账号表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements IMerchantService {

    private final VerificationCodeManager verificationCodeManager;
    private final MerchantSessionManager merchantSessionManager;
    private final PasswordEncoder passwordEncoder;

    public MerchantServiceImpl(VerificationCodeManager verificationCodeManager, MerchantSessionManager merchantSessionManager, PasswordEncoder passwordEncoder) {
        this.verificationCodeManager = verificationCodeManager;
        this.merchantSessionManager = merchantSessionManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String login(LoginFormDTO loginForm) {

        // 校验账号与密码
        Merchant merchant = lambdaQuery()
                .eq(Merchant::getMobile, loginForm.getMobile())
                .one();

        if (merchant == null) {
            throw new CommonException("账号不存在！", ACCOUNT_NOT_EXIST);
        }

        if (!passwordEncoder.matches(loginForm.getPassword(), merchant.getPassword())) {
            throw new CommonException("密码错误!", WRONG_PASSWORD);
        }

        // 为用户在redis创建session
        return merchantSessionManager.createSession(merchant);
    }

    @Override
    public void sendCode(String mobile) {
        verificationCodeManager.sendCode(MERCHANT_SESSION_CODE_PREFIX, mobile);
    }

    @Override
    public String loginByMobile(LoginFormDTO loginForm) {
        // 校验验证码
        if (!verificationCodeManager.verifyCode(MERCHANT_SESSION_CODE_PREFIX, loginForm.getMobile(), loginForm.getCode()))
            throw new CommonException("验证码错误", WRONG_VERIFICATION_CODE);

        // 查询用户
        Merchant merchant = lambdaQuery()
                .eq(Merchant::getMobile, loginForm.getMobile())
                .one();

        // 未查询到则注册新用户
        if (merchant == null) {
            merchant = new Merchant();
            merchant.setMobile(loginForm.getMobile());
            merchant.setNickname("用户" + RandomUtil.randomString(6));
            merchant.setRegisterTime(LocalDateTime.now());
            save(merchant);
        }

        // 为用户在redis创建session
        return merchantSessionManager.createSession(merchant);
    }

    @Override
    public void logout() {
        String token = HttpContext.getRequestHeader("Authorization");
        merchantSessionManager.removeSession(token);
    }

    @Override
    public void signup(SignupFormDTO signupFormDTO) {
        Merchant merchant = new Merchant();
        merchant.setMobile(signupFormDTO.getMobile());
        // 将密码加密后存入
        merchant.setPassword(passwordEncoder.encode(signupFormDTO.getPassword()));
        if (StrUtil.isBlank(signupFormDTO.getNickname())) {
            merchant.setNickname("用户" + RandomUtil.randomString(6));
        } else {
            merchant.setNickname(signupFormDTO.getNickname());
        }
        merchant.setRegisterTime(LocalDateTime.now());
        try {
            save(merchant);
        } catch (DataIntegrityViolationException ex) {
            // 如果手机号冲突则抛出错误
            if (ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new CommonException("手机号已存在，请使用其他手机号注册", ALREADY_EXIST);
            }
            throw new RuntimeException(ex.getMessage());
        }

    }
}
