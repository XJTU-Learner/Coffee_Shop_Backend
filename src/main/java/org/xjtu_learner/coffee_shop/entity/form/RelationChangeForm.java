package org.xjtu_learner.coffee_shop.entity.form;

import lombok.Data;

import java.util.List;

@Data
public class RelationChangeForm {

    /**
     * 新增关系
     */
    List<Integer> plus;

    /**
     * 删除关系
     */
    List<Integer> subtract;
}
