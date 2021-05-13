package top.codecrab.srb.common.excetion;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import top.codecrab.srb.common.response.ResponseEnum;

/**
 * @author codecrab
 */
@Slf4j
public abstract class Assert {

    /**
     * 断言对象不为空
     * 如果对象obj为空，则抛出异常
     *
     * @param obj 待判断对象
     */
    public static void notNull(Object obj, ResponseEnum responseEnum) {
        if (ObjectUtil.isNull(obj)) {
            log.info("obj is null...............");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言对象为空
     * 如果对象obj不为空，则抛出异常
     */
    public static void isNull(Object object, ResponseEnum responseEnum) {
        if (ObjectUtil.isNotNull(object)) {
            log.info("obj is not null......");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言表达式为真
     * 如果不为真，则抛出异常
     *
     * @param expression 是否成功
     */
    public static void isTrue(Boolean expression, ResponseEnum responseEnum) {
        if (BooleanUtil.isFalse(expression)) {
            log.info("fail...............");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言表达式为假
     * 如果不为假，则抛出异常
     *
     * @param expression 是否成功
     */
    public static void isFalse(Boolean expression, ResponseEnum responseEnum) {
        if (BooleanUtil.isTrue(expression)) {
            log.info("true...............");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言两个对象不相等
     * 如果相等，则抛出异常
     */
    public static void notEquals(Object m1, Object m2, ResponseEnum responseEnum) {
        if (ObjectUtil.equals(m1, m2)) {
            log.info("equals...............");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言两个对象相等
     * 如果不相等，则抛出异常
     */
    public static void equals(Object m1, Object m2, ResponseEnum responseEnum) {
        if (ObjectUtil.notEqual(m1, m2)) {
            log.info("not equals...............");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言参数不为空
     * 如果为空，则抛出异常
     */
    public static void notEmpty(String s, ResponseEnum responseEnum) {
        if (StrUtil.isEmpty(s)) {
            log.info("is empty...............");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言参数不为空白字符
     * 如果为空白字符，则抛出异常
     */
    public static void notBlank(String s, ResponseEnum responseEnum) {
        if (StrUtil.isBlank(s)) {
            log.info("is blank...............");
            throw new BusinessException(responseEnum);
        }
    }
}
