package org.xjtu_learner.coffee_shop.controller.member;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.entity.dto.ApiResponse;

@RestController
@RequestMapping("/member/order")
public class MemberOrderController {

    @PostMapping
    public ApiResponse<String> createOrder(){

        //

        return null;
    }

}
