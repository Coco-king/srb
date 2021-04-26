package top.codecrab.srb.sms.service.impl;

import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.excetion.BusinessException;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.sms.service.SmsService;
import top.codecrab.srb.sms.utils.SmsUtils;

import java.util.Map;

/**
 * @author codecrab
 * @since 2021年04月26日 16:06
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public void sendSms(String phoneNumbers, String templateCode, Map<String, Object> param) {
        SendSmsResponse response;
        try {
            response = SmsUtils.sendSms(phoneNumbers, templateCode, param);
        } catch (Exception e) {
            log.error("阿里云短信发送SDK调用失败：{}", e.getMessage());
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);
        }

        // 获取响应体
        SendSmsResponseBody body = response.getBody();
        // 获取操作码
        String code = body.getCode();
        log.info("信息发送成功，响应信息头：{}", response.getHeaders());
        log.info("信息发送成功，请求体为：{}", JSONUtil.toJsonStr(body));

        //ALIYUN_SMS_LIMIT_CONTROL_ERROR(-502, "短信发送过于频繁"),业务限流
        Assert.notEquals("isv.BUSINESS_LIMIT_CONTROL", code, ResponseEnum.ALIYUN_SMS_LIMIT_CONTROL_ERROR);
        //ALIYUN_SMS_MOBILE_NUMBER_ILLEGAL_ERROR ,非法手机号
        Assert.notEquals("isv.MOBILE_NUMBER_ILLEGAL", code, ResponseEnum.ALIYUN_SMS_MOBILE_NUMBER_ILLEGAL_ERROR);
        //ALIYUN_SMS_ERROR(-503, "短信发送失败"),其他失败
        Assert.equals("OK", code, ResponseEnum.ALIYUN_SMS_ERROR);
    }
}
