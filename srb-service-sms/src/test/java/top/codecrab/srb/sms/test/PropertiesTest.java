package top.codecrab.srb.sms.test;

import cn.hutool.core.map.MapUtil;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.codecrab.srb.sms.utils.SmsProperties;
import top.codecrab.srb.sms.utils.SmsUtils;

/**
 * @author codecrab
 * @since 2021年04月26日 15:18
 */
@SpringBootTest
public class PropertiesTest {
    @Test
    void testProp() {
        System.out.println(SmsProperties.KEY_ID);
        System.out.println(SmsProperties.KEY_SECRET);
        System.out.println(SmsProperties.END_POINT);
        System.out.println(SmsProperties.SIGN_NAME);
        System.out.println(SmsProperties.TEMPLATE_CODE);
    }

    @Test
    void testSendMsg() {
        try {
            SendSmsResponse response = SmsUtils.sendSms(
                    "17122840624",
                    SmsProperties.TEMPLATE_CODE,
                    MapUtil.of("code", 333456));
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
