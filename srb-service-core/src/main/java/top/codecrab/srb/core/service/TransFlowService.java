package top.codecrab.srb.core.service;

import top.codecrab.srb.core.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.bo.TransFlowBo;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface TransFlowService extends IService<TransFlow> {

    /**
     * 生成交易流水
     *
     * @param transFlowBo 交易流水对象
     */
    void saveTransFlow(TransFlowBo transFlowBo);

    /**
     * 流水号是否存在
     *
     * @param agentBillNo 流水号
     * @return true：在 false：不在
     */
    boolean isTransNoExist(String agentBillNo);

    /**
     * 获取列表
     *
     * @param userId 用户id
     * @return 交易流水列表
     */
    List<TransFlow> selectByUserId(Long userId);
}
