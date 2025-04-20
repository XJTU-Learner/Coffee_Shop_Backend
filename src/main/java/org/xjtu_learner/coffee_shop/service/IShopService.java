package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.form.NearbySearchForm;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import com.baomidou.mybatisplus.extension.service.IService;
import org.xjtu_learner.coffee_shop.entity.po.ShopChangeRecord;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 门店表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
public interface IShopService extends IService<Shop> {

    boolean checkShopIdValid(Integer shopId);

    boolean checkShopIdValidBatch(List<Integer> shopIdList);

    List<Shop> getShopList(List<Integer> shopIdList);

    PageDTO<Shop> getShopPage(PageQuery pageQuery);

    Map<Shop, String> getNearbyShop(NearbySearchForm form);

    void updateShop(ShopChangeRecord record);

    void setShopNickname(Integer merchantId, String newNickname);
}
