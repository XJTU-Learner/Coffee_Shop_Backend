package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.ReplenishmentForm;
import org.xjtu_learner.coffee_shop.entity.po.MerchantRestock;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商户补货表 服务类
 * </p>
 *
 * @author dopichen
 * @since 2025-04-13
 */
public interface IMerchantRestockService extends IService<MerchantRestock> {

    public void  sumbitRestock(ReplenishmentForm replenishmentForm);

    public void updateStatus(ReplenishmentForm replenishmentForm);

}
