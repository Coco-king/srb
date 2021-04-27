package top.codecrab.srb.core.service;

import top.codecrab.srb.core.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.vo.LoginVo;
import top.codecrab.srb.core.entity.vo.RegisterVo;
import top.codecrab.srb.core.entity.vo.UserInfoVo;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户注册
     *
     * @param registerVo 用户实体类
     */
    void register(RegisterVo registerVo);

    /**
     * 用户登录
     *
     * @param loginVo 登录实体类
     * @param ip      登录ip
     * @return 封装好的userInfoVo
     */
    UserInfoVo login(LoginVo loginVo, String ip);
}
