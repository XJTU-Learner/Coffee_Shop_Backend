package org.xjtu_learner.coffee_shop.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.xjtu_learner.coffee_shop.common.enums.PreferentialType;
import org.xjtu_learner.coffee_shop.common.enums.TimeLimitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponsDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 优惠卷名称
     */
    private String name;

    /**
     * 优惠类型 1=折扣 2=满减
     */
    private PreferentialType preferentialType;

    /**
     * 折扣额度
     */
    private BigDecimal discountAmount;

    /**
     * 满足价格（元，满足该价格才能使用）
     */
    private BigDecimal limitedPrice;

    /**
     * 减价额度(元)
     */
    private BigDecimal reducedPrice;

    /**
     * 使用规则描述
     */
    private String description;

    /**
     * 积分消耗
     */
    private BigDecimal pointCost;

    /**
     * 是否为秒杀优惠券 0=否 1=是
     */
    private Boolean isSeckill;

    /**
     * 限量库存
     */
    private Integer stock;

    /**
     * 抢购开始时间
     */
    private LocalDateTime seckillStartTime;

    /**
     * 抢购结束时间
     */
    private LocalDateTime seckillEndTime;

    /**
     * 时效 1=绝对时效（领取后XXX-XXX时间段有效） 2=相对时效（领取后N天有效）
     */
    private TimeLimitType validType;

    /**
     * 使用开始时间
     */
    private LocalDateTime validStartTime;

    /**
     * 使用结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 自领取之日起有效天数
     */
    private Integer validDays;


    /**
     * 优惠券发放来源 1=积分兑换 2=活动发放 3=秒杀抢购
     */
    private Integer source;


}
