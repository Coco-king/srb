package top.codecrab.srb.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.codecrab.srb.core.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.vo.BorrowerApprovalVo;
import top.codecrab.srb.core.entity.vo.BorrowerDetailVo;
import top.codecrab.srb.core.entity.vo.BorrowerVo;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface BorrowerService extends IService<Borrower> {

    /**
     * 保存借款人信息
     *
     * @param borrowerVo 借款人信息
     * @param userId     用户id
     */
    void saveBorrowerVoByUserId(BorrowerVo borrowerVo, Long userId);

    /**
     * 根据登录的用户id查询认证状态
     *
     * @param userId 用户id
     * @return 认证状态
     */
    Integer getStatusByUserId(Long userId);

    /**
     * 根据关键字分页查询借款人认证列表
     *
     * @param page    分页bean
     * @param keyword 搜索关键词
     * @return 借款人认证列表
     */
    Page<Borrower> listPage(Page<Borrower> page, String keyword);

    /**
     * 查询借款人认证详情信息
     *
     * @param id        认证id
     * @param hasAttach 是否包含Attach附件
     * @return 详情信息
     */
    BorrowerDetailVo getBorrowerDetailVo(Long id, boolean hasAttach);

    /**
     * 借款额度审批
     *
     * @param vo 借款额度的审核状态
     */
    void approval(BorrowerApprovalVo vo);
}
