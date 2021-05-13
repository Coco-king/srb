package top.codecrab.srb.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.core.entity.UserBind;
import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.entity.vo.UserBindVo;
import top.codecrab.srb.core.enums.UserBindEnum;
import top.codecrab.srb.core.hfb.FormHelper;
import top.codecrab.srb.core.hfb.HfbConst;
import top.codecrab.srb.core.hfb.RequestHelper;
import top.codecrab.srb.core.mapper.UserBindMapper;
import top.codecrab.srb.core.mapper.UserInfoMapper;
import top.codecrab.srb.core.service.UserBindService;

import javax.annotation.Resource;
import java.util.Map;

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

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String commitBindUser(UserBindVo userBindVo, Long userId) {

        UserBind userBind = baseMapper.selectOne(new QueryWrapper<UserBind>()
                .eq("id_card", userBindVo.getIdCard())
                .ne("user_id", userId));

        // 如果身份证相同但是用户名不同就抛出身份证已绑定的异常
        Assert.isNull(userBind, ResponseEnum.USER_BIND_ID_CARD_EXIST_ERROR);

        userBind = baseMapper.selectOne(new QueryWrapper<UserBind>()
                .eq("user_id", userId));

        // 没有未绑定的记录则新建，否则就更新
        if (userBind == null) {
            userBind = new UserBind();
        }
        BeanUtil.copyProperties(userBindVo, userBind);
        userBind.setUserId(userId);
        userBind.setName(userBindVo.getPersonalName());
        userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
        this.saveOrUpdate(userBind);

        Map<String, Object> params = BeanUtil.beanToMap(userBindVo);

        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentUserId", userId);
        params.put("returnUrl", HfbConst.USER_BIND_RETURN_URL);
        params.put("notifyUrl", HfbConst.USER_BIND_NOTIFY_URL);
        params.put("timestamp", RequestHelper.getTimestamp());
        params.put("sign", RequestHelper.getSign(params));

        return FormHelper.buildForm(HfbConst.USER_BIND_URL, params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(Map<String, Object> params) {
        String bindCode = params.get("bindCode").toString();
        String userId = params.get("agentUserId").toString();

        UserBind userBind = baseMapper.selectOne(new QueryWrapper<UserBind>()
                .eq("user_id", userId));
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        UserInfo userInfo = userInfoMapper.selectById(userId);
        userInfo.setBindCode(bindCode);
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public String getBindCodeByUserId(Long userId) {
        UserBind userBind = baseMapper.selectOne(new QueryWrapper<UserBind>()
                .select("bind_code").eq("user_id", userId));
        return userBind == null ? "" : userBind.getBindCode();
    }

    @Override
    public Long getUserIdByBindCode(String bindCode) {
        UserBind userBind = baseMapper.selectOne(new QueryWrapper<UserBind>()
                .select("user_id").eq("bind_code", bindCode));
        return userBind == null ? 0L : userBind.getUserId();
    }
}
