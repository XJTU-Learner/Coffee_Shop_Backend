package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.ShopChangeFormDTO;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import org.xjtu_learner.coffee_shop.entity.po.ShopChangeRecord;
import org.xjtu_learner.coffee_shop.dao.ShopChangeRecordMapper;
import org.xjtu_learner.coffee_shop.service.IShopChangeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xjtu_learner.coffee_shop.service.IShopService;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.AUDIT_ONGOING;
import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.INVALID_ARGUMENT;

/**
 * <p>
 * 门店重要信息变更记录表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
@Service
public class ShopChangeRecordServiceImpl extends ServiceImpl<ShopChangeRecordMapper, ShopChangeRecord> implements IShopChangeRecordService {

    private final IShopService shopService;

    public ShopChangeRecordServiceImpl(IShopService shopService) {
        this.shopService = shopService;
    }

    @Override
    public void saveInitRecord(ShopChangeFormDTO formDTO) {
        if (!checkInitForm(formDTO)) throw new CommonException(
                "商铺资料初始化表单不完整，门店资料初始化的必填字段：newNickname、" +
                        "newProvince、newCity、newArea、newStreet、newHouseNumber、newContactRealname、newContactPhone、" +
                        "newBusinessLicense、newOpenTime、newCloseTime、newLongitude和newLatitude\n", INVALID_ARGUMENT);


        saveRecord(formDTO);
    }

    @Override
    public void saveRecord(ShopChangeFormDTO formDTO) {
        Integer id = MerchantContext.get().getId();

        // 判断是否有正在进行的审核
        boolean exists = lambdaQuery()
                .eq(ShopChangeRecord::getMerchantId, id)
                .exists();
        if(exists){
            throw new CommonException("已有正在进行的审核",AUDIT_ONGOING);
        }

        Shop shop = shopService.lambdaQuery().eq(Shop::getId, MerchantContext.get().getShopId()).one();

        ShopChangeRecord record = BeanUtil.copyProperties(formDTO, ShopChangeRecord.class);
        BeanUtil.copyProperties(shop, record, "id");

        record.setMerchantId(id);
        record.setShopId(shop.getId());
        save(record);
    }

    private boolean checkInitForm(ShopChangeFormDTO shopChangeForm) {
        // 检查表单中是否包含门店资料初始化的必填字段：newProvince、newCity、newArea、newStreet、newHouseNumber、
        // newContactRealname、newContactPhone、newBusinessLicense、newOpenTime、newCloseTime、newLongitude和newLatitude
        if (StrUtil.isBlank(shopChangeForm.getNewProvince())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewCity())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewArea())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewStreet())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewHouseNumber())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewContactRealname())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewContactPhone())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewBusinessLicense())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewOpenTime())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewCloseTime())) return false;
        if (shopChangeForm.getNewLongitude() == null) return false;
        if (shopChangeForm.getNewLatitude() == null) return false;

        return true;
    }
}
