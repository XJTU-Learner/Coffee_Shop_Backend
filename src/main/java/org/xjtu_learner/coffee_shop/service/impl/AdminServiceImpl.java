package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xjtu_learner.coffee_shop.common.auth.VerificationCodeManager;
import org.xjtu_learner.coffee_shop.common.auth.session.impl.AdminSessionManager;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.utils.HttpContext;
import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.dto.SignupFormDTO;
import org.xjtu_learner.coffee_shop.entity.po.Admin;
import org.xjtu_learner.coffee_shop.dao.AdminMapper;
import org.xjtu_learner.coffee_shop.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.ADMIN_SESSION_CODE_PREFIX;


/**
 * <p>
 * 后台管理员表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    private final VerificationCodeManager verificationCodeManager;
    private final AdminSessionManager adminSessionManager;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(VerificationCodeManager verificationCodeManager, AdminSessionManager adminSessionManager, PasswordEncoder passwordEncoder) {
        this.verificationCodeManager = verificationCodeManager;
        this.adminSessionManager = adminSessionManager;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public String login(LoginFormDTO loginForm) {

        // 校验账号与密码
        Admin admin = lambdaQuery()
                .eq(Admin::getMobile, loginForm.getMobile())
                .one();

        if (admin == null) {
            throw new CommonException("账号不存在！", ACCOUNT_NOT_EXIST);
        }

        if (!passwordEncoder.matches(loginForm.getPassword(), admin.getPassword())) {
            throw new CommonException("密码错误!", WRONG_PASSWORD);
        }

        // 为用户在redis创建session
        return adminSessionManager.createSession(admin);
    }

    @Override
    public void sendCode(String mobile) {
        verificationCodeManager.sendCode(ADMIN_SESSION_CODE_PREFIX,
                mobile);
    }

    @Override
    public String loginByMobile(LoginFormDTO loginForm) {

        // 校验验证码
        if (!verificationCodeManager.verifyCode(ADMIN_SESSION_CODE_PREFIX, loginForm.getMobile(), loginForm.getCode()))
            throw new CommonException("验证码错误", WRONG_VERIFICATION_CODE);

        // 查询用户
        Admin admin = lambdaQuery()
                .eq(Admin::getMobile, loginForm.getMobile())
                .one();

        // 未查询到则注册新用户
        if (admin == null) {
            admin = new Admin();
            admin.setMobile(loginForm.getMobile());
            admin.setNickname("用户" + RandomUtil.randomString(6));
            admin.setRegisterTime(LocalDateTime.now());
            save(admin);
        }

        // 为用户在redis创建session
        return adminSessionManager.createSession(admin);
    }

    @Override
    public void logout() {
        String token = HttpContext.getRequestHeader("Authorization");
        adminSessionManager.removeSession(token);
    }

    @Override
    public void signup(SignupFormDTO signupFormDTO) {
        Admin admin = new Admin();
        admin.setMobile(signupFormDTO.getMobile());
        // 将密码加密后存入
        admin.setPassword(passwordEncoder.encode(signupFormDTO.getPassword()));
        if (StrUtil.isBlank(signupFormDTO.getNickname())) {
            admin.setNickname("用户" + RandomUtil.randomString(6));
        } else {
            admin.setNickname(signupFormDTO.getNickname());
        }
        admin.setRegisterTime(LocalDateTime.now());
        try {
            save(admin);
        } catch (DataIntegrityViolationException ex) {
            // 如果手机号冲突则抛出错误
            if (ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new CommonException("手机号已存在，请使用其他手机号注册", ALREADY_EXIST);
            }
        }
    }
}

