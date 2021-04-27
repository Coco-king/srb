package top.codecrab.srb.sms.controller.api;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.common.config.Constants;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.sms.controller.base.SmsBaseController;
import top.codecrab.srb.sms.utils.SmsProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author codecrab
 * @since 2021年04月26日 16:24
 */
@Api(tags = "短信管理")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/sms")
public class ApiSmsControllerSms extends SmsBaseController {

    @ApiOperation("获取验证码")
    @GetMapping("/send/{mobile}")
    public Result sendSms(
            @ApiParam(value = "手机号", required = true)
            @PathVariable String mobile
    ) {
        //断言手机号码不为空
        Assert.notBlank(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //断言是中国的手机号码
        Assert.isTrue(PhoneUtil.isMobile(mobile), ResponseEnum.MOBILE_ERROR);

        String code = RandomUtil.randomNumbers(4);
        smsService.sendSms(mobile, SmsProperties.TEMPLATE_CODE, MapUtil.of("code", code));
        redisTemplate.opsForValue().set(Constants.REDIS_SRB_SMS_CODE_KEY + mobile, code, 5, TimeUnit.MINUTES);
        return Result.ok("验证码发送成功");
    }
}
