package top.codecrab.srb.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author codecrab
 */
@Getter
@AllArgsConstructor
public enum IntegralEnum {

    /**
     * 借款人信息枚举
     */
    BORROWER_INFO(null, "借款人基本信息"),
    BORROWER_ID_CARD(30, "借款人身份证信息"),
    BORROWER_HOUSE(100, "借款人房产信息"),
    BORROWER_CAR(60, "借款人车辆信息"),
    ;

    private final Integer integral;
    private final String msg;
}
