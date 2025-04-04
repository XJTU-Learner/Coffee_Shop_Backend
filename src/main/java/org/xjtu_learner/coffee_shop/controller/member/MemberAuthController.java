package org.xjtu_learner.coffee_shop.controller.member;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.common.utils.RegexUtil;
import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.dto.MemberDTO;
import org.xjtu_learner.coffee_shop.entity.dto.MerchantDTO;
import org.xjtu_learner.coffee_shop.entity.dto.SignupFormDTO;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;
import org.xjtu_learner.coffee_shop.service.IMemberService;
import org.xjtu_learner.coffee_shop.service.IMerchantService;

@RestController
@RequestMapping("/member/auth")
public class MemberAuthController {


    private final IMemberService memberService;

    public MemberAuthController(IMemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/wxLogin")
    public ApiResponse<String> wxLogin(@RequestParam("code") String code){
        String token = memberService.wxLogin(code);
        return ApiResponse.success(token);
    }

    @PostMapping("/code")
    public ApiResponse<String> sendCode(@RequestParam("mobile") String mobile) {
        if (RegexUtil.isPhoneInvalid(mobile)) {
            throw new IllegalArgumentException("非法手机号");
        }

        memberService.sendCode(mobile);
        return ApiResponse.success("验证码发送成功！");
    }

    @PostMapping("/loginByMobile")
    public ApiResponse<String> loginByMobile(@Valid @RequestBody LoginFormDTO loginForm) {
        String token = memberService.loginByMobile(loginForm);
        return ApiResponse.success(token);
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        memberService.logout();
        return ApiResponse.success("登出成功！");
    }

    @GetMapping("/me")
    public ApiResponse<MemberDTO> me() {
        return ApiResponse.success(MemberContext.get());
    }
}
