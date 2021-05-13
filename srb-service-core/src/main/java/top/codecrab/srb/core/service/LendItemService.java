package top.codecrab.srb.core.service;

import org.apache.ibatis.annotations.Param;
import top.codecrab.srb.core.entity.LendItem;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.vo.InvestVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface LendItemService extends IService<LendItem> {

    /**
     * 会员投资提交数据
     *
     * @param investVo 投资标的金额
     * @return form表单字符串
     */
    String commitInvest(InvestVo investVo);

    /**
     * 用户投标异步回调
     *
     * @param paramMap 回调参数
     * @return 是否成功
     */
    String notify(Map<String, Object> paramMap);

    /**
     * 获取投标对应的列表
     *
     * @param lendId 标的id
     * @return 列表
     */
    List<LendItem> selectByLendId(Long lendId);
}
