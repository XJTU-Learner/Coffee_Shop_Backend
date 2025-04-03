package org.xjtu_learner.coffee_shop.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * <p>
 * 用户账单记录表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_member_billing_record")
public class MemberBillingRecord implements Serializable {

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
     * 订单id
     */
    @TableField("order_id")
    private Integer orderId;

    /**
     * 账单类型 1=订单使用余额支付  2=下单奖励积分 3=积分兑换优惠券 4=用户订单使用优惠券 5=用户申请退款-余额退回 7=订单退款-下单奖励积分退回 8=订单退款-使用优惠券退回 9=新用户注册赠送积分 10=充值余额
     */
    @TableField("type")
    private Integer type;

    /**
     * 货币类型 1=积分 2=余额 3=优惠券
     */
    @TableField("coin_type")
    private Integer coinType;

    /**
     * 操作类型 1=加 2=减
     */
    @TableField("operate_type")
    private Integer operateType;

    /**
     * 增减的数值
     */
    @TableField("number")
    private BigDecimal number;

    /**
     * 账单信息
     */
    @TableField("message")
    private String message;

    /**
     * 创建时间
     */
    @TableField("create_at")
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    @TableField("update_at")
    private LocalDateTime updateAt;
}
