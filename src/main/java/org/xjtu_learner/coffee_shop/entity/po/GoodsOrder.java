package org.xjtu_learner.coffee_shop.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.xjtu_learner.coffee_shop.common.enums.OrderStatus;
import org.xjtu_learner.coffee_shop.common.enums.PaymentMode;
import org.xjtu_learner.coffee_shop.common.enums.PaymentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * <p>
 * 订单表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_goods_order")
public class GoodsOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField("member_id")
    private Integer memberId;

    /**
     * 接单商家id
     */
    @TableField("merchant_id")
    private Integer merchantId;

    /**
     * 商品总数量
     */
    @TableField("goods_total_quantity")
    private Integer goodsTotalQuantity;

    /**
     * 商品总金额
     */
    @TableField("goods_total_base_amount")
    private BigDecimal goodsTotalBaseAmount;

    /**
     * 商品总金额
     */
    @TableField("goods_total_actual_amount")
    private BigDecimal goodsTotalActualAmount;

    /**
     * 优惠卷用户关系id
     */
    @TableField("coupons_member_relation_id")
    private Integer couponsMemberRelationId;

    /**
     * 优惠券满减金额
     */
    @TableField("coupon_reduced_amount")
    private BigDecimal couponReducedAmount;

    /**
     * 实付款
     */
    @TableField("actual_price")
    private BigDecimal actualPrice;

    /**
     * 获得积分
     */
    @TableField("point_increase")
    private BigDecimal pointIncrease;

    /**
     * 平台抽成总金额
     */
    @TableField("platform_extract_price")
    private BigDecimal platformExtractPrice;

    /**
     * 商家实际收入
     */
    @TableField("merchant_income")
    private BigDecimal merchantIncome;

    /**
     * 订单描述
     */
    @TableField("description")
    private String description;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 取餐号
     */
    @TableField("queue_no")
    private Integer queueNo;

    /**
     * 订单状态 1=未付款 2=制作中3=待自取 4=已完成 5=售后处理中 6=售后处理完成 7=已取消(未支付)
     */
    @TableField("status")
    private OrderStatus status;

    /**
     * 支付方式 1=平台余额 2=微信支付
     */
    @TableField("payment_mode")
    private PaymentMode paymentMode;

    /**
     * 支付截止时间(五分钟内未付款的订单会被自动关闭)
     */
    @TableField("payment_deadline")
    private LocalDateTime paymentDeadline;

    /**
     * 交易状态 1=待支付 2=支付成功 3=支付失败 4=交易超时自动关闭
     */
    @TableField("payment_status")
    private PaymentStatus paymentStatus;

    /**
     * 支付成功时间
     */
    @TableField("payment_success_time")
    private LocalDateTime paymentSuccessTime;

    /**
     * (未支付)订单取消原因 1=用户主动取消 2=订单超时未支付 3=系统异常
     */
    @TableField("cancel_reason")
    private Integer cancelReason;

    /**
     * 订单完成时间
     */
    @TableField("order_completion_time")
    private LocalDateTime orderCompletionTime;


    /**
     * 是否删除 0=正常 1=删除
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 下单时间
     */
    @TableField("create_at")
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    @TableField("update_at")
    private LocalDateTime updateAt;
}
