package org.xjtu_learner.coffee_shop.entity.form;

import lombok.Data;

@Data
public class WithdrawalForm {

    Double amount;//提现金额
    int payment_method;//提现方式
}
