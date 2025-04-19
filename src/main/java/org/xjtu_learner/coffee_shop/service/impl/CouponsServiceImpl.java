package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.enums.PreferentialType;
import org.xjtu_learner.coffee_shop.common.enums.TimeLimitType;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.utils.BloomFilterUtil;
import org.xjtu_learner.coffee_shop.entity.form.CouponsForm;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.*;
import org.xjtu_learner.coffee_shop.dao.CouponsMapper;
import org.xjtu_learner.coffee_shop.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.*;
import static org.xjtu_learner.coffee_shop.common.utils.BloomFilterUtil.*;

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

    private RBloomFilter<String> bloomFilter;
    private final BloomFilterUtil bloomFilterUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final ICouponsGoodsRelationService couponsGoodsRelationService;
    private final ICouponsShopRelationService couponsShopRelationService;

    public CouponsServiceImpl(StringRedisTemplate stringRedisTemplate, BloomFilterUtil bloomFilterUtil, ICouponsGoodsRelationService couponsGoodsRelationService, ICouponsShopRelationService couponsShopRelationService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.bloomFilterUtil = bloomFilterUtil;
        this.couponsGoodsRelationService = couponsGoodsRelationService;
        this.couponsShopRelationService = couponsShopRelationService;
    }

    @PostConstruct
    void init() {
        // 预热缓存
        initCache();

        // 初始化布隆过滤器
        initBloomFilter();
    }

    private void initBloomFilter() {
        this.bloomFilter = bloomFilterUtil.getBloomFilter(BLOOMFILTER_COUPONS, BLOOMFILTER_COUPONS_SIZE, BLOOMFILTER_COUPONS_FPP);
        List<String> couponsIdList = lambdaQuery()
                .select(Coupons::getId)
                .list()
                .stream()
                .map(Coupons::getId)
                .map(Object::toString)
                .toList();

        bloomFilter.add(couponsIdList);
    }

    private void initCache() {

        // 缓存预热
        List<Coupons> toCaChe = lambdaQuery()
                .eq(Coupons::getIsDeleted, false)
                .list();

        Map<String, String> toCaCheString = toCaChe.stream()
                .collect(Collectors.toMap(
                        (coupons) -> (CACHE_COUPONS_PREFIX + coupons.getId().toString()),
                        JSONUtil::toJsonStr
                ));
        // MSET批量插入
        stringRedisTemplate.opsForValue().multiSet(toCaCheString);

    }

    @Override
    public Coupons getCoupons(Integer couponsId) {
        if (!checkGoodsIdValid(couponsId)) {
            throw new CommonException("有不存在的couponsId", NOT_EXIST);
        }

        String key = CACHE_COUPONS_PREFIX + couponsId;

        String json = stringRedisTemplate.opsForValue().get(key);
        // 缓存命中
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, Coupons.class);
        }
        // 缓存未命中
        Coupons toCache = lambdaQuery()
                .eq(Coupons::getId, couponsId)
                .eq(Coupons::getIsDeleted, false)
                .one();

        if (toCache == null) {
            throw new CommonException("该优惠券不存在或已下架", NOT_EXIST);
        }
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(toCache));
        return toCache;
    }

    @Override
    public List<Coupons> getCouponsList(List<Integer> couponsIdList) {

        if (!checkGoodsIdValidBatch(couponsIdList)) {
            throw new CommonException("有不存在的couponsId", NOT_EXIST);
        }

        String keyPrefix = CACHE_COUPONS_PREFIX;
        // 构造出List<String>的keyList，用于MGET批量查询
        List<String> keyList = couponsIdList.stream()
                .map(Object::toString)
                .map(key -> String.format("%s%s", keyPrefix, key))
                .toList();

        // MGET批量从缓存中获取商品对象
        Map<Integer, Coupons> cachedCoupons = Objects.requireNonNull(stringRedisTemplate.opsForValue().multiGet(keyList))
                .stream()
                .filter(Objects::nonNull)
                .map((json) -> JSONUtil.toBean(json, Coupons.class))
                .collect(Collectors.toMap(Coupons::getId, po -> po));

        // 组装没有命中的商品ID
        List<Integer> notHitIdList = couponsIdList.stream()
                .filter(couponsId -> !cachedCoupons.containsKey(couponsId))
                .toList();

        Map<Integer, Coupons> notHitGoods;
        if (CollectionUtil.isNotEmpty(notHitIdList)) {
            // 批量从数据库查询未命中的商品信息列表
            notHitGoods = lambdaQuery()
                    .in(Coupons::getId, notHitIdList)
                    .eq(Coupons::getIsDeleted, false)  // 如果已被删除的优惠券将不会被加入缓存
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Coupons::getId, po -> po));

            // 将未命中的优惠券重新加载到缓存
            Map<String, String> toCacheString = notHitGoods.entrySet().stream()
                    .collect(Collectors.toMap(
                            (entry) -> (keyPrefix + entry.getKey().toString()),
                            (entry) -> (JSONUtil.toJsonStr(entry.getValue()))
                    ));
            // MSET批量插入Coupons
            stringRedisTemplate.opsForValue().multiSet(toCacheString);
        } else {
            notHitGoods = Collections.emptyMap();
        }

        cachedCoupons.putAll(notHitGoods);
        return cachedCoupons.values().stream().toList();
    }

    @Override
    public PageDTO<Coupons> getCouponsPage(PageQuery pageQuery) {

        // TODO: 实现缓存+分页
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
        coupons.setDiscount(form.getDiscountAmount());
    }

    private static void checkAndSet(CouponsForm form, Coupons coupons) {
        if (form.getPreferentialType() != null) {
            coupons.setPreferentialType(form.getPreferentialType());
            if (form.getPreferentialType() == PreferentialType.DISCOUNT) {
                if (form.getDiscountAmount() == null)
                    throw new CommonException("优惠类型为'折扣'但discountAmount参数为空", INVALID_ARGUMENT);
                coupons.setDiscount(form.getDiscountAmount());
            }
            if (form.getPreferentialType() == PreferentialType.REDUCTION) {
                if (form.getLimitedPrice() == null || form.getReducedPrice() == null)
                    throw new CommonException("优惠类型为'满减'但limitedPrice或reducedPrice参数为空", INVALID_ARGUMENT);
                coupons.setLimitedAmount(form.getLimitedPrice());
                coupons.setReducedAmount(form.getReducedPrice());
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
                if (form.getValidDays() == null) {
                    throw new CommonException("时效类型为相对时效但是validDays参数为空", INVALID_ARGUMENT);
                }
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
                .set(Coupons::getIsDeleted, true)
                .set(Coupons::getUpdateAt, LocalDateTime.now())
                .eq(Coupons::getIsDeleted, false)
                .eq(Coupons::getId, id)
                .update();

        if (!success) {
            throw new CommonException("删除失败，可能原因：该优惠券已经删除或不存在", UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkGoodsIdValid(Integer couponsId) {
        return bloomFilter.contains(String.valueOf(couponsId));
    }

    @Override
    public boolean checkGoodsIdValidBatch(List<Integer> couponsIdList) {
        List<String> list = couponsIdList.stream().map(Object::toString).toList();
        long contains = bloomFilter.contains(list);
        return contains == couponsIdList.size();
    }


}
