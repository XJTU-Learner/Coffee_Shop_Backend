package org.xjtu_learner.coffee_shop.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;
import org.xjtu_learner.coffee_shop.common.enums.PreferentialType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class CouponsMemberDTO {

    /**
     * 主键id -
     */
    private Integer id;

    /**
     * 用户id -
     */
    private Integer memberId;

    /**
     * 优惠卷id -
     */
    private Integer couponsId;

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
     * 生效时间 -
     */
    private LocalDateTime startTime;

    /**
     * 过期时间 -
     */
    private LocalDateTime endTime;

    /**
     * 是否已经使用，0=未使用，1=已使用 -
     */
    private Boolean isUsed;

    /**
     * 是否过期，0=未过期，1=已过期 -
     */
    private Boolean isExpired;

    /**
     * 是否有效，0=否，1=是 -
     */
    private Boolean isValid;

    /**
     * 优惠券发放来源 1=积分兑换 2=活动发放 3=秒杀抢购
     */
    private Integer source;

}
