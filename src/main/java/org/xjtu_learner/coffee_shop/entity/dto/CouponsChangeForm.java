package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

@Data
public class CouponsChangeForm {

    CouponsForm base;

    RelationChangeForm relatedGoodsChange;

    RelationChangeForm relatedShopChange;
}
