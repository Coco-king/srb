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

    private Boolean flag;

    private Map<String, Object> data = new HashMap<>();

    private Result(Integer code, String message, boolean flag) {
        this.code = code;
        this.message = message;
        this.flag = flag;
    }

    /**
     * 返回成功
     */
    public static Result ok() {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                ResponseEnum.SUCCESS.getFlag()

        );
    }

    /**
     * 返回成功
     */
    public static Result ok(String message) {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                message, ResponseEnum.SUCCESS.getFlag()
        );
    }

    /**
     * 返回成功
     */
    public static Result ok(Map<String, Object> data) {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                ResponseEnum.SUCCESS.getFlag(), data
        );
    }

    /**
     * 返回成功
     */
    public static Result ok(String key, Object value) {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                ResponseEnum.SUCCESS.getFlag(),
                MapUtil.of(key, value)
        );
    }

    /**
     * 返回成功
     */
    public static Result ok(String message, String key, Object value) {
        return new Result(
                ResponseEnum.SUCCESS.getCode(),
                message, ResponseEnum.SUCCESS.getFlag(),
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
                ResponseEnum.ERROR.getFlag()
        );
    }

    /**
     * 服务器异常
     */
    public static Result error(Integer code, String message, Boolean flag) {
        return new Result(code, message, flag);
    }

    /**
     * 返回失败
     */
    public static Result fail() {
        return new Result(
                ResponseEnum.FAIL.getCode(),
                ResponseEnum.FAIL.getMessage(),
                ResponseEnum.FAIL.getFlag()
        );
    }

    /**
     * 返回失败
     */
    public static Result fail(String message) {
        return new Result(
                ResponseEnum.FAIL.getCode(),
                message,
                ResponseEnum.FAIL.getFlag()
        );
    }

    /**
     * 设置特定结果
     */
    public static Result setResult(ResponseEnum responseEnum) {
        return new Result(
                responseEnum.getCode(),
                responseEnum.getMessage(),
                responseEnum.getFlag()
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

    public Result flag(Boolean flag) {
        this.setFlag(flag);
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
