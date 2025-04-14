package org.xjtu_learner.coffee_shop.controller.merchant;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.service.impl.GoodsServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.MerchantRestockServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.ShopGoodsRelationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/merchant/goods")
public class MerchantGoodsController {

    private  final ShopGoodsRelationServiceImpl  shopGoodsRelationService;
    private  final MerchantRestockServiceImpl  merchantRestockService;

    private  final GoodsServiceImpl goodsService;


    @Autowired
    public MerchantGoodsController(ShopGoodsRelationServiceImpl shopGoodsRelationService, MerchantRestockServiceImpl merchantRestockService, GoodsServiceImpl goodsService) {
        this.shopGoodsRelationService = shopGoodsRelationService;
        this.merchantRestockService = merchantRestockService;
        this.goodsService = goodsService;
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

    @RequestMapping("/restock")
    // 商家勾选缺货商品进货

    public ApiResponse<String> restock(@RequestBody ReplenishmentForm replenishmentForm){
        merchantRestockService.sumbitRestock(replenishmentForm);
        return ApiResponse.success("补货申请已成功提交！");
    }

    @RequestMapping("/updatestatus")

    //商家更新商品状态

    public ApiResponse<String> updatestatus(@RequestBody ReplenishmentForm replenishmentForm){
        merchantRestockService.updateStatus(replenishmentForm);
        return ApiResponse.success("商品状态更新成功！");

    }


    @RequestMapping("/allgoodslists")

    //商家获取现在总司端发售的所有商品

    public  ApiResponse<PageDTO<Goods>> getAllgGoodsList (@RequestBody PageQuery pageQuery){

        return ApiResponse.success(goodsService.getGoodsList(pageQuery));
    }


    @RequestMapping("/newgoodslists")

    //商家获取发售的新品的所有商品

    public  ApiResponse<PageDTO<Goods>> getNewgGoodsList (@RequestBody PageQuery pageQuery){

        return ApiResponse.success(goodsService.getNewGoodsList(pageQuery));
    }


    @RequestMapping("/addnewgoods")

    //商家添加商品

    public  ApiResponse<String> AddNewGoodsList(@RequestParam int goodId) {
        Goods goods = shopGoodsRelationService.addNewGoods(goodId);
        return ApiResponse.success("添加成功!");

    }




}
