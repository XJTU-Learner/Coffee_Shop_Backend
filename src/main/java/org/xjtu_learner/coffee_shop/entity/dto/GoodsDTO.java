package org.xjtu_learner.coffee_shop.entity.dto;


import lombok.Data;

@Data
public class GoodsDTO {

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
     * 基础价格
     */
    private String basePrice;
    /**
     * 分类标签
     */
    private String tag;

    private boolean isSoldOut;


}
