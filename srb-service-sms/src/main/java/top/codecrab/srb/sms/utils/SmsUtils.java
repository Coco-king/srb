package top.codecrab.srb.sms.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.google.gson.Gson;

import java.util.Map;

/**
 * @author codecrab
 * @since 2021年04月26日 15:06
 */
public class SmsUtils {
    public static SendSmsResponse sendSms(String phoneNumbers, String templateCode, Map<String, Object> param) throws Exception {
        Gson gson = new Gson();
        // 使用AK&SK初始化账号Client
        Config config = new Config()
                .setAccessKeyId(SmsProperties.KEY_ID)
                .setAccessKeySecret(SmsProperties.KEY_SECRET);
        // 访问的域名
        config.endpoint = SmsProperties.END_POINT;
        Client client = new Client(config);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setTemplateParam(gson.toJson(param))
                .setTemplateCode(templateCode)
                .setSignName(SmsProperties.SIGN_NAME)
                .setPhoneNumbers(phoneNumbers);
        return client.sendSms(sendSmsRequest);
    }
}
