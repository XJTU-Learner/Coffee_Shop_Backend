package org.xjtu_learner.coffee_shop.controller.member;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.ApiResponse;
import org.xjtu_learner.coffee_shop.entity.form.BalancePayForm;
import org.xjtu_learner.coffee_shop.entity.form.GoodsOrderForm;
import org.xjtu_learner.coffee_shop.entity.form.PayOrderForm;
import org.xjtu_learner.coffee_shop.entity.po.GoodsOrder;
import org.xjtu_learner.coffee_shop.entity.po.Member;
import org.xjtu_learner.coffee_shop.service.impl.GoodsOrderServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.MemberServiceImpl;

import java.math.BigDecimal;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.WRONG_PASSWORD;
import static org.xjtu_learner.coffee_shop.common.enums.PaymentStatus.UNPAID;

@RestController
@RequestMapping("/member/order")
public class MemberOrderController {


    private final MemberServiceImpl memberService;
    private final GoodsOrderServiceImpl goodsOrderService;

    public MemberOrderController(GoodsOrderServiceImpl goodsOrderService, MemberServiceImpl memberService) {
        this.goodsOrderService = goodsOrderService;
        this.memberService = memberService;
    }

    @PostMapping
    public ApiResponse<Integer> createOrder(@RequestBody GoodsOrderForm form) {

        Integer orderId = goodsOrderService.createOrder(form);
        return ApiResponse.success(orderId);

    }


    // 基于余额支付
    @PostMapping("/pay/balance")
    public ApiResponse<String> payOrderByBalance(BalancePayForm form) {
        // 检验支付密码是否正确
        boolean success = memberService.checkPaymentPassword(MemberContext.get().getId(), form.getPaymentPassword());
        if (!success) {
            throw new CommonException("支付密码错误", WRONG_PASSWORD);
        }
        goodsOrderService.payOrderByBalance(form.getOrderId());

        return ApiResponse.success("支付成功");

    }
}
