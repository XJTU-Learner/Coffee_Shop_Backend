package org.xjtu_learner.coffee_shop.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.common.constant.MqConstant;
import org.xjtu_learner.coffee_shop.common.enums.OrderStatus;
import org.xjtu_learner.coffee_shop.common.enums.PaymentStatus;
import org.xjtu_learner.coffee_shop.common.enums.PreferentialType;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.mq.MultiDelayMessage;
import org.xjtu_learner.coffee_shop.common.mq.processor.DelayMessageProcessor;
import org.xjtu_learner.coffee_shop.entity.form.GoodsOrderDetailForm;
import org.xjtu_learner.coffee_shop.entity.form.GoodsOrderForm;
import org.xjtu_learner.coffee_shop.entity.po.*;
import org.xjtu_learner.coffee_shop.dao.GoodsOrderMapper;
import org.xjtu_learner.coffee_shop.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;
import static org.xjtu_learner.coffee_shop.common.constant.RuleConstant.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Slf4j
@Service
public class GoodsOrderServiceImpl extends ServiceImpl<GoodsOrderMapper, GoodsOrder> implements IGoodsOrderService {


    private final IGoodsService goodsService;
    private final MemberServiceImpl memberService;
    private final CallMeBotService callMeBotService;
    private final ICouponsService couponsService;
    private final IGoodsOrderDetailService goodsOrderDetailService;
    private final ICouponsMemberRelationService couponsMemberRelationService;
    private final ICouponsGoodsRelationService couponsGoodsRelationService;
    private final ICouponsShopRelationService couponsShopRelationService;

    private final RabbitTemplate rabbitTemplate;

    private final TransactionTemplate transactionTemplate;

    public GoodsOrderServiceImpl(GoodsServiceImpl goodsService, GoodsOrderDetailServiceImpl goodsOrderDetailService, CouponsServiceImpl couponsService, CouponsMemberRelationServiceImpl couponsMemberRelationService, CallMeBotService callMeBotService, MemberServiceImpl memberService, ICouponsShopRelationService couponsShopRelationService, RabbitTemplate rabbitTemplate, TransactionTemplate transactionTemplate, CallMeBotService callMeBotService1, MemberServiceImpl memberService1, ICouponsGoodsRelationService couponsGoodsRelationService, TransactionTemplate transactionTemplate1) {
        this.goodsService = goodsService;
        this.goodsOrderDetailService = goodsOrderDetailService;
        this.couponsService = couponsService;
        this.couponsMemberRelationService = couponsMemberRelationService;
        this.couponsShopRelationService = couponsShopRelationService;
        this.rabbitTemplate = rabbitTemplate;
        this.callMeBotService = callMeBotService1;
        this.memberService = memberService1;
        this.couponsGoodsRelationService = couponsGoodsRelationService;
        this.transactionTemplate = transactionTemplate1;
    }

    @Override
    @Transactional
    public Integer createOrder(GoodsOrderForm goodsOrderForm) {

        Integer memberId = MemberContext.get().getId();
        Integer merchantId = goodsOrderForm.getMerchantId();

        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setMemberId(memberId);
        goodsOrder.setMerchantId(merchantId);
        goodsOrder.setRemark(goodsOrderForm.getRemark());
        goodsOrder.setPaymentMode(goodsOrderForm.getPaymentMode());


        List<GoodsOrderDetailForm> details = goodsOrderForm.getDetail();

        // 统计商品总数
        int totalCount = details.size();
        goodsOrder.setGoodsTotalQuantity(totalCount);

        // 先通过缓存查得各商品详细信息
        List<Goods> goodsList = goodsService.getGoodsList(details.stream().map(GoodsOrderDetailForm::getGoodsId).toList());

        Map<Integer, Goods> goodsMap = goodsList.stream()
                .collect(Collectors.toMap(
                        Goods::getId,
                        (goods) -> (goods)
                ));

        // 总基础金额
        BigDecimal totalBaseAmount = BigDecimal.valueOf(0);
        // 总实际金额
        BigDecimal totalActualAmount = BigDecimal.valueOf(0);
        // 平台抽成金额
        BigDecimal platformExtractPrice = BigDecimal.valueOf(0);

        List<GoodsOrderDetail> orderDetailList = new ArrayList<>();
        for (GoodsOrderDetailForm detail : details) {
            Goods goods = goodsMap.get(detail.getGoodsId());

            GoodsOrderDetail orderDetail = new GoodsOrderDetail();
            orderDetail.setGoodsId(goods.getId());
            orderDetail.setInfo(detail.getInfo());

            BigDecimal basePrice = goods.getBasePrice();
            // 累加基础金额
            totalBaseAmount = totalBaseAmount.add(basePrice);

            // TODO: 这里不一定符合现实，在基础金额上计算（优惠券成本转嫁给商家）是最简单的实现，后期可以更改为更复杂更实际的实现
            // 累加平台抽成（在基础金额上计算，与优惠券无关）
            platformExtractPrice = platformExtractPrice.add(basePrice.multiply(goods.getPlatformExtractRatio()));

            // 判断该商品是否使用优惠券
            Integer couponsMemberRelationId = detail.getCouponsMemberRelationId();
            if (couponsMemberRelationId != null) {
                // 这里要进行一次mysql查询
                Integer couponsId = couponsMemberRelationService.checkValid(memberId, couponsMemberRelationId);
                Coupons coupons = couponsService.getCoupons(couponsId);

                if (!(coupons.getPreferentialType() == PreferentialType.DISCOUNT)) {
                    throw new CommonException("异常优惠券，商品只能使用‘折扣’类型的优惠券", INVALID_ARGUMENT);
                }

                // 检验优惠券是否对有效门店和有效商品使用
                if (!coupons.getIsGoodsUniversal()) {
                    couponsGoodsRelationService.checkValid(couponsId, goods.getId());
                }
                if (!coupons.getIsShopUniversal()) {
                    couponsShopRelationService.checkValid(couponsId, merchantId);
                }

                orderDetail.setCouponsMemberRelationId(couponsMemberRelationId);
                BigDecimal discount = coupons.getDiscount();
                BigDecimal actualPrice = basePrice.multiply(discount);
                BigDecimal discountAmount = basePrice.subtract(actualPrice);

                orderDetail.setActualAmount(actualPrice);
                orderDetail.setCouponsDiscountAmount(discountAmount);
                // 累加实际金额
                totalActualAmount = totalActualAmount.add(actualPrice);
            } else {
                orderDetail.setActualAmount(basePrice);
                // 累加实际金额
                totalActualAmount = totalActualAmount.add(basePrice);
            }

            orderDetailList.add(orderDetail);
        }

        goodsOrder.setGoodsTotalBaseAmount(totalBaseAmount);
        goodsOrder.setGoodsTotalActualAmount(totalActualAmount);
        goodsOrder.setPlatformExtractPrice(platformExtractPrice);


        BigDecimal actualPrice;
        // 如果该订单使用了满减优惠券
        Integer couponsMemberRelationId = goodsOrderForm.getCouponsMemberRelationId();
        if (couponsMemberRelationId != null) {
            // 检验该优惠券的有效性（这里要进行一次mysql查询）
            Integer couponsId = couponsMemberRelationService.checkValid(memberId, couponsMemberRelationId);
            // 从缓存中查询优惠券信息得到满减金额
            Coupons coupons = couponsService.getCoupons(couponsId);

            if (!(coupons.getPreferentialType() == PreferentialType.REDUCTION)) {
                throw new CommonException("异常优惠券，订单整体只能使用‘满减’类型的优惠券", INVALID_ARGUMENT);
            }

            // 检验优惠券是否对有效门店使用
            if (!coupons.getIsShopUniversal()) {
                couponsShopRelationService.checkValid(couponsId, merchantId);
            }

            goodsOrder.setCouponsMemberRelationId(couponsMemberRelationId);
            BigDecimal limitedAmount = coupons.getLimitedAmount();

            // 未达到满减金额
            if (totalActualAmount.compareTo(limitedAmount) < 0) {
                throw new CommonException("订单总金额未达到满减门槛", INVALID_ARGUMENT);
            }
            goodsOrder.setCouponReducedAmount(limitedAmount);

            BigDecimal reducedAmount = coupons.getReducedAmount();
            actualPrice = totalActualAmount.subtract(reducedAmount);
        } else {
            actualPrice = totalActualAmount;
        }

        goodsOrder.setActualPrice(actualPrice);

        // 根据实付款计算获得积分值
        BigDecimal pointIncrease = actualPrice.multiply(POINTS_ACQUISITION_RATIO);
        goodsOrder.setPointIncrease(pointIncrease);

        // 商家实际收入 = 实付款 - 平台抽成金额
        BigDecimal merchantIncome = actualPrice.subtract(platformExtractPrice);
        goodsOrder.setMerchantIncome(merchantIncome);

        goodsOrder.setPaymentDeadline(LocalDateTime.now().plus(PAYMENT_DEADLINE));

        // 订单入库
        save(goodsOrder);
        Integer orderId = goodsOrder.getId();

        orderDetailList.forEach(
                (detail) -> detail.setOrderId(orderId)
        );

        // 订单细节入库
        goodsOrderDetailService.saveBatch(orderDetailList);

        // 延迟检测订单状态消息
        MultiDelayMessage<Integer> delayMessage = MultiDelayMessage.of(orderId, DEFAULT_DELAY_INTERVAL);
        try {
            rabbitTemplate.convertAndSend(
                    MqConstant.ORDER_DELAY_EXCHANGE, MqConstant.ORDER_DELAY_ROUTING_KEY, delayMessage,
                    new DelayMessageProcessor(delayMessage.removeNextDelay())
            );
        } catch (AmqpException e) {
            throw new CommonException("延迟消息发送异常", MESSAGE_SEND_FAILED);
        }

        //TODO: 异步写入用户流水
        log.debug("异步写入用户流水");

        //TODO: 异步写入商家流水
        log.debug("异步写入商家流水");

        //TODO: 异步写入平台流水
        log.debug("异步写入平台流水");

        return orderId;
    }


    @Override
    public boolean markOrderPaySuccess(Integer orderId) {
        return lambdaUpdate()
                .set(GoodsOrder::getStatus, OrderStatus.IN_PRODUCTION)
                .eq(GoodsOrder::getId, orderId)
                .eq(GoodsOrder::getStatus, OrderStatus.UNPAID)
                .update();
    }

    @Override
    public boolean timeoutCancel(Integer orderId) {
        return lambdaUpdate()
                .set(GoodsOrder::getStatus, OrderStatus.TIMED_OUT)
                .eq(GoodsOrder::getId, orderId)
                .eq(GoodsOrder::getStatus, OrderStatus.UNPAID)
                .update();
    }


    @Override
    @Transactional
    public void payOrderByBalance(Integer orderId) {

        // 检验订单状态是否为未支付状态
        GoodsOrder order = getById(orderId);
        if (order == null || order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new CommonException("订单不存在或已支付", INVALID_ARGUMENT);
        }

        // 尝试扣减余额
        boolean deductSuccess = memberService.deductBalance(order.getMemberId(), order.getActualPrice());
        if (!deductSuccess) {
            throw new CommonException("支付失败", TO_BE_SUPPLEMENTED);
        }

        // 支付成功更新订单支付状态
        boolean updateSuccess = lambdaUpdate()
                .set(GoodsOrder::getPaymentStatus, PaymentStatus.PAID)
                .set(GoodsOrder::getPaymentSuccessTime, LocalDateTime.now())
                .eq(GoodsOrder::getId, orderId)
                .eq(GoodsOrder::getPaymentStatus, PaymentStatus.UNPAID)
                .update();
        if (!updateSuccess) {
            throw new CommonException("更新支付状态失败", TO_BE_SUPPLEMENTED);
        }
    }
}
