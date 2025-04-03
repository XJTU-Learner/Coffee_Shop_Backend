package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.util.RandomUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xjtu_learner.coffee_shop.common.auth.VerificationCodeManager;
import org.xjtu_learner.coffee_shop.common.auth.session.impl.AdminSessionManager;
import org.xjtu_learner.coffee_shop.common.utils.RegexUtil;
import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.po.Admin;
import org.xjtu_learner.coffee_shop.dao.AdminMapper;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;
import org.xjtu_learner.coffee_shop.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


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
    public ApiResponse<String> login(LoginFormDTO loginForm) {

        // 校验手机号
        if(RegexUtil.isPhoneInvalid(loginForm.getMobile())){
            return ApiResponse.failure("非法手机号");
        }

        // 校验账号与密码
        Admin admin = lambdaQuery()
                .eq(Admin::getMobile, loginForm.getMobile())
                .one();

        if(admin==null){
            return ApiResponse.failure("账号不存在");
        }

        if(!passwordEncoder.matches(loginForm.getPassword(),admin.getPassword())){
            return ApiResponse.failure("密码错误");
        }

        // 为用户在redis创建session
        String token = adminSessionManager.createSession(admin);

        return ApiResponse.success(token);
    }

    @Override
    public ApiResponse<String> loginByMobile(LoginFormDTO loginForm) {

        // 校验手机号
        if(RegexUtil.isPhoneInvalid(loginForm.getMobile())){
            return ApiResponse.failure("非法手机号");
        }

        // 校验验证码
        if(!verificationCodeManager.verifyCode(loginForm.getMobile(), loginForm.getCode())) return ApiResponse.failure("登陆失败");

        // 查询用户
        Admin admin = lambdaQuery()
                .eq(Admin::getMobile, loginForm.getMobile())
                .one();

        // 未查询到则注册新用户
        if(admin == null){
            admin = new Admin();
            admin.setMobile(loginForm.getMobile());
            admin.setNickname("用户" + RandomUtil.randomString(6));
            save(admin);
        }

        // 为用户在redis创建session
        String token = adminSessionManager.createSession(admin);

        return ApiResponse.success(token);
    }

    @Override
    public ApiResponse<String> logout() {
        //TODO:获取token
        String token = "";
        adminSessionManager.removeSession(token);

        return ApiResponse.success("登出成功！");
    }
}
