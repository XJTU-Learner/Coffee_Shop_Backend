package org.xjtu_learner.coffee_shop.controller.merchant;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.common.auth.context.AdminContext;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.common.utils.RegexUtil;
import org.xjtu_learner.coffee_shop.entity.dto.AdminDTO;
import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.dto.MerchantDTO;
import org.xjtu_learner.coffee_shop.entity.dto.SignupFormDTO;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;
import org.xjtu_learner.coffee_shop.service.IAdminService;
import org.xjtu_learner.coffee_shop.service.IMerchantService;

@RestController
@RequestMapping("/merchant/auth")
public class MerchantAuthController {


    private final IMerchantService merchantService;
    
    public MerchantAuthController(IMerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginFormDTO loginForm) {
        String token = merchantService.login(loginForm);
        return ApiResponse.success(token);
    }

    @PostMapping("/code")
    public ApiResponse<String> sendCode(@RequestParam("mobile") String mobile) {
        if (RegexUtil.isPhoneInvalid(mobile)) {
            throw new IllegalArgumentException("非法手机号");
        }

        merchantService.sendCode(mobile);
        return ApiResponse.success("验证码发送成功！");
    }

    @PostMapping("/loginByMobile")
    public ApiResponse<String> loginByMobile(@Valid @RequestBody LoginFormDTO loginForm) {
        String token = merchantService.loginByMobile(loginForm);
        return ApiResponse.success(token);
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        merchantService.logout();
        return ApiResponse.success("登出成功！");
    }

    @GetMapping("/me")
    public ApiResponse<MerchantDTO> me() {
        return ApiResponse.success(MerchantContext.get());
    }

    @PostMapping("/signup")
    public ApiResponse<String> signup(@Valid @RequestBody SignupFormDTO signupFormDTO){
        merchantService.signup(signupFormDTO);
        return ApiResponse.success("注册成功！");
    }
}
