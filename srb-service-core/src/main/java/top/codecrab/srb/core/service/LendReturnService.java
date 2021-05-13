package top.codecrab.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.LendReturn;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface LendReturnService extends IService<LendReturn> {

    /**
     * 获取还款记录列表
     *
     * @param lendId 标的id
     * @return 还款记录列表
     */
    List<LendReturn> selectByLendId(Long lendId);

    /**
     * 用户还款
     *
     * @param lendReturn 标的还款
     * @param userId       用户id
     * @return 自动提交的form表单
     */
    String commitReturn(LendReturn lendReturn, Long userId);

    /**
     * 还款异步回调
     *
     * @param paramMap 回调参数
     * @return 自动提交的form表单
     */
    String notify(Map<String, Object> paramMap);
}
