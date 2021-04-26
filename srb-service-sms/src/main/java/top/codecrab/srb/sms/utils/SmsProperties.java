package top.codecrab.srb.sms.utils;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author codecrab
 * @since 2021年04月26日 15:03
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsProperties implements InitializingBean {

    private String endPoint;
    private String keyId;
    private String keySecret;
    private String templateCode;
    private String signName;

    public static String END_POINT;
    public static String KEY_ID;
    public static String KEY_SECRET;
    public static String TEMPLATE_CODE;
    public static String SIGN_NAME;

    /**
     * 当私有成员被赋值后，此方法自动被调用，从而初始化常量
     */
    @Override
    public void afterPropertiesSet() {
        END_POINT = endPoint;
        KEY_ID = keyId;
        KEY_SECRET = keySecret;
        TEMPLATE_CODE = templateCode;
        SIGN_NAME = signName;
    }
}
