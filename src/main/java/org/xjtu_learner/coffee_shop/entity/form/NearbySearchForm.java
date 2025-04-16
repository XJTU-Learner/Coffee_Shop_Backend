package org.xjtu_learner.coffee_shop.entity.form;

import lombok.Data;

@Data
public class NearbySearchForm {

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 半径
     */
    private Double radius = 10.0; // km

}
