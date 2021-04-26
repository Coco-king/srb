package top.codecrab.srb.common.response;

import cn.hutool.core.map.MapUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * AccessLevel.PRIVATE：构造器私有
 *
 * @author codecrab
 * @since 2021年04月23日 8:45
 */
@Data
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result {

    private Integer code;

    private String message;

    private Boolean success;

    private Map<String, Object> data = new HashMap<>();

    private Result(Integer code, String message, Boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    /**
     * 返回成功
     */
    public static Result ok() {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                ResponseEnum.SUCCESS.getSuccess()

        );
    }

    /**
     * 返回成功
     */
    public static Result ok(String message) {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                message, ResponseEnum.SUCCESS.getSuccess()
        );
    }

    /**
     * 返回成功
     */
    public static Result ok(Map<String, Object> data) {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                ResponseEnum.SUCCESS.getSuccess(), data
        );
    }

    /**
     * 返回成功
     */
    public static Result ok(String key, Object value) {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                ResponseEnum.SUCCESS.getSuccess(),
                MapUtil.of(key, value)
        );
    }

    /**
     * 返回成功
     */
    public static Result ok(String message, String key, Object value) {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                message, ResponseEnum.SUCCESS.getSuccess(),
                MapUtil.of(key, value)
        );
    }

    /**
     * 服务器异常
     */
    public static Result error() {
        return new Result(
                ResponseEnum.ERROR.getCode(),
                ResponseEnum.ERROR.getMessage(),
                ResponseEnum.ERROR.getSuccess()
        );
    }

    /**
     * 服务器异常
     */
    public static Result error(Integer code, String message, Boolean success) {
        return new Result(code, message, success);
    }

    /**
     * 返回失败
     */
    public static Result fail() {
        return new Result(
                ResponseEnum.FAIL.getCode(),
                ResponseEnum.FAIL.getMessage(),
                ResponseEnum.FAIL.getSuccess()
        );
    }

    /**
     * 返回失败
     */
    public static Result fail(String message) {
        return new Result(
                ResponseEnum.FAIL.getCode(),
                message,
                ResponseEnum.FAIL.getSuccess()
        );
    }

    /**
     * 设置特定结果
     */
    public static Result setResult(ResponseEnum responseEnum) {
        return new Result(
                responseEnum.getCode(),
                responseEnum.getMessage(),
                responseEnum.getSuccess()
        );
    }

    public Result message(String message) {
        this.setMessage(message);
        return this;
    }

    public Result code(Integer code) {
        this.setCode(code);
        return this;
    }

    public Result success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public Result data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public Result data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }
}
