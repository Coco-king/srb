package top.codecrab.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.Lend;
import top.codecrab.srb.core.entity.LendItemReturn;
import top.codecrab.srb.core.entity.LendReturn;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface LendItemReturnService extends IService<LendItemReturn> {

    /**
     * 获取标的出借回款记录列表
     *
     * @param lendId 标的id
     * @param userId 用户id
     * @return 出借回款记录列表
     */
    List<LendItemReturn> selectByLendId(Long lendId, Long userId);

    /**
     * 还款明细
     *
     * @param lendReturn 标的还款
     * @param lend       所属标的
     * @return 还款明细
     */
    List<Map<String, Object>> addReturnDetail(LendReturn lendReturn, Lend lend);
}
