package top.codecrab.srb.core.service.impl;

import top.codecrab.srb.core.entity.UserAccount;
import top.codecrab.srb.core.mapper.UserAccountMapper;
import top.codecrab.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

}
