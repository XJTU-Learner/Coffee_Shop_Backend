package org.xjtu_learner.coffee_shop.common.auth.context;

import org.xjtu_learner.coffee_shop.entity.dto.MerchantDTO;

public class MerchantContext {
    private static final ThreadLocal<MerchantDTO> tl = new ThreadLocal<>();

    public static void save(MerchantDTO context) {
        tl.set(context);
    }

    public static MerchantDTO get() {
        return tl.get();
    }

    public static void remove() {
        tl.remove();
    }
}
