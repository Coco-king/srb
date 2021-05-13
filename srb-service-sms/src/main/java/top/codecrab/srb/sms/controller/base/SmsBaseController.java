package top.codecrab.srb.sms.controller.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.codecrab.srb.sms.client.CoreUserInfoClient;
import top.codecrab.srb.sms.service.SmsService;

/**
 * @author codecrab
 * @since 2021年04月26日 16:24
 */
public class SmsBaseController {
    @Autowired
    protected SmsService smsService;
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;
    @Autowired
    protected CoreUserInfoClient userInfoClient;
}
