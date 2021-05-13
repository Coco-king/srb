package top.codecrab.srb.core.mapper;

import org.apache.ibatis.annotations.Param;
import top.codecrab.srb.core.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

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
}
