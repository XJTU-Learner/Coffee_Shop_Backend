package org.xjtu_learner.coffee_shop.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.form.AuditChangeForm;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import org.xjtu_learner.coffee_shop.service.IMerchantChangeRecordService;
import org.xjtu_learner.coffee_shop.service.IShopChangeRecordService;
import org.xjtu_learner.coffee_shop.service.IShopService;

import java.util.List;

@RestController
@RequestMapping("/admin/merchantManagement")
public class AdminMerchantManagementController {

    private final IShopService shopService;
    private final IMerchantChangeRecordService merchantChangeRecordService;
    private final IShopChangeRecordService shopChangeRecordService;

    public AdminMerchantManagementController(IShopService shopService, IMerchantChangeRecordService merchantChangeRecordService, IShopChangeRecordService shopChangeRecordService) {
        this.shopService = shopService;
        this.merchantChangeRecordService = merchantChangeRecordService;
        this.shopChangeRecordService = shopChangeRecordService;
    }

    @GetMapping("/merchantProfile")
    public ApiResponse<PageDTO<MerchantChangeRecordDTO>> getChangeProfileList(PageQuery pageQuery) {

        PageDTO<MerchantChangeRecordDTO> changeProfilePage = merchantChangeRecordService.getChangeProfileList(pageQuery);
        return ApiResponse.success(changeProfilePage);
    }


    @GetMapping("/shopProfile")
    public ApiResponse<PageDTO<ShopChangeRecordDTO>> getChangeShopList(PageQuery pageQuery){

        PageDTO<ShopChangeRecordDTO> changeShopList = shopChangeRecordService.getChangeShopList(pageQuery);
        return ApiResponse.success(changeShopList);
    }

    @PostMapping("/merchantProfile")
    public ApiResponse<String> auditChangeProfile(@RequestBody AuditChangeForm form){

        merchantChangeRecordService.auditChangeProfile(form);
        return ApiResponse.success("审核成功！");
    }

    @PostMapping("/shopProfile")
    public ApiResponse<String> auditChangeShop(@RequestBody AuditChangeForm form){

        shopChangeRecordService.auditChangeShop(form);
        return ApiResponse.success("审核成功！");

    }

    @GetMapping("/shop/all")
    public ApiResponse<PageDTO<ShopDTO>> getShopsPage(PageQuery pageQuery){

        PageDTO<Shop> shopPage = shopService.getShopPage(pageQuery);

        List<Shop> shopList = shopPage.getList();
        List<ShopDTO> shopDTOList = shopList.stream()
                .map((po) -> (BeanUtil.copyProperties(po, ShopDTO.class)))
                .toList();

        PageDTO<ShopDTO> page = PageDTO.<ShopDTO>builder()
                .total(shopPage.getTotal())
                .pages(shopPage.getPages())
                .list(shopDTOList)
                .build();

        return ApiResponse.success(page);
    }

}
