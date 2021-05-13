package top.codecrab.srb.core.service;

import org.apache.ibatis.annotations.Param;
import top.codecrab.srb.core.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface UserAccountService extends IService<UserAccount> {

    /**
     * 把充值请求生成form表单并提交
     *
     * @param chargeAmt 充值金额
     * @param userId    充值的用户id
     * @return form表单字符串
     */
    String commitCharge(BigDecimal chargeAmt, Long userId);

    /**
     * 充值成功回调
     *
     * @param paramMap 回调参数
     * @return 是否成功
     */
    String notify(Map<String, Object> paramMap);

    /**
     * 根据userId查询账户余额
     *
     * @param userId 用户id
     * @return 账户余额
     */
    BigDecimal getAccount(Long userId);

    /**
     * 更新账户余额和冻结余额
     *
     * @param bindCode     用户绑定协议号
     * @param amount       要更新的账户余额 正数为加，负数为减
     * @param freezeAmount 要更新的冻结余额 正数为加，负数为减
     */
    void updateAccount(
            @Param("bindCode") String bindCode,
            @Param("amount") BigDecimal amount,
            @Param("freezeAmount") BigDecimal freezeAmount
    );

    /**
     * 用户提现
     *
     * @param fetchAmt 提现金额
     * @param userId   用户id
     * @return 表单的str形式
     */
    String commitWithdraw(BigDecimal fetchAmt, Long userId);

    /**
     * 用户提现异步回调
     *
     * @param paramMap 回调参数
     * @return 是否成功 成功：success 失败：任意字符串
     */
    String notifyWithdraw(Map<String, Object> paramMap);
}
