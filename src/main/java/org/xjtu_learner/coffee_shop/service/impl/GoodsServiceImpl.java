package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.utils.BloomFilterUtil;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.form.GoodsForm;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.Coupons;
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

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.*;
import static org.xjtu_learner.coffee_shop.common.utils.BloomFilterUtil.*;

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

    private RBloomFilter<String> bloomFilter;
    private final StringRedisTemplate stringRedisTemplate;
    private final BloomFilterUtil bloomFilterUtil;

    public GoodsServiceImpl(StringRedisTemplate stringRedisTemplate, BloomFilterUtil bloomFilterUtil) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.bloomFilterUtil = bloomFilterUtil;
    }

    @PostConstruct
    void init() {
        // 预热缓存
        initCache();

        // 初始化布隆过滤器
        initBloomFilter();
    }

    private void initBloomFilter() {
        this.bloomFilter = bloomFilterUtil.getBloomFilter(BLOOMFILTER_GOODS, BLOOMFILTER_GOODS_SIZE, BLOOMFILTER_GOODS_FPP);
        List<String> goodsIdList = lambdaQuery()
                .select(Goods::getId)
                .list()
                .stream()
                .map(Goods::getId)
                .map(Object::toString)
                .toList();

        if (CollectionUtil.isNotEmpty(goodsIdList)) {
            bloomFilter.add(goodsIdList);
        }
    }

    private void initCache() {
        // 缓存预热（将热门或新品加载到redis）
        List<Goods> toCaChe = lambdaQuery()
                .eq(Goods::getIsDeleted, false)
                .eq(Goods::getIsHot, true)
                .or()
                .eq(Goods::getIsNew, true)
                .list();

        if(CollectionUtil.isEmpty(toCaChe)){
            return;
        }

        Map<String, String> toCaCheString = toCaChe.stream()
                .collect(Collectors.toMap(
                        (goods) -> (CACHE_GOODS_PREFIX + goods.getId().toString()),
                        JSONUtil::toJsonStr
                ));
        // MSET批量插入Goods
        stringRedisTemplate.opsForValue().multiSet(toCaCheString);
    }


    @Override
    public List<Goods> getGoodsList(List<Integer> goodsIdList) {

        if (!checkGoodsIdValidBatch(goodsIdList)) {
            throw new CommonException("有不存在的goodsId", NOT_EXIST);
        }

        String keyPrefix = CACHE_GOODS_PREFIX;
        // 构造出List<String>的keyList，用于MGET批量查询
        List<String> keyList = goodsIdList.stream()
                .map(Object::toString)
                .map(key -> String.format("%s%s", keyPrefix, key))
                .toList();

        // MGET批量从缓存中获取商品对象
        Map<Integer, Goods> cachedGoods = Objects.requireNonNull(stringRedisTemplate.opsForValue().multiGet(keyList))
                .stream()
                .filter(Objects::nonNull)
                .map((json) -> JSONUtil.toBean(json, Goods.class))
                .collect(Collectors.toMap(Goods::getId, po -> po));

        // 组装没有命中的商品ID
        List<Integer> notHitIdList = goodsIdList.stream()
                .filter(goodsId -> !cachedGoods.containsKey(goodsId))
                .toList();

        Map<Integer, Goods> notHitGoods;
        if (CollectionUtil.isNotEmpty(notHitIdList)) {
            // 批量从数据库查询未命中的商品信息列表
            notHitGoods = lambdaQuery()
                    .in(Goods::getId, notHitIdList)
                    .eq(Goods::getIsDeleted, false)  // 如果已被下架的商品将不会被加入缓存
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Goods::getId, po -> po));

            // 如果存在数据库未查询到的商品，则报错
            if (notHitIdList.size() != notHitGoods.size()) {
                throw new CommonException("有不存在或者已下架的商品", NOT_EXIST);
            }

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
        return cachedGoods.values().stream().toList();
    }


    @Override
    public PageDTO<Goods> getGoodsPage(PageQuery pageQuery) {

//        由于goods在redis中以字符串缓存，不易于实现分页，若先通过mysql查出idList，再查缓存
//        并不会得到较大优化，故直接通过mysql实现，若之后加入了更高效的索引查询组件，此处可以优化
        // TODO: 实现缓存+分页
        Page<Goods> page = lambdaQuery()
                .page(pageQuery.toMpPage(pageQuery.getSortBy(), pageQuery.getIsAsc()));

        return PageDTO.of(page, Goods.class);
    }


    @Override
    public PageDTO<Goods> getNewGoodsList(PageQuery pageQuery) {
        Page<Goods> page = lambdaQuery()
                .eq(Goods::getIsNew, true)
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
        Integer goodsId = form.getId();
        if (goodsId == null) {
            throw new CommonException("未指定指定主键id", INVALID_ARGUMENT);
        }
        Goods goods = BeanUtil.copyProperties(form, Goods.class);
        goods.setUpdateAt(LocalDateTime.now());
        boolean success = lambdaUpdate()
                .eq(Goods::getId, goodsId)
                .update(goods);

        if (!success) {
            throw new CommonException("更新失败，可能原因：该商品不存在", UPDATE_FAILED);
        }

        // 删除缓存
        stringRedisTemplate.delete(CACHE_GOODS_PREFIX + goodsId);
    }

    /*
     * 下架是一个复杂的场景，各门店的shopGoodsRelation以zset的形式缓存，如果某商品下架，所有门店的缓存都将存在数据不一致
     * 问题，但是如果因为这样就把所有的门店缓存删除代价太大，尝试过使用渐进性修改（延迟修改数据库字段和缓存），但是实现较为复杂，
     * 且会导致'获取指定门店的出售商品列表'接口代价提升，遂放弃。
     *
     * 事实上，像下架某一商品这种重大事件，总司有义务提前向各门店通知，
     * 下架商品的数据一致性问题可以通过集中的数据修改实现。
     *
     * 关于下架业务的数据库字段，在tb_goods表中的is_deleted字段表示该商品是否被下架，如果只有这个字段，则总司端一旦下架
     * 某个商品，所有相关门店都会收到影响。事实业务中可能出现只是一部分地区下架了该商品（这可以通过tb_shop_goods_relation
     * 表中的is_deleted字段实现），甚至是下架的商品又可以恢复上架（季节性产品），这样的业务更加复杂。
     * */
    @Override
    @Transactional
    public void deleteGoods(Integer id) {
        if (id == null) {
            throw new CommonException("未指定指定主键id", INVALID_ARGUMENT);
        }
        boolean success = lambdaUpdate()
                .eq(Goods::getId, id)
                .eq(Goods::getIsDeleted, true)
                .set(Goods::getUpdateAt, LocalDateTime.now())
                .set(Goods::getIsDeleted, false)
                .update();

        if (!success) {
            throw new CommonException("下架失败，可能原因：该商品已经下架或商品不存在", UPDATE_FAILED);
        }

        // 删除缓存
        stringRedisTemplate.delete(CACHE_GOODS_PREFIX + id);
    }

    @Override
    public boolean checkGoodsIdValid(Integer goodsId) {
        return bloomFilter.contains(goodsId.toString());
    }

    @Override
    public boolean checkGoodsIdValidBatch(List<Integer> goodsIdList) {
        List<String> list = goodsIdList.stream().map(Object::toString).toList();
        long contains = bloomFilter.contains(list);
        return contains == goodsIdList.size();
    }

}
