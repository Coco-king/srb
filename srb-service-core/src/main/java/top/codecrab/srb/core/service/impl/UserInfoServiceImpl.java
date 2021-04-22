package top.codecrab.srb.core.service.impl;

import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.mapper.UserInfoMapper;
import top.codecrab.srb.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
