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
 * 订单商品详情表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_goods_order_detail")
public class GoodsOrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Integer orderId;

    /**
     * 商品id
     */
    @TableField("goods_id")
    private Integer goodsId;

    /**
     * 购买数量
     */
    @TableField("count")
    private Integer count;

    /**
     * 该商品是否使用了优惠券 0=否 1=是
     */
    @TableField("is_used_coupons")
    private Boolean isUsedCoupons;

    /**
     * 优惠券折扣金额/优惠券满减金额
     */
    @TableField("coupons_discount_price")
    private BigDecimal couponsDiscountPrice;

    /**
     * 实付款
     */
    @TableField("actual_price")
    private BigDecimal actualPrice;

    /**
     * 是否删除 0=正常 1=删除
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

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
