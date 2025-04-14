package org.xjtu_learner.coffee_shop.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * <p>
 * 商户补货表
 * </p>
 *
 * @author dopichen
 * @since 2025-04-13
 */
@Getter
@Setter
@ToString
@TableName("tb_merchant_restock")
public class MerchantRestock implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的商户ID
     */
    @TableField("merchant_id")
    private Integer merchantId;

    /**
     * 补货列表
     */
    @TableField("restock_list")
    private String restockList;

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

    /**
     * 申请状态
     */
    @TableField("status")
    private Integer status;
}
