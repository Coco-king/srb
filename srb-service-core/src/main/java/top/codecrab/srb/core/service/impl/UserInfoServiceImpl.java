package top.codecrab.srb.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.config.Constants;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.core.entity.UserAccount;
import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.entity.UserLoginRecord;
import top.codecrab.srb.core.entity.query.UserInfoQuery;
import top.codecrab.srb.core.entity.vo.LoginVo;
import top.codecrab.srb.core.entity.vo.RegisterVo;
import top.codecrab.srb.core.entity.vo.UserIndexVo;
import top.codecrab.srb.core.entity.vo.UserInfoVo;
import top.codecrab.srb.core.mapper.UserAccountMapper;
import top.codecrab.srb.core.mapper.UserInfoMapper;
import top.codecrab.srb.core.mapper.UserLoginRecordMapper;
import top.codecrab.srb.core.service.UserInfoService;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterVo registerVo) {
        Integer count = baseMapper.selectCount(new QueryWrapper<UserInfo>()
                .eq("mobile", registerVo.getMobile()));

        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        // 插入用户记录
        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVo.getUserType());
        userInfo.setMobile(registerVo.getMobile());
        userInfo.setPassword(SecureUtil.md5(registerVo.getPassword()));
        userInfo.setNickName(registerVo.getMobile());
        userInfo.setName(registerVo.getMobile());
        userInfo.setHeadImg(Constants.USER_DEFAULT_AVATAR);
        userInfo.setStatus(Constants.USER_STATUS_NORMAL);
        baseMapper.insert(userInfo);

        // 插入用户账户记录
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVo login(LoginVo loginVo, String ip) {
        UserInfo userInfo = baseMapper.selectOne(new QueryWrapper<UserInfo>()
                .eq("mobile", loginVo.getMobile())
                .eq("user_type", loginVo.getUserType()));

        // 断言userInfo不为空
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        // 断言密码相同
        Assert.equals(SecureUtil.md5(loginVo.getPassword()), userInfo.getPassword(), ResponseEnum.LOGIN_PASSWORD_ERROR);
        // 断言用户状态为正常
        Assert.equals(userInfo.getStatus(), Constants.USER_STATUS_NORMAL, ResponseEnum.LOGIN_LOCKED_ERROR);

        // 封装用户登录日志对象
        UserLoginRecord record = new UserLoginRecord();
        record.setUserId(userInfo.getId());
        record.setIp(ip);
        userLoginRecordMapper.insert(record);

        // 生成token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());

        // 封装返回到视图层的用户信息
        UserInfoVo infoVo = new UserInfoVo();
        BeanUtil.copyProperties(userInfo, infoVo);
        infoVo.setToken(token);
        return infoVo;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> page, UserInfoQuery userInfoQuery) {
        if (userInfoQuery == null) {
            return baseMapper.selectPage(page, Wrappers.emptyWrapper());
        }

        String mobile = userInfoQuery.getMobile();
        Integer userType = userInfoQuery.getUserType();
        Integer status = userInfoQuery.getStatus();

        return baseMapper.selectPage(page, new QueryWrapper<UserInfo>()
                .eq(StrUtil.isNotBlank(mobile), "mobile", mobile)
                .eq(userType != null, "user_type", userType)
                .eq(status != null, "status", status)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public UserIndexVo getIndexUserInfo(Long userId) {

        UserInfo userInfo = baseMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.WEIXIN_FETCH_USERINFO_ERROR);

        UserLoginRecord loginRecord = userLoginRecordMapper.selectOne(new QueryWrapper<UserLoginRecord>()
                .eq("user_id", userId).orderByDesc("id").last(" limit 1, 1 "));

        UserAccount userAccount = userAccountMapper.selectOne(new QueryWrapper<UserAccount>()
                .eq("user_id", userId));

        UserIndexVo indexVo = new UserIndexVo();
        BeanUtil.copyProperties(userInfo, indexVo);
        BeanUtil.copyProperties(userAccount, indexVo);
        indexVo.setLastLoginTime(loginRecord == null ? LocalDateTime.now() : loginRecord.getCreateTime());
        return indexVo;
    }

    @Override
    public String getMobileByBindCode(String bindCode) {
        UserInfo userInfo = baseMapper.selectOne(new QueryWrapper<UserInfo>()
                .eq("bind_code", bindCode));
        return userInfo == null ? "" : userInfo.getMobile();
    }
}
