package top.codecrab.srb.sms.service;

import java.util.Map;

/**
 * @author codecrab
 * @since 2021年04月26日 16:03
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @param phoneNumbers 手机号
     * @param templateCode 模板Id
     * @param param        短信参数。Json类型：{"code":"123456"}
     */
    void sendSms(String phoneNumbers, String templateCode, Map<String, Object> param);
}
