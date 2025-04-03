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
 * 优惠卷表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_coupons")
public class Coupons implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 优惠卷名称
     */
    @TableField("name")
    private String name;

    /**
     * 优惠类型 1=折扣 2=满减
     */
    @TableField("preferential_type")
    private Integer preferentialType;

    /**
     * 折扣额度
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 满足价格（元，满足该价格才能使用）
     */
    @TableField("limited_price")
    private BigDecimal limitedPrice;

    /**
     * 减价额度(元)
     */
    @TableField("reduced_price")
    private BigDecimal reducedPrice;

    /**
     * 使用规则描述
     */
    @TableField("description")
    private String description;

    /**
     * 积分消耗
     */
    @TableField("point_cost")
    private BigDecimal pointCost;

    /**
     * 是否为秒杀优惠券 0=否 1=是
     */
    @TableField("is_seckill")
    private Boolean isSeckill;

    /**
     * 限量库存
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 抢购开始时间
     */
    @TableField("seckill_start_time")
    private LocalDateTime seckillStartTime;

    /**
     * 抢购结束时间
     */
    @TableField("seckill_end_time")
    private LocalDateTime seckillEndTime;

    /**
     * 时效 1=绝对时效（领取后XXX-XXX时间段有效） 2=相对时效（领取后N天有效）
     */
    @TableField("valid_type")
    private Integer validType;

    /**
     * 使用开始时间
     */
    @TableField("valid_start_time")
    private LocalDateTime validStartTime;

    /**
     * 使用结束时间
     */
    @TableField("valid_end_time")
    private LocalDateTime validEndTime;

    /**
     * 自领取之日起有效天数
     */
    @TableField("valid_days")
    private Integer validDays;

    /**
     * 是否被删除 0=否 1=是
     */
    @TableField("is_delete")
    private Boolean isDelete;

    /**
     * 优惠券发放来源 1=积分兑换 2=活动发放 3=秒杀抢购
     */
    @TableField("source")
    private Integer source;

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
