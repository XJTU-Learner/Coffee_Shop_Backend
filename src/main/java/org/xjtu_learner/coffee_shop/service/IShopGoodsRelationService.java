package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.GoodsDTO;
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


    public List<GoodsDTO> transferToGoodsDTOList(List<ShopGoodsRelation> goodsList);

    public Goods addNewGoods(int goodId);

    List<ShopGoodsDTO> getShopGoodsList(Integer shopId);

    PageDTO<ShopGoodsDTO> getShopGoodsPage(Integer shopId, PageQuery pageQuery);

    List<ShopGoodsDTO> getSoldOutList(Integer shopId);

}
