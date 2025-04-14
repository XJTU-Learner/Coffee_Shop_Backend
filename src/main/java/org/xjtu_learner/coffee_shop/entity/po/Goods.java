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
 * 商品表
 * </p >
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_goods")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 商品图片
     */
    @TableField("image")
    private String image;

    /**
     * 商品详情
     */
    @TableField("detail")
    private String detail;

    /**
     * 基础价格
     */
    @TableField("base_price")
    private BigDecimal basePrice;

    /**
     * 平台抽成比例
     */
    @TableField("platform_extract_ratio")
    private BigDecimal platformExtractRatio;

    /**
     * 分类标签
     */
    @TableField("tag")
    private String tag;

    /**
     * 是否热门 0=否 1=是
     */
    @TableField("is_hot")
    private Boolean isHot;

    /**
     * 是否新品 0=否 1=是
     */
    @TableField("is_new")
    private Boolean isNew;

    /**
     * 出售状态 0=已上架 1=已下架
     */
    @TableField("for_sale")
    private Boolean forSale;

    /**
     * 下单选项
     */
    @TableField("options")
    private String options;

    /**
     * 在售门店数
     */
    @TableField("for_sale_count")
    private Integer forSaleCount;

    /**
     * 累计销量
     */
    @TableField("total_sales")
    private Integer totalSales;

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