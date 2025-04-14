package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.enums.PreferentialType;
import org.xjtu_learner.coffee_shop.common.enums.TimeLimitType;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.CouponsForm;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.dto.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.*;
import org.xjtu_learner.coffee_shop.dao.CouponsMapper;
import org.xjtu_learner.coffee_shop.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.INVALID_ARGUMENT;
import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.UPDATE_FAILED;

/**
 * <p>
 * 优惠卷表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class CouponsServiceImpl extends ServiceImpl<CouponsMapper, Coupons> implements ICouponsService {

    private final IGoodsService goodsService;
    private final IShopService shopService;
    private final ICouponsGoodsRelationService couponsGoodsRelationService;
    private final ICouponsShopRelationService couponsShopRelationService;

    public CouponsServiceImpl(IGoodsService goodsService, IShopService shopService, ICouponsGoodsRelationService couponsGoodsRelationService, ICouponsShopRelationService couponsShopRelationService) {
        this.goodsService = goodsService;
        this.shopService = shopService;
        this.couponsGoodsRelationService = couponsGoodsRelationService;
        this.couponsShopRelationService = couponsShopRelationService;
    }

    @Override
    public PageDTO<Coupons> getCouponsList(PageQuery pageQuery) {
        Page<Coupons> page = lambdaQuery()
                .page(pageQuery.toMpPage(pageQuery.getSortBy(), pageQuery.getIsAsc()));

        return PageDTO.of(page, Coupons.class);
    }

    @Override
    @Transactional
    public void createCoupons(CouponsForm form) {
        Coupons coupons = new Coupons();

        checkBeforeCreate(form, coupons);
        checkAndSet(form, coupons);

        save(coupons);

        Integer couponsId = coupons.getId();
        couponsGoodsRelationService.createRelations(couponsId, form.getRelatedGoods());
        couponsShopRelationService.createRelations(couponsId, form.getRelatedShop());
    }


    private void checkBeforeCreate(CouponsForm form, Coupons coupons) {
        if (form.getId() != null) {
            throw new CommonException("不需要指定主键id", INVALID_ARGUMENT);
        }
        if (StrUtil.isBlank(form.getName())) {
            throw new CommonException("未指定优惠券名称或为空", INVALID_ARGUMENT);
        }
        coupons.setName(form.getName());
        if (form.getPreferentialType() == null) {
            throw new CommonException("未指定优惠券优惠类型", INVALID_ARGUMENT);
        }
        if (form.getPointCost() == null) {
            throw new CommonException("未指定优惠券积分价格", INVALID_ARGUMENT);
        }
        coupons.setPointCost(form.getPointCost());
        if (form.getValidType() == null) {
            throw new CommonException("未指定优惠券时限类型", INVALID_ARGUMENT);
        }
        if (form.getSource() == null) {
            throw new CommonException("未指定优惠券来源", INVALID_ARGUMENT);
        }
        coupons.setSource(form.getSource());
        coupons.setDiscountAmount(form.getDiscountAmount());
    }

    private static void checkAndSet(CouponsForm form, Coupons coupons) {
        if (form.getPreferentialType() != null) {
            coupons.setPreferentialType(form.getPreferentialType());
            if (form.getPreferentialType() == PreferentialType.DISCOUNT) {
                if (form.getDiscountAmount() == null)
                    throw new CommonException("优惠类型为'折扣'但discountAmount参数为空", INVALID_ARGUMENT);
                coupons.setDiscountAmount(form.getDiscountAmount());
            }
            if (form.getPreferentialType() == PreferentialType.REDUCTION) {
                if (form.getLimitedPrice() == null || form.getReducedPrice() == null)
                    throw new CommonException("优惠类型为'满减'但limitedPrice或reducedPrice参数为空", INVALID_ARGUMENT);
                coupons.setLimitedPrice(form.getLimitedPrice());
                coupons.setReducedPrice(form.getReducedPrice());
            }
        }

        if (form.getIsSeckill() != null) {
            coupons.setIsSeckill(form.getIsSeckill());
            if (form.getIsSeckill()) {
                if (form.getStock() == null || form.getSeckillStartTime() == null || form.getSeckillEndTime() == null)
                    throw new CommonException("为秒杀优惠卷但是stock、seckillStartTime和seckillEndTime参数不完整", INVALID_ARGUMENT);
                coupons.setStock(form.getStock());
                coupons.setSeckillStartTime(form.getSeckillStartTime());
                coupons.setSeckillEndTime(form.getSeckillEndTime());
            }
        }

        if (form.getValidType() != null) {
            coupons.setValidType(form.getValidType());
            if (form.getValidType() == TimeLimitType.ABSOLUTE) {
                if (form.getValidStartTime() == null || form.getValidEndTime() == null)
                    throw new CommonException("时效类型为绝对时效但是validStartTime和validEndTime参数不完整", INVALID_ARGUMENT);
                coupons.setValidStartTime(form.getValidStartTime());
                coupons.setValidEndTime(form.getValidEndTime());
            }

            if (form.getValidType() == TimeLimitType.RELATIVE) {
                coupons.setValidType(form.getValidType());
                if (form.getValidDays() == null) {
                    throw new CommonException("时效类型为相对时效但是validDays参数为空", INVALID_ARGUMENT);
                }
                coupons.setValidStartTime(LocalDateTime.now());
                coupons.setValidEndTime(LocalDateTime.now().plusDays(form.getValidDays()));
            }
        }
    }

    @Override
    @Transactional
    public void updateCoupons(CouponsForm form) {
        if (form.getId() == null) {
            throw new CommonException("未指定指定主键id", INVALID_ARGUMENT);
        }
        Coupons coupons = new Coupons();

        if (StrUtil.isNotBlank(form.getName())) coupons.setName(form.getName());
        coupons.setDescription(form.getDescription());
        coupons.setPointCost(form.getPointCost());
        coupons.setSource(form.getSource());
        coupons.setUpdateAt(LocalDateTime.now());

        checkAndSet(form, coupons);
        boolean success = lambdaUpdate()
                .eq(Coupons::getId, form.getId())
                .update(coupons);
        if (!success) {
            throw new CommonException("更新失败，可能原因：该优惠券不存在", UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteCoupons(Integer id) {
        if (id == null) {
            throw new CommonException("未指定指定主键id", INVALID_ARGUMENT);
        }
        boolean success = lambdaUpdate()
                .set(Coupons::getIsDelete, true)
                .set(Coupons::getUpdateAt, LocalDateTime.now())
                .eq(Coupons::getIsDelete, false)
                .eq(Coupons::getId, id)
                .update();

        if (!success) {
            throw new CommonException("删除失败，可能原因：该优惠券已经删除或不存在", UPDATE_FAILED);
        }
    }


}
