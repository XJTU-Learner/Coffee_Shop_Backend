package org.xjtu_learner.coffee_shop.controller.merchant;


import cn.hutool.core.bean.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.form.RestockForm;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.entity.po.ShopGoodsRelation;
import org.xjtu_learner.coffee_shop.service.IMerchantRestockRecordService;
import org.xjtu_learner.coffee_shop.service.impl.GoodsServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.ShopGoodsRelationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/merchant/goods")
public class MerchantGoodsController {

    private final ShopGoodsRelationServiceImpl shopGoodsRelationService;
    private final IMerchantRestockRecordService merchantRestockRecordService;

    private final GoodsServiceImpl goodsService;


    @Autowired
    public MerchantGoodsController(ShopGoodsRelationServiceImpl shopGoodsRelationService, IMerchantRestockRecordService merchantRestockRecordService, GoodsServiceImpl goodsService) {
        this.shopGoodsRelationService = shopGoodsRelationService;
        this.merchantRestockRecordService = merchantRestockRecordService;
        this.goodsService = goodsService;
    }


    @GetMapping
    public ApiResponse<PageDTO<ShopGoodsDTO>> getShopGoodsPage(PageQuery pageQuery) {

        int shopId = MerchantContext.get().getShopId();
        PageDTO<ShopGoodsRelation> goodsList = shopGoodsRelationService.getShopGoodsPage(shopId, pageQuery);
        List<ShopGoodsDTO> shopGoodsDTOList = shopGoodsRelationService.getShopGoodsDTOList(goodsList.getList());

        PageDTO<ShopGoodsDTO> pageDTO = PageDTO.<ShopGoodsDTO>builder()
                .total(goodsList.getTotal())
                .pages(goodsList.getPages())
                .list(shopGoodsDTOList)
                .build();

        return ApiResponse.success(pageDTO);
    }


    @GetMapping("/soldOut")
    public ApiResponse<List<ShopGoodsDTO>> getSoldOutPage() {

        int shopId = MerchantContext.get().getShopId();
        List<ShopGoodsRelation> goodsList = shopGoodsRelationService.getSoldOutList(shopId);
        List<ShopGoodsDTO> shopGoodsDTOList = shopGoodsRelationService.getShopGoodsDTOList(goodsList);

        return ApiResponse.success(shopGoodsDTOList);
    }


    @PostMapping("/restock")
    public ApiResponse<String> submitRestock(@RequestBody RestockForm restockForm) {
        merchantRestockRecordService.submitRestock(restockForm);
        return ApiResponse.success("补货申请已成功提交！");
    }

    @PutMapping("/isSoldOut")
    public ApiResponse<String> updateIsSoldOut(@RequestParam("id") Integer id,@RequestParam("isSoldOut") Boolean isSoldOut) {
        shopGoodsRelationService.updateIsSoldOut(id, isSoldOut);
        return ApiResponse.success("商品状态更新成功！");

    }


    @GetMapping("/all")
    public ApiResponse<PageDTO<GoodsDTO>> getAllGoods(PageQuery pageQuery) {

        PageDTO<Goods> goodsPage = goodsService.getGoodsPage(pageQuery);
        List<Goods> goodsList = goodsPage.getList();
        List<GoodsDTO> goodsDTOList = goodsList.stream()
                .map((po) -> (BeanUtil.copyProperties(po, GoodsDTO.class)))
                .toList();

        PageDTO<GoodsDTO> page = PageDTO.<GoodsDTO>builder()
                .total(goodsPage.getTotal())
                .pages(goodsPage.getPages())
                .list(goodsDTOList)
                .build();

        return ApiResponse.success(page);
    }


    @GetMapping("/new")
    public ApiResponse<PageDTO<GoodsDTO>> getNewGoodsPage(PageQuery pageQuery) {
        PageDTO<Goods> newGoodsPage = goodsService.getNewGoodsList(pageQuery);
        List<Goods> goodsList = newGoodsPage.getList();
        List<GoodsDTO> goodsDTOList = goodsList.stream()
                .map((po) -> (BeanUtil.copyProperties(po, GoodsDTO.class)))
                .toList();

        PageDTO<GoodsDTO> page = PageDTO.<GoodsDTO>builder()
                .total(newGoodsPage.getTotal())
                .pages(newGoodsPage.getPages())
                .list(goodsDTOList)
                .build();

        return ApiResponse.success(page);
    }


    @PostMapping("/add")
    public ApiResponse<String> AddNewGoodsList(@RequestBody List<Integer> goodIdList) {
        shopGoodsRelationService.addGoods(goodIdList);
        return ApiResponse.success("添加成功!");

    }
}
