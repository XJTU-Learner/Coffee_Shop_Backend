package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.po.MerchantChangeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商户重要信息变更表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
public interface IMerchantChangeRecordService extends IService<MerchantChangeRecord> {

    void saveRecord(MerchantChangeForm formDTO);

    void saveInitRecord(MerchantChangeForm formDTO);

    PageDTO<MerchantChangeRecordDTO> getChangeProfileList(PageQuery pageQuery);

    void auditChangeProfile(AuditChangeForm form);
}
