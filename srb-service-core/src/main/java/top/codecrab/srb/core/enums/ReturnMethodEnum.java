package top.codecrab.srb.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author codecrab
 */
@Getter
@AllArgsConstructor
public enum ReturnMethodEnum {

    /**
     * 还款类型信息
     */
    ONE(1, "等额本息"),
    TWO(2, "等额本金"),
    THREE(3, "每月还息一次还本"),
    FOUR(4, "一次还本还息"),
    ;

    private final Integer method;
    private final String msg;
}
