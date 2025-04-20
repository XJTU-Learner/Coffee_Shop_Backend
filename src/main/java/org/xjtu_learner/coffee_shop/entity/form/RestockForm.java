package org.xjtu_learner.coffee_shop.entity.form;


import lombok.Data;

import java.util.List;

@Data
public class RestockForm {

    /**
     * 要补货的shop_goods_relation_id
     */
    private List<Integer> restockList;
}
