package org.xjtu_learner.coffee_shop.controller.member;

import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.entity.dto.ApiResponse;

@RestController
@RequestMapping("/member/profile")
public class MemberProfileController {

    @PostMapping("/paymentPassword")
    public ApiResponse<String> setPaymentPassword(@RequestParam("password") String password){

        return null;
    }

    @PutMapping("/paymentPassword")
    public ApiResponse<String> changePaymentPassword(){

        return null;
    }
}
