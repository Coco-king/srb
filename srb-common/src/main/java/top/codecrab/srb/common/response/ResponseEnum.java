package top.codecrab.srb.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author codecrab
 * @since 2021年04月23日 8:53
 */
@Getter
@ToString
@AllArgsConstructor
public enum ResponseEnum {

    /**
     * 返回给前端数据的枚举
     */
    SUCCESS(0, true, "操作成功"),
    FAIL(-1, false, "操作失败"),
    ERROR(-99999, false, "服务器内部错误"),

    /**
     * -1xx 服务器错误
     */
    BAD_SQL_GRAMMAR_ERROR(-101, false, "SQL语法错误"),
    SERVLET_ERROR(-102, false, "Servlet请求异常"),
    UPLOAD_ERROR(-103, false, "文件上传错误"),
    EXPORT_DATA_ERROR(104, false, "数据导出失败"),
    IMPORT_DATA_ERROR(105, false, "数据导入失败"),
    FILE_TYPE_MISMATCH_ERROR(106, false, "需要的文件类型与上传的不匹配"),
    EXCEL_TYPE_MISMATCH_ERROR(107, false, "EXCEL文件格式有误或已损坏"),

    /**
     * -2xx 参数校验
     */
    BORROW_AMOUNT_NULL_ERROR(-201, false, "借款额度不能为空"),
    MOBILE_NULL_ERROR(-202, false, "手机号码不能为空"),
    MOBILE_ERROR(-203, false, "手机号码不正确"),
    PASSWORD_NULL_ERROR(204, false, "密码不能为空"),
    CODE_NULL_ERROR(205, false, "验证码不能为空"),
    CODE_ERROR(206, false, "验证码错误"),
    MOBILE_EXIST_ERROR(207, false, "手机号已被注册"),
    LOGIN_MOBILE_ERROR(208, false, "用户不存在"),
    LOGIN_PASSWORD_ERROR(209, false, "密码错误"),
    LOGIN_LOCKED_ERROR(210, false, "用户被锁定"),
    LOGIN_AUTH_ERROR(-211, false, "未登录"),
    INTEGRAL_START_AMOUNT_NULL_ERROR(-212, false, "积分区间开始不能为空"),
    INTEGRAL_END_AMOUNT_NULL_ERROR(-213, false, "积分区间结束不能为空"),
    LAZY_LOAD_WITH_PARENT_ID_NULL_ERROR(-214, false, "指定为懒加载模式时父ID不能为空"),

    USER_BIND_ID_CARD_EXIST_ERROR(-301, false, "身份证号码已绑定"),
    USER_NO_BIND_ERROR(302, false, "用户未绑定"),
    USER_NO_AMOUNT_ERROR(303, false, "用户信息未审核"),
    USER_AMOUNT_LESS_ERROR(304, false, "您的借款额度不足"),
    LEND_INVEST_ERROR(305, false, "当前状态无法投标"),
    LEND_FULL_SCALE_ERROR(306, false, "已满标，无法投标"),
    NOT_SUFFICIENT_FUNDS_ERROR(307, false, "余额不足，请充值"),

    PAY_UNIFIED_ORDER_ERROR(401, false, "统一下单错误"),

    /**
     * 业务限流
     */
    ALIYUN_SMS_LIMIT_CONTROL_ERROR(-502, false, "短信发送过于频繁"),
    /**
     * 其他失败
     */
    ALIYUN_SMS_ERROR(-503, false, "短信发送失败"),
    ALIYUN_SMS_MOBILE_NUMBER_ILLEGAL_ERROR(-504, false, "非法手机号"),

    WEIXIN_CALLBACK_PARAM_ERROR(-601, false, "回调参数不正确"),
    WEIXIN_FETCH_ACCESS_TOKEN_ERROR(-602, false, "获取ACCESS_TOKEN失败"),
    WEIXIN_FETCH_USERINFO_ERROR(-603, false, "获取用户信息失败");

    private final Integer code;

    private final Boolean success;

    private final String message;
}
