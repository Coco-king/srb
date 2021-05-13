package top.codecrab.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.BorrowInfo;
import top.codecrab.srb.core.entity.Lend;
import top.codecrab.srb.core.entity.vo.BorrowInfoApprovalVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface LendService extends IService<Lend> {

    /**
     * 新建标的
     *
     * @param vo         页面传入的信息
     * @param borrowInfo 借款人申请信息
     */
    void createLend(BorrowInfoApprovalVo vo, BorrowInfo borrowInfo);

    /**
     * 查询标的列表
     *
     * @return 标的列表
     */
    List<Lend> selectList();

    /**
     * 根据标的id查询标的详情和借款申请详情
     *
     * @param id 标的id
     * @return 封装为map
     */
    Map<String, Object> selectMap(Long id);

    /**
     * 计算投资收益
     *
     * @param invest       本金
     * @param yearRate     年化率
     * @param totalMonth   期数
     * @param returnMethod 还款方式
     * @return 收益
     */
    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalMonth, Integer returnMethod);

    /**
     * 放款
     *
     * @param id 标的id
     */
    void makeLoan(Long id);
}
