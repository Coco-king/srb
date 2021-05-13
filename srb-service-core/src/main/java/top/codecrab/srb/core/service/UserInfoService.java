package top.codecrab.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.codecrab.srb.core.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.query.UserInfoQuery;
import top.codecrab.srb.core.entity.vo.LoginVo;
import top.codecrab.srb.core.entity.vo.RegisterVo;
import top.codecrab.srb.core.entity.vo.UserIndexVo;
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

    /**
     * 分页条件查询用户列表
     *
     * @param page          分页对象
     * @param userInfoQuery 条件对象
     * @return 用户列表
     */
    IPage<UserInfo> listPage(Page<UserInfo> page, UserInfoQuery userInfoQuery);

    /**
     * 解锁/锁定用户
     *
     * @param id     用户id
     * @param status 用户状态
     */
    void lock(Long id, Integer status);

    /**
     * 获取用户个人空间数据
     *
     * @param userId 用户id
     * @return 用户个人空间VO
     */
    UserIndexVo getIndexUserInfo(Long userId);

    /**
     * 根据bindCode获取手机号
     *
     * @param bindCode 绑定协议号
     * @return 手机号
     */
    String getMobileByBindCode(String bindCode);
}
