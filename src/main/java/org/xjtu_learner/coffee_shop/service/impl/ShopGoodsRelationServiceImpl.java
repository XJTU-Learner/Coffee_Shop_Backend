package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.utils.CacheAgent;
import org.xjtu_learner.coffee_shop.entity.dto.GoodsDTO;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.dto.PageQuery;
import org.xjtu_learner.coffee_shop.entity.dto.ShopGoodsDTO;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.entity.po.ShopGoodsRelation;
import org.xjtu_learner.coffee_shop.dao.ShopGoodsRelationMapper;
import org.xjtu_learner.coffee_shop.service.IGoodsService;
import org.xjtu_learner.coffee_shop.service.IShopGoodsRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xjtu_learner.coffee_shop.service.IShopService;

import java.util.*;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.NOT_EXIST;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.*;

/**
 * <p>
 * 门店商品关联表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class ShopGoodsRelationServiceImpl extends ServiceImpl<ShopGoodsRelationMapper, ShopGoodsRelation> implements IShopGoodsRelationService {


    private final ShopGoodsRelationMapper shopGoodsRelationMapper;
    private final IShopService shopService;
    private final IGoodsService goodsService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final CacheAgent cacheAgent;

    public ShopGoodsRelationServiceImpl(ShopGoodsRelationMapper shopGoodsRelationMapper, IShopService shopService, GoodsServiceImpl goodsService, StringRedisTemplate stringRedisTemplate, RedissonClient redissonClient, CacheAgent cacheAgent) {
        this.shopGoodsRelationMapper = shopGoodsRelationMapper;
        this.shopService = shopService;
        this.goodsService = goodsService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redissonClient = redissonClient;
        this.cacheAgent = cacheAgent;
    }


    //转化成GoodsDTO列表
    public List<GoodsDTO> transferToGoodsDTOList(List<ShopGoodsRelation> goodsList) {
//        List<GoodsDTO> goodsDTOList = new ArrayList<>();
//        for (ShopGoodsRelation goods : goodsList) {
//            GoodsDTO goodsDTO = new GoodsDTO();
//            goodsDTO.setId(goods.getGoodsId());
//            goodsDTO.setName(goods.ge());
//            goodsDTO.setImage(goods.getImage());
//            goodsDTO.setBasePrice(String.valueOf(goods.getBasePrice()));
//            goodsDTO.setTag(goods.getTag());
//            goodsDTO.setIsSoldOut(false);
//            goodsDTOList.add(goodsDTO);
//        }
//        return goodsDTOList;
        return null;
    }

    //添加商品
    @Override
    public Goods addNewGoods(int goodId) {

        Goods good = goodsService.getById(goodId);
        if (good != null) {
            ShopGoodsRelation temp = lambdaQuery().eq(ShopGoodsRelation::getGoodsId, goodId).one();
            if (temp != null) {
                throw new CommonException("已经存在此商品请勿重复添加", NOT_EXIST);
            }
//
//            ShopGoodsRelation shopGoodsRelation = new ShopGoodsRelation();
//            shopGoodsRelation.setShopId(MerchantContext.get().getShopId());
//            shopGoodsRelation.setGoodsId(good.getId());
//            shopGoodsRelation.setName(good.getName());
//            shopGoodsRelation.setImage(good.getImage());
//            shopGoodsRelation.setBasePrice(good.getBasePrice());
//            shopGoodsRelation.setTag(good.getTag());
//            shopGoodsRelation.setIsSoldOut(false);
//            save(shopGoodsRelation);

        } else {
            throw new CommonException("暂无此商品", NOT_EXIST);
        }
        return good;

    }


    /*
     * 使用缓存查询列表类型结果
     * */
    @Override
    public List<ShopGoodsDTO> getShopGoodsList(Integer shopId) {

        checkShopIdValid(shopId);

        List<ShopGoodsRelation> relationList = getShopGoodsRelationList(shopId);

        return getShopGoodsDTOList(relationList);
    }


    @Override
    public PageDTO<ShopGoodsDTO> getShopGoodsPage(Integer shopId, PageQuery pageQuery) {

        checkShopIdValid(shopId);

        PageDTO<ShopGoodsRelation> relationPage = getShopGoodsRelationPage(shopId, pageQuery);

        List<ShopGoodsRelation> relationList = relationPage.getList();

        List<ShopGoodsDTO> all = getShopGoodsDTOList(relationList);

        return PageDTO.<ShopGoodsDTO>builder()
                .total(relationPage.getTotal())
                .pages(relationPage.getPages())
                .list(all)
                .build();
    }


    private List<ShopGoodsRelation> getShopGoodsRelationList(Integer shopId) {

        // 通过缓存查询ShopGoodsDTO
        String key = CACHE_SHOP_GOODS_RELATION_PREFIX + shopId;

        List<ShopGoodsRelation> cachedRelation = Objects.requireNonNull(stringRedisTemplate.opsForZSet().range(key, 0, -1))
                .stream()
                .map((json) -> (JSONUtil.toBean(json, ShopGoodsRelation.class)))
                .toList();

        // 缓存未命中，通过mysql查询ShopGoodsRelation并重建缓存
        if (cachedRelation.isEmpty()) {
            cachedRelation = rebuildCache(shopId);
        }
        return cachedRelation;
    }


    private PageDTO<ShopGoodsRelation> getShopGoodsRelationPage(Integer shopId, PageQuery pageQuery) {

        String key = CACHE_SHOP_GOODS_RELATION_PREFIX + shopId;

        long pageSize = pageQuery.getPageSize();
        long pageNo = pageQuery.getPageNo();

        Long total = stringRedisTemplate.opsForZSet().zCard(key);
        long start = pageSize * (pageNo - 1);
        long end = Math.min(start + pageSize, total) - 1;

        List<ShopGoodsRelation> cachedRelation;
        if (total != 0) {
            // 通过缓存查询ShopGoodsDTO
            cachedRelation = Objects.requireNonNull(stringRedisTemplate.opsForZSet().range(key, start, end))
                    .stream()
                    .map((json) -> (JSONUtil.toBean(json, ShopGoodsRelation.class)))
                    .toList();
        } else {
            // 缓存未命中，通过mysql查询ShopGoodsRelation并重建缓存
            cachedRelation = rebuildCache(shopId);

            total = (long) cachedRelation.size();
        }
        long pages = total / pageSize + 1;

        return PageDTO.<ShopGoodsRelation>builder()
                .total(total)
                .pages(pages)
                .list(cachedRelation)
                .build();
    }


    private List<ShopGoodsDTO> getShopGoodsDTOList(List<ShopGoodsRelation> relationList) {
        List<ShopGoodsDTO> all = relationList.stream()
                .map((po) -> (ShopGoodsDTO.builder()
                        .id(po.getId())
                        .goodsId(po.getGoodsId())
                        .isSoldOut(po.getIsSoldOut())
                        .build()))
                .toList();

        // 查询Goods缓存填充ShopGoodsDTO中的goodsInfo字段
        List<Integer> idList = relationList.stream()
                .map(ShopGoodsRelation::getGoodsId)
                .toList();

        Map<Integer, Goods> goods = goodsService.getGoods(idList);

        // 遍历商品ID列表，组装对象列表
        all.forEach(dto -> {
            dto.setGoodsInfo(BeanUtil.copyProperties(goods.get(dto.getGoodsId()), GoodsDTO.class));
        });
        return all;
    }


    private void checkShopIdValid(Integer shopId) {
        // 通过布隆过滤器解决缓存穿透问题
        RBloomFilter<String> bloomFilter = shopService.getBloomFilter();
        if (!bloomFilter.contains(String.valueOf(shopId))) throw new CommonException("不存在的shopId", NOT_EXIST);
    }


    @Override
    public List<ShopGoodsDTO> getSoldOutList(Integer shopId) {

        checkShopIdValid(shopId);

        List<ShopGoodsRelation> relationList = getSoldOutShopGoodsRelationList(shopId);

        return getShopGoodsDTOList(relationList);
    }


    private List<ShopGoodsRelation> getSoldOutShopGoodsRelationList(Integer shopId) {

        List<ShopGoodsRelation> cachedRelation = Objects.requireNonNull(stringRedisTemplate.opsForZSet().range(CACHE_SHOP_GOODS_RELATION_PREFIX + shopId, 0, -1))
                .stream()
                .map((json) -> (JSONUtil.toBean(json, ShopGoodsRelation.class)))
                .filter(ShopGoodsRelation::getIsSoldOut)
                .toList();

        // 缓存未命中，通过mysql查询ShopGoodsRelation并重建缓存
        if (cachedRelation.isEmpty()) {
            cachedRelation = rebuildCache(shopId);
        }
        return cachedRelation;
    }

    private List<ShopGoodsRelation> rebuildCache(Integer shopId) {
        List<ShopGoodsRelation> cachedRelation;
        cachedRelation = lambdaQuery()
                .eq(ShopGoodsRelation::getShopId, shopId)
                .list();

        Set<ZSetOperations.TypedTuple<String>> toCache = cachedRelation.stream()
                .map((po) -> (ZSetOperations.TypedTuple.of(JSONUtil.toJsonStr(po), Double.valueOf(String.valueOf(po.getTotalSales())))))
                .collect(Collectors.toSet());

        stringRedisTemplate.opsForZSet().add(CACHE_SHOP_GOODS_RELATION_PREFIX + shopId, toCache);
        return cachedRelation;
    }

}
