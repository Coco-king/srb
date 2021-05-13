package top.codecrab.srb.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author codecrab
 */
@Getter
@AllArgsConstructor
public enum BorrowerStatusEnum {

    /**
     * 借款人状态
     */
    NO_AUTH(0, "未认证"),
    AUTH_RUN(1, "认证中"),
    AUTH_OK(2, "认证成功"),
    AUTH_FAIL(-1, "认证失败"),
    ;

    private final Integer status;
    private final String msg;

    public static String getMsgByStatus(int status) {
        BorrowerStatusEnum[] arrObj = BorrowerStatusEnum.values();
        for (BorrowerStatusEnum obj : arrObj) {
            if (status == obj.getStatus()) {
                return obj.getMsg();
            }
        }
        return "";
    }
}

