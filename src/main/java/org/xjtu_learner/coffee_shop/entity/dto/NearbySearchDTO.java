package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NearbySearchDTO <D>{

    /**
     * 数据
     */
    D dto;

    /**
     * 距离
     */
    String distance;
    
}
