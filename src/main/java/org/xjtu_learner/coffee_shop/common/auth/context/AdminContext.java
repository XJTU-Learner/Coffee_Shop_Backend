package org.xjtu_learner.coffee_shop.common.auth.context;

import org.xjtu_learner.coffee_shop.entity.dto.AdminDTO;

public class AdminContext {
    private static final ThreadLocal<AdminDTO> tl = new ThreadLocal<>();

    public static void save(AdminDTO context) {
        tl.set(context);
    }

    public static AdminDTO get() {
        return tl.get();
    }

    public static void remove() {
        tl.remove();
    }
}
