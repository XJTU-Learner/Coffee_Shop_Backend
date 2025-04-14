package org.xjtu_learner.coffee_shop.entity.dto;


import lombok.Data;

import java.util.List;

//补货表单
@Data
public class ReplenishmentForm {

    private  Integer shopId;

    private Integer merchantId;
    //要进货的商品id
    private List<String> goodsList;
}
