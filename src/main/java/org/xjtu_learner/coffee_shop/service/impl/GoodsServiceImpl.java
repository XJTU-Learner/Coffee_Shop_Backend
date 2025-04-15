package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.dao.GoodsMapper;
import org.xjtu_learner.coffee_shop.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.INVALID_ARGUMENT;
import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.UPDATE_FAILED;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.CACHE_GOODS_PREFIX;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.CACHE_GOODS_TTL;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    private final StringRedisTemplate stringRedisTemplate;

    public GoodsServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    void init() {
        initCache();
    }

    private void initCache() {
        // 缓存预热（将热门或新品加载到redis）
        List<Goods> toCaChe = lambdaQuery()
                .eq(Goods::getIsHot, true)
                .or()
                .eq(Goods::getIsNew, true)
                .list();

        Map<String, String> toCaCheString = toCaChe.stream()
                .collect(Collectors.toMap(
                        (entry) -> (CACHE_GOODS_PREFIX + entry.getId().toString()),
                        JSONUtil::toJsonStr
                ));
        // MSET批量插入Goods
        stringRedisTemplate.opsForValue().multiSet(toCaCheString);
    }

    @Override
    public PageDTO<Goods> getGoodsPage(PageQuery pageQuery) {

        Page<Goods> page = lambdaQuery()
                .page(pageQuery.toMpPage(pageQuery.getSortBy(), pageQuery.getIsAsc()));

        return PageDTO.of(page, Goods.class);
    }

    /**
     * 批量缓存获取指定idList的全部商品
     * @param idList 指定的商品id
     * @return idList所对应的全部商品
     */
    @Override
    public Map<Integer, Goods> getGoods(List<Integer> idList) {

        String keyPrefix = CACHE_GOODS_PREFIX;
        // 构造出List<String>的keyList，用于MGET批量查询
        List<String> keyList = idList.stream()
                .map(Object::toString)
                .map(key -> String.format("%s%s", keyPrefix, key))
                .toList();

        // MGET批量从缓存中获取商品对象
        Map<Integer, Goods> cachedGoods = Objects.requireNonNull(stringRedisTemplate.opsForValue().multiGet(keyList))
                .stream()
                .filter(Objects::nonNull)
                .map((json) -> JSONUtil.toBean(json, Goods.class))
                .collect(Collectors.toMap(Goods::getId, dto -> dto));

        // 组装没有命中的商品ID
        List<Integer> notHitIdList = idList.stream()
                .filter(goodsId -> !cachedGoods.containsKey(goodsId))
                .toList();

        Map<Integer, Goods> notHitGoods;
        if (CollectionUtil.isNotEmpty(notHitIdList)) {
            // 批量从数据库查询未命中的商品信息列表
            notHitGoods = lambdaQuery()
                    .in(Goods::getId, notHitIdList)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Goods::getId, dto -> dto));

            // 将未命中的商品重新加载到缓存
            Map<String, String> toCacheString = notHitGoods.entrySet().stream()
                    .collect(Collectors.toMap(
                            (entry) -> (keyPrefix + entry.getKey().toString()),
                            (entry) -> (JSONUtil.toJsonStr(entry.getValue()))
                    ));
            // MSET批量插入Goods
            stringRedisTemplate.opsForValue().multiSet(toCacheString);
            // redis pipeline 批量为非热点和非新品商品设置过期时间
            stringRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
                notHitGoods.forEach((key, val) -> {
                    if (!val.getIsHot() && !val.getIsNew()) {
                        connection.expire((keyPrefix + key.toString()).getBytes(), CACHE_GOODS_TTL);
                    }
                });
                return null;
            });
        } else {
            notHitGoods = Collections.emptyMap();
        }

        cachedGoods.putAll(notHitGoods);
        return cachedGoods;
    }


    @Override
    public PageDTO<Goods> getNewGoodsList(PageQuery pageQuery) {
        Page<Goods> page = lambdaQuery().eq(Goods::getIsNew, true)
                .page(pageQuery.toMpPage(pageQuery.getSortBy(), pageQuery.getIsAsc()));
        return PageDTO.of(page, Goods.class);
    }

    @Override
    @Transactional
    public void createGoods(GoodsForm form) {
        if (form.getId() != null) {
            throw new CommonException("不需要指定主键id", INVALID_ARGUMENT);
        }
        Goods newGoods = BeanUtil.copyProperties(form, Goods.class, "id");
        save(newGoods);
    }

    @Override
    @Transactional
    public void updateGoods(GoodsForm form) {
        if (form.getId() == null) {
            throw new CommonException("未指定指定主键id", INVALID_ARGUMENT);
        }
        Goods goods = BeanUtil.copyProperties(form, Goods.class);
        goods.setUpdateAt(LocalDateTime.now());
        boolean success = lambdaUpdate()
                .eq(Goods::getId, form.getId())
                .update(goods);

        if (!success) {
            throw new CommonException("更新失败，可能原因：该商品不存在", UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteGoods(Integer id) {
        if (id == null) {
            throw new CommonException("未指定指定主键id", INVALID_ARGUMENT);
        }
        boolean success = lambdaUpdate()
                .eq(Goods::getId, id)
                .eq(Goods::getForSale, true)
                .set(Goods::getUpdateAt, LocalDateTime.now())
                .set(Goods::getForSale, false)
                .update();

        if (!success) {
            throw new CommonException("下架失败，可能原因：该商品已经下架或商品不存在", UPDATE_FAILED);
        }
    }
}
