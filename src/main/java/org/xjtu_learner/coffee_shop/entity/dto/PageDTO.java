package org.xjtu_learner.coffee_shop.entity.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class PageDTO<T> {

    /**
     * 总数据条数
     */
    private final Long total;

    /**
     * 总页数
     */
    private final Long pages;

    /**
     * 数据
     */
    private final List<T> list;


    public static <T> PageDTO<T> empty(Long total, Long pages) {
        return new PageDTO<>(total, pages, Collections.emptyList());
    }

    public static <T> PageDTO<T> empty(Page<?> page) {
        return new PageDTO<>(page.getTotal(), page.getPages(), Collections.emptyList());
    }

    public static <T, R> PageDTO<T> of(Page<R> page, Function<R, T> mapper) {
        if (CollectionUtil.isEmpty(page.getRecords())) {
            return empty(page);
        }
        return new PageDTO<>(page.getTotal(), page.getPages(),
                page.getRecords().stream().map(mapper).collect(Collectors.toList()));
    }

    public static <T, R> PageDTO<T> of(Page<R> page, Class<T> clazz) {
        return new PageDTO<>(page.getTotal(), page.getPages(), BeanUtil.copyToList(page.getRecords(), clazz));
    }
}
