package top.codecrab.srb.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author codecrab
 * @since 2021年05月11日 8:07
 */
@Getter
@AllArgsConstructor
public enum LendItemStatusEnum {

    /**
     * 0：默认 1：已支付 2：已还款
     */
    DEFAULT(0, "默认"),
    PAID_RUN(1, "已支付"),
    REPAID(2, "已还款"),
    ;

    private final Integer status;
    private final String msg;
}
