package top.codecrab.srb.sms.client.rollback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.codecrab.srb.sms.client.CoreUserInfoClient;

/**
 * @author codecrab
 * @since 2021年04月29日 16:44
 */
@Slf4j
@Service
public class CoreUserInfoClientRollback implements CoreUserInfoClient {
    @Override
    public boolean checkMobile(String mobile) {
        log.error("远程调用checkMobile失败。。。");
        return false;
    }
}
