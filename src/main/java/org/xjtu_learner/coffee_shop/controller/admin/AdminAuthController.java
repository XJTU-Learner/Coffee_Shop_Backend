package org.xjtu_learner.coffee_shop.controller.admin;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.common.auth.context.AdminContext;
import org.xjtu_learner.coffee_shop.common.utils.RegexUtil;
import org.xjtu_learner.coffee_shop.entity.dto.AdminDTO;
import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.dto.SignupFormDTO;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;
import org.xjtu_learner.coffee_shop.service.IAdminService;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final IAdminService adminService;

    public AdminAuthController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginFormDTO loginForm) {
        String token = adminService.login(loginForm);
        return ApiResponse.success(token);
    }

    @PostMapping("/code")
    public ApiResponse<String> sendCode(@RequestParam("mobile") String mobile) {
        if (RegexUtil.isPhoneInvalid(mobile)) {
            throw new IllegalArgumentException("非法手机号");
        }

        adminService.sendCode(mobile);
        return ApiResponse.success("验证码发送成功！");
    }

    @PostMapping("/loginByMobile")
    public ApiResponse<String> loginByMobile(@Valid @RequestBody LoginFormDTO loginForm) {
        String token = adminService.loginByMobile(loginForm);
        return ApiResponse.success(token);
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        adminService.logout();
        return ApiResponse.success("登出成功！");
    }

    @GetMapping("/me")
    public ApiResponse<AdminDTO> me() {
        return ApiResponse.success(AdminContext.get());
    }

    @PostMapping("/signup")
    public ApiResponse<String> signup(@Valid @RequestBody SignupFormDTO signupFormDTO){
        adminService.signup(signupFormDTO);
        return ApiResponse.success("注册成功！");
    }
}
