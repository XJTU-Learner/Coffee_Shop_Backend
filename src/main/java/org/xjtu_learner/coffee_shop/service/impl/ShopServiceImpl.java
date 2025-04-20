package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RBloomFilter;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.utils.BloomFilterUtil;
import org.xjtu_learner.coffee_shop.entity.form.NearbySearchForm;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import org.xjtu_learner.coffee_shop.dao.ShopMapper;
import org.xjtu_learner.coffee_shop.entity.po.ShopChangeRecord;
import org.xjtu_learner.coffee_shop.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.CACHE_SHOP_GEO_KEY;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.CACHE_SHOP_PREFIX;
import static org.xjtu_learner.coffee_shop.common.utils.BloomFilterUtil.*;

/**
 * <p>
 * 门店表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    private RBloomFilter<String> bloomFilter;
    private final BloomFilterUtil bloomFilterUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public ShopServiceImpl(BloomFilterUtil bloomFilterUtil, StringRedisTemplate stringRedisTemplate) {
        this.bloomFilterUtil = bloomFilterUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    void init() {
        initBloomFilter();
        initCache();
    }

    private void initCache() {

        List<Shop> shopList = lambdaQuery().list();

        if (CollectionUtil.isEmpty(shopList)) {
            return;
        }

        // 将未命中的商品重新加载到缓存
        Map<String, String> toCacheString = shopList.stream()
                .collect(Collectors.toMap(
                        (shop) -> (CACHE_SHOP_PREFIX + shop.getId().toString()),
                        JSONUtil::toJsonStr
                ));
        // MSET批量插入Shop
        stringRedisTemplate.opsForValue().multiSet(toCacheString);

        // 初始化GEO缓存
        initGEOCache(shopList);
    }

    private void initGEOCache(List<Shop> shopList) {
        Map<String, Point> toCacheGEO = shopList.stream()
                .collect(Collectors.toMap(
                        (shop) -> (shop.getId().toString()),
                        (shop) -> (new Point(shop.getLongitude().doubleValue(), shop.getLatitude().doubleValue()))
                ));
        stringRedisTemplate.opsForGeo().add(CACHE_SHOP_GEO_KEY, toCacheGEO);
    }


    private void initBloomFilter() {
        this.bloomFilter = bloomFilterUtil.getBloomFilter(BLOOMFILTER_SHOP_GOODS_RELATION, BLOOMFILTER_SHOP_GOODS_RELATION_SIZE, BLOOMFILTER_SHOP_GOODS_RELATION_FPP);
        // 初始化布隆过滤器
        List<String> shopIdList = lambdaQuery()
                .select(Shop::getId)
                .list().stream()
                .map(Shop::getId)
                .map(Object::toString)
                .toList();

        if (CollectionUtil.isNotEmpty(shopIdList)) {
            bloomFilter.add(shopIdList);
        }
    }

    @Override
    public boolean checkShopIdValid(Integer shopId) {
        return bloomFilter.contains(String.valueOf(shopId));
    }


    @Override
    public boolean checkShopIdValidBatch(List<Integer> shopIdList) {
        List<String> list = shopIdList.stream().map(Object::toString).toList();
        long contains = bloomFilter.contains(list);
        return contains == shopIdList.size();
    }

    @Override
    public List<Shop> getShopList(List<Integer> shopIdList) {

        if (!checkShopIdValidBatch(shopIdList)) {
            throw new CommonException("有不存在的goodsId", NOT_EXIST);
        }

        String keyPrefix = CACHE_SHOP_PREFIX;

        // 构造出List<String>的keyList，用于MGET批量查询
        List<String> keyList = shopIdList.stream()
                .map(Object::toString)
                .map(key -> String.format("%s%s", keyPrefix, key))
                .toList();

        // MGET批量从缓存中获取商品对象
        Map<Integer, Shop> cachedShop = Objects.requireNonNull(stringRedisTemplate.opsForValue().multiGet(keyList))
                .stream()
                .filter(Objects::nonNull)
                .map((json) -> JSONUtil.toBean(json, Shop.class))
                .collect(Collectors.toMap(Shop::getId, po -> po));

        // 组装没有命中的商品ID
        List<Integer> notHitIdList = shopIdList.stream()
                .filter(shopId -> !cachedShop.containsKey(shopId))
                .toList();

        Map<Integer, Shop> notHitShop;
        if (CollectionUtil.isNotEmpty(notHitIdList)) {
            // 批量从数据库查询未命中的商品信息列表
            notHitShop = lambdaQuery()
                    .in(Shop::getId, notHitIdList)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Shop::getId, po -> po));

            // 将未命中的商品重新加载到缓存
            Map<String, String> toCacheString = notHitShop.entrySet().stream()
                    .collect(Collectors.toMap(
                            (entry) -> (keyPrefix + entry.getKey().toString()),
                            (entry) -> (JSONUtil.toJsonStr(entry.getValue()))
                    ));
            // MSET批量插入Shop
            stringRedisTemplate.opsForValue().multiSet(toCacheString);

            // 重新加载GEO缓存
            initGEOCache(notHitShop.values().stream().toList());

        } else {
            notHitShop = Collections.emptyMap();
        }

        cachedShop.putAll(notHitShop);
        return cachedShop.values().stream().toList();
    }

    @Override
    public PageDTO<Shop> getShopPage(PageQuery pageQuery) {

//        由于shop在redis中以字符串缓存，不易于实现分页，若先通过mysql查出idList，再查缓存
//        并不会得到较大优化，故直接通过mysql实现，若之后加入了更高效的索引查询组件，此处可以优化
        // TODO: 实现缓存+分页（同GoodsServiceImpl$getGoodsPage）
        Page<Shop> page = lambdaQuery()
                .page(pageQuery.toMpPage(pageQuery.getSortBy(), pageQuery.getIsAsc()));

        return PageDTO.of(page, Shop.class);
    }

    @Override
    public Map<Shop, String> getNearbyShop(NearbySearchForm form) {

        /*
         * 这里可能有数据不一致的问题，当一个shop的信息变更后，需要在缓存中删除其记录以及在GEO缓存中的记录，
         * 该shop的缓存重建应当发生在getShopList()中，在重建shop信息的同时重建GEO缓存，本方法不触发getShopList()
         * 中的缓存重建，也不应该触发缓存重建（因为从GEO缓存搜索附近门店无法得知哪些门店缓存失效）
         * */

        // TODO:解决这里的数据一致性问题

        Point point = new Point(form.getLongitude(), form.getLatitude());
        Distance distance = new Distance(form.getRadius(), RedisGeoCommands.DistanceUnit.METERS);
        Circle circle = new Circle(point, distance);

        GeoResults<RedisGeoCommands.GeoLocation<String>> search = stringRedisTemplate.opsForGeo().search(CACHE_SHOP_GEO_KEY, circle);

        Map<Integer, String> nearByMap = search.getContent().stream()
                .collect(Collectors.toMap(
                        (result) -> (Integer.valueOf(result.getContent().getName())),
                        (result) -> (result.getDistance().toString())
                ));

        Map<Integer, Shop> shopMap = getShopList(nearByMap.keySet().stream().toList()).stream()
                .collect(Collectors.toMap(
                        Shop::getId,
                        (shop) -> (shop)
                ));

        return nearByMap.entrySet().stream().collect(Collectors.toMap(
                        (entry) -> (shopMap.get(entry.getKey())),
                        Map.Entry::getValue
                )
        );
    }

    @Override
    public void updateShop(ShopChangeRecord record) {

        boolean success = lambdaUpdate()
                .set(record.getNewProvince() != null, Shop::getProvince, record.getNewProvince())
                .set(record.getNewCity() != null, Shop::getCity, record.getNewCity())
                .set(record.getNewArea() != null, Shop::getArea, record.getNewArea())
                .set(record.getNewStreet() != null, Shop::getStreet, record.getNewStreet())
                .set(record.getNewHouseNumber() != null, Shop::getHouseNumber, record.getNewHouseNumber())
                .set(record.getNewShopImg() != null, Shop::getShopImg, record.getNewShopImg())
                .set(record.getNewContactRealname() != null, Shop::getContactRealname, record.getNewContactRealname())
                .set(record.getNewContactPhone() != null, Shop::getContactPhone, record.getNewContactPhone())
                .set(record.getNewBriefIntroduction() != null, Shop::getBriefIntroduction, record.getNewBriefIntroduction())
                .set(record.getNewBusinessLicense() != null, Shop::getBusinessLicense, record.getNewBusinessLicense())
                .set(record.getNewOpenTime() != null, Shop::getOpenTime, record.getNewOpenTime())
                .set(record.getNewCloseTime() != null, Shop::getCloseTime, record.getNewCloseTime())
                .set(record.getNewLatitude() != null, Shop::getLatitude, record.getNewLatitude())
                .set(record.getNewLongitude() != null, Shop::getLongitude, record.getNewLongitude())
                .set(Shop::getUpdateAt, LocalDateTime.now())
                .update();

        if (!success) {
            throw new CommonException("更新门店信息异常", TO_BE_SUPPLEMENTED);
        }
    }

    @Override
    public void setShopNickname(Integer merchantId, String newNickname) {
        boolean success = lambdaUpdate()
                .set(Shop::getNickname, newNickname)
                .eq(Shop::getId, merchantId)
                .update();

        if (!success) {
            throw new CommonException("设置门店名称失败", UPDATE_FAILED);
        }
    }
}
