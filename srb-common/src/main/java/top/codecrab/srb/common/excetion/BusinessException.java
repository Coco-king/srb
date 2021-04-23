package top.codecrab.srb.common.excetion;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.codecrab.srb.common.response.ResponseEnum;

/**
 * @author codecrab
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BusinessException extends RuntimeException {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    private Boolean flag;

    /**
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this.message = message;
        this.flag = false;
    }

    /**
     * @param message 错误消息
     * @param code    错误码
     */
    public BusinessException(String message, Integer code) {
        this.message = message;
        this.code = code;
        this.flag = false;
    }

    /**
     * @param message 错误消息
     * @param code    错误码
     * @param cause   原始异常对象
     */
    public BusinessException(String message, Integer code, Throwable cause) {
        super(cause);
        this.message = message;
        this.code = code;
        this.flag = false;
    }

    /**
     * @param resultCodeEnum 接收枚举类型
     */
    public BusinessException(ResponseEnum resultCodeEnum) {
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
        this.flag = resultCodeEnum.getFlag();
    }

    /**
     * @param resultCodeEnum 接收枚举类型
     * @param cause          原始异常对象
     */
    public BusinessException(ResponseEnum resultCodeEnum, Throwable cause) {
        super(cause);
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
        this.flag = resultCodeEnum.getFlag();
    }

}
