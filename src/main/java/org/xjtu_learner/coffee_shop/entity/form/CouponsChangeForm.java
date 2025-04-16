package org.xjtu_learner.coffee_shop.entity.form;

import lombok.Data;

@Data
public class CouponsChangeForm {

    CouponsForm base;

    RelationChangeForm relatedGoodsChange;

    RelationChangeForm relatedShopChange;
}
