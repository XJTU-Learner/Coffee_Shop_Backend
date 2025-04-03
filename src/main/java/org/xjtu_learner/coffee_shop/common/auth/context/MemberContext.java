package org.xjtu_learner.coffee_shop.common.auth.context;

import org.xjtu_learner.coffee_shop.entity.dto.MemberDTO;

public class MemberContext {
    private static final ThreadLocal<MemberDTO> tl = new ThreadLocal<>();

    public static void save(MemberDTO context) {
        tl.set(context);
    }

    public static MemberDTO get() {
        return tl.get();
    }

    public static void remove() {
        tl.remove();
    }
}
