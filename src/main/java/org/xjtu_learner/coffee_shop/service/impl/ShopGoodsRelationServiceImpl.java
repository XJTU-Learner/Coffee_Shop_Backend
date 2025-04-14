package org.xjtu_learner.coffee_shop.service.impl;

import io.lettuce.core.dynamic.CommandCreationException;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.GoodsDTO;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.entity.po.Merchant;
import org.xjtu_learner.coffee_shop.entity.po.ShopGoodsRelation;
import org.xjtu_learner.coffee_shop.dao.ShopGoodsRelationMapper;
import org.xjtu_learner.coffee_shop.service.IShopGoodsRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.NOT_EXIST;

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


    private  final GoodsServiceImpl goodsService;
    private  final MerchantServiceImpl merchantService;

    public ShopGoodsRelationServiceImpl(GoodsServiceImpl goodsService, MerchantServiceImpl merchantService) {
        this.goodsService = goodsService;
        this.merchantService = merchantService;
    }

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
    //添加商品
    @Override
    public Goods addNewGoods(int goodId) {

        Goods good =goodsService.getById(goodId);
        if(good!=null)
        {
            ShopGoodsRelation temp =lambdaQuery().eq(ShopGoodsRelation::getGoodsId,goodId).one();
            if(temp!=null)
            {
                throw new CommonException("已经存在此商品请勿重复添加",NOT_EXIST);
            }

            ShopGoodsRelation shopGoodsRelation =new ShopGoodsRelation();
            shopGoodsRelation.setShopId(MerchantContext.get().getShopId());
            shopGoodsRelation.setGoodsId(good.getId());
            shopGoodsRelation.setName(good.getName());
            shopGoodsRelation.setImage(good.getImage());
            shopGoodsRelation.setBasePrice(good.getBasePrice());
            shopGoodsRelation.setTag(good.getTag());
            shopGoodsRelation.setIsSoldOut(false);
            save(shopGoodsRelation);

        }
        else {
            throw new CommonException("暂无此商品",NOT_EXIST);
        }
        return  good;

    }


}
