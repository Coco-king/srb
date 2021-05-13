package top.codecrab.srb.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author codecrab
 * @since 2021年05月11日 18:14
 */
@Getter
@AllArgsConstructor
public enum LendReturnStatusEnum {

    /**
     * 状态（0-未归还 1-已归还）
     */
    NOT_RETURNED(0, "未归还"),
    RETURNED(1, "已归还"),
    ;

    private final Integer status;
    private final String msg;
}
