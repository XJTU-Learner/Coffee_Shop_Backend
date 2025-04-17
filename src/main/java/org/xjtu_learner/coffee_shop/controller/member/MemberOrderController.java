package org.xjtu_learner.coffee_shop.controller.member;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.entity.dto.ApiResponse;
import org.xjtu_learner.coffee_shop.entity.form.MemberOrderForm;
import org.xjtu_learner.coffee_shop.entity.form.MemberOrderForm;
import org.xjtu_learner.coffee_shop.entity.dto.PayOrderForm;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrder;
import org.xjtu_learner.coffee_shop.entity.po.Member;
import org.xjtu_learner.coffee_shop.service.impl.GoodsOrderServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.MemberServiceImpl;

import java.math.BigDecimal;

import static org.xjtu_learner.coffee_shop.common.enums.OrderStatus.WAIT_PAY;
import static org.xjtu_learner.coffee_shop.common.enums.PaymentStatus.WaitPay;

@RestController
@RequestMapping("/member/order")
public class MemberOrderController {


    private final GoodsOrderServiceImpl goodsOrderService;
    private final MemberServiceImpl memberService;

    public MemberOrderController(GoodsOrderServiceImpl goodsOrderService, MemberServiceImpl memberService) {
        this.goodsOrderService = goodsOrderService;
        this.memberService = memberService;
    }

    @PostMapping("/createorder")
    @Transactional
    public ApiResponse<String> createOrder(@RequestBody MemberOrderForm memberOrderForm){

        goodsOrderService.createOrder(memberOrderForm);
        return ApiResponse.success("下单成功请及时支付！");

    }

    //支付订单
    @PostMapping("/pay")
    @Transactional
    public ApiResponse<String> payOrder(@RequestBody PayOrderForm payOrderForm){
        GoodsOrder goodsOrder=goodsOrderService.getById(payOrderForm.getOrder_id());
        if(goodsOrder==null || goodsOrder.getPaymentStatus()!=WaitPay){
            return ApiResponse.failure("订单不存在或已支付！");
        }
        else {
            Member member = memberService.getById(MemberContext.get().getId());
            if (member.getBalance().compareTo(new BigDecimal(payOrderForm.getActual_price())) < 0) {
                return ApiResponse.failure("余额不足！");
            } else {
                goodsOrderService.payOrder(payOrderForm.getOrder_id());
                return ApiResponse.success("支付成功！");

            }

        }

    }

}
