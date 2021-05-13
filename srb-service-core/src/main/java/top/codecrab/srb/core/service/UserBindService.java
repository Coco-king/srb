package top.codecrab.srb.core.service;

import top.codecrab.srb.core.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.vo.UserBindVo;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface UserBindService extends IService<UserBind> {

    /**
     * 绑定会员
     *
     * @param userBindVo 用户绑定信息
     * @param userId     用户id
     * @return 拼接完成的form表单
     */
    String commitBindUser(UserBindVo userBindVo, Long userId);

    /**
     * 根据参数列表修改user_info表和user_bind表数据
     *
     * @param params 参数列表
     */
    void notify(Map<String, Object> params);

    /**
     * 根据userId获取用户绑定号
     *
     * @param userId 用户ID
     * @return 绑定号
     */
    String getBindCodeByUserId(Long userId);

    /**
     * 根据用户绑定号获取userId
     *
     * @param bindCode 用户绑定号
     * @return userId
     */
    Long getUserIdByBindCode(String bindCode);
}
