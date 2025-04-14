package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.GoodsDTO;
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
    public List<GoodsDTO> getAllGoods(int shopId);

    public List<GoodsDTO> getSoldOutList(int shopId);

    public List<GoodsDTO> transferToGoodsDTOList(List<ShopGoodsRelation> goodsList);

    public Goods addNewGoods(int goodId);

}
