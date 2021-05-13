package top.codecrab.srb.sms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.codecrab.srb.sms.client.rollback.CoreUserInfoClientRollback;

/**
 * @author codecrab
 * @since 2021年04月29日 15:48
 */
@FeignClient(value = "service-core", fallback = CoreUserInfoClientRollback.class)
public interface CoreUserInfoClient {

    /**
     * 校验手机号是否已注册
     *
     * @param mobile 手机号
     * @return true：已注册 false：未注册
     */
    @GetMapping("/api/core/userInfo/checkMobile/{mobile}")
    boolean checkMobile(@PathVariable("mobile") String mobile);

}
