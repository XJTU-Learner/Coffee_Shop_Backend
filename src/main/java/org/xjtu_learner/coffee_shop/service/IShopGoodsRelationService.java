package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.dto.PageQuery;
import org.xjtu_learner.coffee_shop.entity.dto.ShopGoodsDTO;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.entity.po.ShopGoodsRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 门店商品关联表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface IShopGoodsRelationService extends IService<ShopGoodsRelation> {

    List<ShopGoodsRelation> getShopGoodsList(Integer shopId);

    PageDTO<ShopGoodsRelation> getShopGoodsPage(Integer shopId, PageQuery pageQuery);

    List<ShopGoodsDTO> getShopGoodsDTOList(List<ShopGoodsRelation> relationList);

    List<ShopGoodsRelation> getSoldOutList(Integer shopId);

    List<ShopGoodsRelation> getSoldOutShopGoodsRelationList(Integer shopId);

    void updateIsSoldOut(Integer shopGoodsRelationId, Boolean isSoldOut);

    void checkExist(Integer shopGoodsRelationId);

    void checkExistBatch(List<Integer> shopGoodsRelationIdList);

    void deleteCache();

    void addGoods(List<Integer> goodIdList);
}
