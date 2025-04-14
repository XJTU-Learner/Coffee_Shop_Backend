package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.MemberBillDTO;
import org.xjtu_learner.coffee_shop.entity.po.MerchantBillingRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商家账单记录表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface IMerchantBillingRecordService extends IService<MerchantBillingRecord> {
    public List<MemberBillDTO> get_memberbill(String timeRange);

}
