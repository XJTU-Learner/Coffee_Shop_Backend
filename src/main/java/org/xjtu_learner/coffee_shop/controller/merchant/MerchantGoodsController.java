package org.xjtu_learner.coffee_shop.controller.merchant;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.entity.dto.GoodsDTO;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;
import org.xjtu_learner.coffee_shop.service.impl.ShopGoodsRelationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/merchant/goods")
public class MerchantGoodsController {

    private  final ShopGoodsRelationServiceImpl  shopGoodsRelationService;


    @Autowired
    public MerchantGoodsController(ShopGoodsRelationServiceImpl shopGoodsRelationService) {
        this.shopGoodsRelationService = shopGoodsRelationService;
    }


    @RequestMapping("/list")
    // 定义一个list方法，用于返回商品列表
    public ApiResponse<List<GoodsDTO>> list(){
        // 获取当前商户的id
        int shopid=MerchantContext.get().getShopId();
        List<GoodsDTO> goodsList = shopGoodsRelationService.getAllGoods(shopid);
        // 返回商品列表
        return ApiResponse.success(goodsList);
    }


    @RequestMapping("/soldoutlist")
    // 定义一个list方法，用于返回售空;
    public ApiResponse<List<GoodsDTO>> soldoutlist(){

        // 获取当前商户的id
        int shopid=MerchantContext.get().getShopId();
        List<GoodsDTO> goodsList = shopGoodsRelationService.getSoldOutList(shopid);
        return ApiResponse.success(goodsList);
    }






}
