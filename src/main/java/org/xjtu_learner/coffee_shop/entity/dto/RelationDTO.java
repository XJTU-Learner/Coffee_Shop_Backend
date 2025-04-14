package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

@Data
public class RelationDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 主对象id
     */
    private Integer majorId;

    /**
     * 从对象id
     */
    private Integer minorId;
}
