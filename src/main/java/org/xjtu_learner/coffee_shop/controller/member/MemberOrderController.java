package org.xjtu_learner.coffee_shop.controller.member;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.entity.dto.ApiResponse;
import org.xjtu_learner.coffee_shop.entity.dto.MemberOrderForm;
import org.xjtu_learner.coffee_shop.service.impl.GoodsOrderServiceImpl;

@RestController
@RequestMapping("/member/order")
public class MemberOrderController {


    private final GoodsOrderServiceImpl goodsOrderService;

    public MemberOrderController(GoodsOrderServiceImpl goodsOrderService) {
        this.goodsOrderService = goodsOrderService;
    }

    @PostMapping("/createOrder")
    public ApiResponse<String> createOrder(@RequestBody MemberOrderForm memberOrderForm){

        goodsOrderService.createOrder(memberOrderForm);
        return ApiResponse.success("下单成功请及时支付！");

    }

}
