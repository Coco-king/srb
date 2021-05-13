package top.codecrab.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.BorrowInfo;
import top.codecrab.srb.core.entity.vo.BorrowInfoApprovalVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    /**
     * 获取借款额度
     *
     * @param userId 当前登录的用户id
     * @return 借款额度
     */
    BigDecimal getBorrowAmount(Long userId);

    /**
     * 保存借款额度申请
     *
     * @param borrowInfo 借款额度bean对象
     * @param userId     当前登录用户id
     */
    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    /**
     * 查询BorrowInfo扩展的列表
     *
     * @return BorrowInfo扩展列表
     */
    List<BorrowInfo> selectList();

    /**
     * 查询借款申请详情
     *
     * @param id 借款申请id
     * @return 详情
     */
    Map<String, Object> getBorrowInfoDetail(Long id);

    /**
     * 审批借款申请
     *
     * @param vo 借款申请vo
     */
    void approval(BorrowInfoApprovalVo vo);
}
