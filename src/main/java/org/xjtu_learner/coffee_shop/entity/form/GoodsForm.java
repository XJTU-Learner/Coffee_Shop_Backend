package org.xjtu_learner.coffee_shop.entity.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsForm {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品详情
     */
    private String detail;

    /**
     * 基础价格
     */
    private BigDecimal basePrice;

    /**
     * 平台抽成比例
     */
    private BigDecimal platformExtractRatio;

    /**
     * 分类标签
     */
    private String tag;

    /**
     * 是否热门 0=否 1=是
     */
    private Boolean isHot;

    /**
     * 是否新品 0=否 1=是
     */
    private Boolean isNew;

    /**
     * 下单选项
     */
    private String options;
}
