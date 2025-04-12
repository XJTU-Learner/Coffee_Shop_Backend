package org.xjtu_learner.coffee_shop.service.impl;

import org.xjtu_learner.coffee_shop.entity.dto.GoodsDTO;
import org.xjtu_learner.coffee_shop.entity.po.Merchant;
import org.xjtu_learner.coffee_shop.entity.po.ShopGoodsRelation;
import org.xjtu_learner.coffee_shop.dao.ShopGoodsRelationMapper;
import org.xjtu_learner.coffee_shop.service.IShopGoodsRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 门店商品关联表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class ShopGoodsRelationServiceImpl extends ServiceImpl<ShopGoodsRelationMapper, ShopGoodsRelation> implements IShopGoodsRelationService {


    MerchantServiceImpl merchantService;
    //获取全部在售全部商品列表
    public List<GoodsDTO> getAllGoods(int shopId){
        List<ShopGoodsRelation> goodsList = lambdaQuery().eq(
                ShopGoodsRelation::getShopId,shopId
        ).list();
       return transferToGoodsDTOList(goodsList);
    }

    //获取全部售空商品列表
    public List<GoodsDTO> getSoldOutList(int shopId){
        List<ShopGoodsRelation>  goodsList = lambdaQuery().eq(ShopGoodsRelation::getShopId,shopId)
                .eq(ShopGoodsRelation::getIsSoldOut,true).list();

        return transferToGoodsDTOList(goodsList);

    }


    //转化成GoodsDTO列表
    public List<GoodsDTO> transferToGoodsDTOList(List<ShopGoodsRelation> goodsList){
        List<GoodsDTO> goodsDTOList = new ArrayList<>();
        for(ShopGoodsRelation goods:goodsList){
            GoodsDTO goodsDTO = new GoodsDTO();
            goodsDTO.setId(goods.getGoodsId());
            goodsDTO.setName(goods.getName());
            goodsDTO.setImage(goods.getImage());
            goodsDTO.setBasePrice(String.valueOf(goods.getBasePrice()));
            goodsDTO.setTag(goods.getTag());
            goodsDTO.setSoldOut(false);
            goodsDTOList.add(goodsDTO);
        }
        return  goodsDTOList;
    }



}
