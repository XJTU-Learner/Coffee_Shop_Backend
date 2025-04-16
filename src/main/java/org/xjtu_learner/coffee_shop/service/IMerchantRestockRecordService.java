package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.RestockForm;
import org.xjtu_learner.coffee_shop.entity.po.MerchantRestockRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商户补货表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-16
 */
public interface IMerchantRestockRecordService extends IService<MerchantRestockRecord> {

    void submitRestock(RestockForm restockForm);

}
