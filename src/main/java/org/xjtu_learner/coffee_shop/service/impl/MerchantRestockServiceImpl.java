package org.xjtu_learner.coffee_shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.entity.dto.ReplenishmentForm;
import org.xjtu_learner.coffee_shop.entity.po.Merchant;
import org.xjtu_learner.coffee_shop.entity.po.MerchantRestock;
import org.xjtu_learner.coffee_shop.dao.MerchantRestockMapper;
import org.xjtu_learner.coffee_shop.entity.po.ShopGoodsRelation;
import org.xjtu_learner.coffee_shop.service.IMerchantRestockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商户补货表 服务实现类
 * </p>
 *
 * @author dopichen
 * @since 2025-04-13
 */
@Service
public class MerchantRestockServiceImpl extends ServiceImpl<MerchantRestockMapper, MerchantRestock> implements IMerchantRestockService {


    private final ShopGoodsRelationServiceImpl shopGoodsRelationService;

    public MerchantRestockServiceImpl(ShopGoodsRelationServiceImpl shopGoodsRelationService) {
        this.shopGoodsRelationService = shopGoodsRelationService;
    }

    //保存进货提交记录
    public void  sumbitRestock(ReplenishmentForm  replenishmentForm){
        MerchantRestock merchantRestock =new MerchantRestock();
        merchantRestock.setMerchantId(MerchantContext.get().getId());
        merchantRestock.setRestockList(String.join(",",replenishmentForm.getGoodsList()));
        merchantRestock.setStatus(0);
        save(merchantRestock);
    }

    //更新商品状态
    public void updateStatus(ReplenishmentForm replenishmentForm){
        //获取将要更新的商品列表
        List<Integer> goodsList =new ArrayList<>();
        for(String goodsId:replenishmentForm.getGoodsList()){
            goodsList.add(Integer.parseInt(goodsId));
        }
        ShopGoodsRelation shopGoodsRelation =new ShopGoodsRelation();
        UpdateWrapper<ShopGoodsRelation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("shop_id",MerchantContext.get().getShopId())
        .in("goods_id",goodsList)
                .set("is_sold_out",false);

        shopGoodsRelationService.update(shopGoodsRelation,updateWrapper);



    }
}
