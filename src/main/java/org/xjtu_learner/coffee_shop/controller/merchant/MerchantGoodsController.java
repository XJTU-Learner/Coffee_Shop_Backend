package org.xjtu_learner.coffee_shop.controller.merchant;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    private final ShopGoodsRelationServiceImpl shopGoodsRelationService;
    private final MerchantRestockServiceImpl merchantRestockService;

    private final GoodsServiceImpl goodsService;


    @Autowired
    public MerchantGoodsController(ShopGoodsRelationServiceImpl shopGoodsRelationService, MerchantRestockServiceImpl merchantRestockService, GoodsServiceImpl goodsService) {
        this.shopGoodsRelationService = shopGoodsRelationService;
        this.merchantRestockService = merchantRestockService;
        this.goodsService = goodsService;
    }


    @GetMapping
    public ApiResponse<PageDTO<ShopGoodsDTO>> getShopGoodsPage(PageQuery pageQuery) {

        int shopId = MerchantContext.get().getShopId();
        PageDTO<ShopGoodsDTO> goodsList = shopGoodsRelationService.getShopGoodsPage(shopId, pageQuery);
        return ApiResponse.success(goodsList);
    }


    @GetMapping("/soldOut")
    public ApiResponse<List<ShopGoodsDTO>> getSoldOutPage() {


        int shopId = MerchantContext.get().getShopId();
        List<ShopGoodsDTO> goodsList = shopGoodsRelationService.getSoldOutList(shopId);
        return ApiResponse.success(goodsList);
    }

    @RequestMapping("/restock")
    // 商家勾选缺货商品进货

    public ApiResponse<String> restock(@RequestBody ReplenishmentForm replenishmentForm) {
        merchantRestockService.sumbitRestock(replenishmentForm);
        return ApiResponse.success("补货申请已成功提交！");
    }

    @RequestMapping("/updatestatus")

    //商家更新商品状态

    public ApiResponse<String> updatestatus(@RequestBody ReplenishmentForm replenishmentForm) {
        merchantRestockService.updateStatus(replenishmentForm);
        return ApiResponse.success("商品状态更新成功！");

    }


    @RequestMapping("/allgoodslists")

    //商家获取现在总司端发售的所有商品

    public ApiResponse<PageDTO<Goods>> getAllgGoodsList(@RequestBody PageQuery pageQuery) {

        return ApiResponse.success(goodsService.getGoodsPage(pageQuery));
    }


    @RequestMapping("/newgoodslists")

    //商家获取发售的新品的所有商品

    public ApiResponse<PageDTO<Goods>> getNewgGoodsList(@RequestBody PageQuery pageQuery) {

        return ApiResponse.success(goodsService.getNewGoodsList(pageQuery));
    }


    @RequestMapping("/addnewgoods")

    //商家添加商品

    public ApiResponse<String> AddNewGoodsList(@RequestParam int goodId) {
        Goods goods = shopGoodsRelationService.addNewGoods(goodId);
        return ApiResponse.success("添加成功!");

    }


}
