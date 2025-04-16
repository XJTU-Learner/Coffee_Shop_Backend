package org.xjtu_learner.coffee_shop.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.entity.form.RestockForm;
import org.xjtu_learner.coffee_shop.entity.po.MerchantRestockRecord;
import org.xjtu_learner.coffee_shop.dao.MerchantRestockRecordMapper;
import org.xjtu_learner.coffee_shop.service.IMerchantRestockRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xjtu_learner.coffee_shop.service.IShopGoodsRelationService;

import java.util.List;

/**
 * <p>
 * 商户补货表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-16
 */
@Service
public class MerchantRestockRecordServiceImpl extends ServiceImpl<MerchantRestockRecordMapper, MerchantRestockRecord> implements IMerchantRestockRecordService {

    private final IShopGoodsRelationService shopGoodsRelationService;

    public MerchantRestockRecordServiceImpl(IShopGoodsRelationService shopGoodsRelationService) {
        this.shopGoodsRelationService = shopGoodsRelationService;
    }

    @Override
    @Transactional
    public void submitRestock(RestockForm restockForm) {
        Integer merchantId = MerchantContext.get().getId();
        List<Integer> restockList = restockForm.getRestockList();

        shopGoodsRelationService.checkExistBatch(restockList);

        List<MerchantRestockRecord> toSave = restockList.stream()
                .map((restockId) -> {
                    MerchantRestockRecord record = new MerchantRestockRecord();
                    record.setMerchantId(merchantId);
                    record.setShopGoodsRelationId(restockId);
                    return record;
                })
                .toList();

        saveBatch(toSave);
    }
}
