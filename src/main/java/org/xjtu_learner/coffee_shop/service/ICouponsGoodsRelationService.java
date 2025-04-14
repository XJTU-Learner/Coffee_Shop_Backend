package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.RelationDTO;
import org.xjtu_learner.coffee_shop.entity.po.CouponsGoodsRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 优惠卷使用规则对应的指定商品，如优惠卷用于‘美国拿铁’ 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface ICouponsGoodsRelationService extends IService<CouponsGoodsRelation> {

    List<RelationDTO> getRelatedGoodsList(Integer id);

    void createRelations(Integer couponsId, List<Integer> plus);

    void deleteRelation(Integer couponsId, List<Integer> subtract);
}
