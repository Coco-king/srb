package top.codecrab.srb.core.service.impl;

import top.codecrab.srb.core.entity.UserBind;
import top.codecrab.srb.core.mapper.UserBindMapper;
import top.codecrab.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

}
