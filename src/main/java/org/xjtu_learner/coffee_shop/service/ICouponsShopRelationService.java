package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.RelationDTO;
import org.xjtu_learner.coffee_shop.entity.po.CouponsShopRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 优惠卷使用店铺范围，优惠卷和店铺关系表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface ICouponsShopRelationService extends IService<CouponsShopRelation> {

    List<RelationDTO> getRelatedShopList(Integer id);

    void createRelations(Integer couponsId, List<Integer> plus);

    void deleteRelation(Integer couponsId, List<Integer> subtract);

    void checkValid(Integer couponsId, Integer shopId);
}
