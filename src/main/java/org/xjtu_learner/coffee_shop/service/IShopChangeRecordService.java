package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.po.ShopChangeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 门店重要信息变更记录表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
public interface IShopChangeRecordService extends IService<ShopChangeRecord> {

    void saveInitRecord(ShopChangeForm shopChangeForm);

    void saveRecord(ShopChangeForm formDTO);

    PageDTO<ShopChangeRecordDTO> getChangeShopList(PageQuery pageQuery);

    void auditChangeShop(AuditChangeForm form);
}
