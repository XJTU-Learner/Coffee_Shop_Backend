package org.xjtu_learner.coffee_shop.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * <p>
 * 商户补货表
 * </p>
 *
 * @author dopichen
 * @since 2025-04-16
 */
@Getter
@Setter
@ToString
@TableName("tb_merchant_restock_record")
public class MerchantRestockRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的商户id
     */
    @TableField("merchant_id")
    private Integer merchantId;

    /**
     * 补货商品id
     */
    @TableField("shop_goods_relation_id")
    private Integer shopGoodsRelationId;

    /**
     * 审核员
     */
    @TableField("auditor")
    private Integer auditor;

    /**
     * 审批状态 0=审核中 1=审核成功 2=审核失败
     */
    @TableField("audit_status")
    private Integer auditStatus;

    /**
     * 审批失败原因
     */
    @TableField("audit_reason")
    private String auditReason;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

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
