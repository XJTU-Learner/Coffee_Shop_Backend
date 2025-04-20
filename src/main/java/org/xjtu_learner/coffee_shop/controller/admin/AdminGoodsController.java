package org.xjtu_learner.coffee_shop.controller.admin;

import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.form.GoodsForm;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.service.IGoodsService;

@RestController
@RequestMapping("/admin/goods")
public class AdminGoodsController {

    private final IGoodsService goodsService;

    public AdminGoodsController(IGoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping
    public ApiResponse<PageDTO<Goods>> getGoodsPage(PageQuery pageQuery){
        PageDTO<Goods> goodsList = goodsService.getGoodsPage(pageQuery);
        return ApiResponse.success(goodsList);
    }

    @PostMapping
    public ApiResponse<String> createGoods(@RequestBody GoodsForm form){
        goodsService.createGoods(form);
        return ApiResponse.success("商品创建成功！");
    }

    @PutMapping
    public ApiResponse<String> updateGoods(@RequestBody GoodsForm form){
        goodsService.updateGoods(form);
        return ApiResponse.success("商品更新成功！");
    }

    @DeleteMapping
    public ApiResponse<String> deleteGoods(@RequestParam("goodsId") Integer id){
        goodsService.deleteGoods(id);
        return ApiResponse.success("商品下架成功！");
    }

}
