package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.GoodsDTO;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
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

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.INVALID_ARGUMENT;
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


    private final IShopService shopService;
    private final IGoodsService goodsService;
    private final StringRedisTemplate stringRedisTemplate;

    public ShopGoodsRelationServiceImpl(IShopService shopService, GoodsServiceImpl goodsService, StringRedisTemplate stringRedisTemplate) {
        this.shopService = shopService;
        this.goodsService = goodsService;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public List<ShopGoodsRelation> getShopGoodsRelationList(Integer shopId) {

        checkShopIdValid(shopId);

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


    @Override
    public PageDTO<ShopGoodsRelation> getShopGoodsRelationPage(Integer shopId, PageQuery pageQuery) {

        checkShopIdValid(shopId);

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

    @Override
    public List<ShopGoodsDTO> getShopGoodsDTOList(List<ShopGoodsRelation> relationList) {
        List<ShopGoodsDTO> all = relationList.stream()
                .map((po) -> (ShopGoodsDTO.builder()
                        .id(po.getId())
                        .goodsId(po.getGoodsId())
                        .isSoldOut(po.getIsSoldOut())
                        .build()))
                .toList();

        // 查询Goods缓存填充ShopGoodsDTO中的goodsInfo字段
        List<Integer> goodsIdList = relationList.stream()
                .map(ShopGoodsRelation::getGoodsId)
                .toList();

        List<Goods> goodsList = goodsService.getGoodsList(goodsIdList);
        Map<Integer, Goods> goods = goodsList.stream()
                .collect(Collectors.toMap(
                        Goods::getId,
                        (po) -> (po)
                ));

        // 遍历商品ID列表，组装对象列表
        all.forEach(dto -> {
            dto.setGoodsInfo(BeanUtil.copyProperties(goods.get(dto.getGoodsId()), GoodsDTO.class));
        });
        return all;
    }


    private void checkShopIdValid(Integer shopId) {
        // 通过布隆过滤器解决缓存穿透问题
        if (!shopService.checkShopIdValid(shopId)) throw new CommonException("不存在的shopId", NOT_EXIST);
    }


    @Override
    public List<ShopGoodsRelation> getSoldOutList(Integer shopId) {

        checkShopIdValid(shopId);

        return getSoldOutShopGoodsRelationList(shopId);

    }


    @Override
    public List<ShopGoodsRelation> getSoldOutShopGoodsRelationList(Integer shopId) {

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

    @Override
    public void updateIsSoldOut(Integer shopGoodsRelationId, Boolean isSoldOut) {

        checkExist(shopGoodsRelationId);

        boolean success = lambdaUpdate()
                .set(ShopGoodsRelation::getIsSoldOut, isSoldOut)
                .eq(ShopGoodsRelation::getId, shopGoodsRelationId)
                .eq(ShopGoodsRelation::getShopId, MerchantContext.get().getShopId())
                .eq(ShopGoodsRelation::getIsSoldOut, !isSoldOut)
                .update();

        if (!success) {
            throw new CommonException("更新失败，可能原因：非本门店数据或商品状态已为" + isSoldOut, INVALID_ARGUMENT);
        }

        deleteCache();
    }

    private List<ShopGoodsRelation> rebuildCache(Integer shopId) {

        List<ShopGoodsRelation>  cachedRelation = lambdaQuery()
                .eq(ShopGoodsRelation::getShopId, shopId)
                .eq(ShopGoodsRelation::getIsDeleted,false)  // 已下架的商品不加入缓存
                .list();

        Set<ZSetOperations.TypedTuple<String>> toCache = cachedRelation.stream()
                .map((po) -> (ZSetOperations.TypedTuple.of(JSONUtil.toJsonStr(po), Double.valueOf(String.valueOf(po.getTotalSales())))))
                .collect(Collectors.toSet());

        stringRedisTemplate.opsForZSet().add(CACHE_SHOP_GOODS_RELATION_PREFIX + shopId, toCache);
        return cachedRelation;
    }


    @Override
    public void checkExist(Integer shopGoodsRelationId) {

        List<ShopGoodsRelation> shopGoodsList = getShopGoodsRelationList(MerchantContext.get().getShopId());

        List<Integer> existed = shopGoodsList.stream().map(ShopGoodsRelation::getId).toList();

        if (!existed.contains(shopGoodsRelationId)) {
            throw new CommonException("该门店并不存在以下商品关系：" + shopGoodsRelationId, NOT_EXIST);
        }
    }


    @Override
    public void checkExistBatch(List<Integer> shopGoodsRelationIdList) {
        // 检查restockList是否均为本门店所对应的商品
        List<ShopGoodsRelation> shopGoodsList = getShopGoodsRelationList(MerchantContext.get().getShopId());

        List<Integer> existed = shopGoodsList.stream().map(ShopGoodsRelation::getId).toList();

        List<Integer> notExist = shopGoodsRelationIdList.stream()
                .filter((shopGoodsRelationId) -> (!existed.contains(shopGoodsRelationId)))
                .toList();

        if (CollectionUtil.isNotEmpty(notExist)) {
            throw new CommonException("该门店并不存在以下商品关系：" + notExist, NOT_EXIST);
        }
    }


    @Override
    @Transactional
    public void addGoods(List<Integer> goodIdList) {

        Integer shopId = MerchantContext.get().getShopId();
        List<Goods> goodsList = goodsService.getGoodsList(goodIdList);

        // 去除goodsList中门店已经存在的商品
        List<ShopGoodsRelation> shopGoodsList = getShopGoodsRelationList(shopId);
        List<Integer> existGoodsId = shopGoodsList.stream()
                .map(ShopGoodsRelation::getGoodsId)
                .toList();

        List<Goods> afterRemoving = goodsList.stream()
                .filter((po) -> (!existGoodsId.contains(po.getId())))
                .toList();

        List<ShopGoodsRelation> toSave = afterRemoving.stream()
                .map((goods) -> {
                    ShopGoodsRelation shopGoodsRelation = new ShopGoodsRelation();
                    shopGoodsRelation.setShopId(shopId);
                    shopGoodsRelation.setGoodsId(goods.getId());
                    return shopGoodsRelation;
                })
                .toList();

        saveBatch(toSave);

        // 删除缓存
        deleteCache();
    }


    @Override
    public void deleteCache() {

        Integer shopId = MerchantContext.get().getShopId();
        stringRedisTemplate.delete(CACHE_SHOP_GOODS_RELATION_PREFIX + shopId);
    }

}
