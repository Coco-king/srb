package top.codecrab.srb.core.service.impl;

import top.codecrab.srb.core.entity.UserLoginRecord;
import top.codecrab.srb.core.mapper.UserLoginRecordMapper;
import top.codecrab.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

}
