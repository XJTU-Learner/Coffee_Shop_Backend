package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ShopGoodsDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 商品信息
     */
    private GoodsDTO goodsInfo;

    /**
     * 是否售空
     */
    private Boolean isSoldOut;
}
