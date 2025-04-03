package org.xjtu_learner.coffee_shop.entity.po;

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
 * 
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_member_point_consume_record")
public class MemberPointConsumeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId("id")
    private Integer id;

    /**
     * 用户id
     */
    @TableField("member_id")
    private Integer memberId;

    /**
     * 兑换的优惠券id
     */
    @TableField("coupons_id")
    private Integer couponsId;

    /**
     * 消耗积分
     */
    @TableField("point_cost")
    private BigDecimal pointCost;

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
