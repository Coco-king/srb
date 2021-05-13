package top.codecrab.srb.core.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.srb.amqp.config.AmqpConstants;
import top.codecrab.srb.amqp.service.AmqpService;
import top.codecrab.srb.base.entity.dto.SmsDTO;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.core.entity.UserAccount;
import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.entity.bo.TransFlowBo;
import top.codecrab.srb.core.enums.TransTypeEnum;
import top.codecrab.srb.core.hfb.FormHelper;
import top.codecrab.srb.core.hfb.HfbConst;
import top.codecrab.srb.core.hfb.RequestHelper;
import top.codecrab.srb.core.mapper.UserAccountMapper;
import top.codecrab.srb.core.service.TransFlowService;
import top.codecrab.srb.core.service.UserAccountService;
import top.codecrab.srb.core.service.UserBindService;
import top.codecrab.srb.core.service.UserInfoService;
import top.codecrab.srb.core.utils.LendNoUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserBindService userBindService;

    @Autowired
    private TransFlowService transFlowService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AmqpService amqpService;

    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {

        UserInfo userInfo = userInfoService.getById(userId);

        Map<String, Object> params = new HashMap<>(16);
        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentBillNo", LendNoUtils.getChargeNo());
        params.put("bindCode", userInfo.getBindCode());
        params.put("chargeAmt", chargeAmt);
        params.put("feeAmt", BigDecimal.ZERO);
        params.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        params.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        params.put("timestamp", RequestHelper.getTimestamp());
        params.put("sign", RequestHelper.getSign(params));

        return FormHelper.buildForm(HfbConst.RECHARGE_URL, params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notify(Map<String, Object> paramMap) {
        String agentBillNo = MapUtil.get(paramMap, "agentBillNo", String.class);
        if (transFlowService.isTransNoExist(agentBillNo)) {
            log.warn("幂等性返回");
            return "success";
        }

        // 流水号不存在，更新账户余额，添加交易流水
        String bindCode = MapUtil.get(paramMap, "bindCode", String.class);
        BigDecimal chargeAmt = MapUtil.get(paramMap, "chargeAmt", BigDecimal.class);
        baseMapper.updateAccount(bindCode, chargeAmt, BigDecimal.ZERO);

        // 生成交易流水
        transFlowService.saveTransFlow(
                new TransFlowBo(
                        agentBillNo,
                        bindCode,
                        userBindService.getUserIdByBindCode(bindCode).toString(),
                        chargeAmt,
                        TransTypeEnum.RECHARGE,
                        "账户充值"
                )
        );

        log.info("向rabbitMq发送消息：充值金额{}元", chargeAmt);

        amqpService.sendMessage(
                AmqpConstants.EXCHANGE_TOPIC_SMS,
                AmqpConstants.ROUTING_SMS_ITEM,
                new SmsDTO(
                        userInfoService.getMobileByBindCode(bindCode),
                        StrUtil.format("Recharge{}", chargeAmt)
                )
        );

        return "success";
    }

    @Override
    public BigDecimal getAccount(Long userId) {
        UserAccount userAccount = baseMapper.selectOne(new QueryWrapper<UserAccount>()
                .eq("user_id", userId));

        Assert.notNull(userAccount, ResponseEnum.LOGIN_MOBILE_ERROR);
        return userAccount.getAmount();
    }

    @Override
    public void updateAccount(String bindCode, BigDecimal amount, BigDecimal freezeAmount) {
        baseMapper.updateAccount(bindCode, amount, freezeAmount);
    }

    @Override
    public String commitWithdraw(BigDecimal fetchAmt, Long userId) {

        BigDecimal account = userAccountService.getAccount(userId);
        Assert.isTrue(fetchAmt.compareTo(account) <= 0, ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        String bindCode = userBindService.getBindCodeByUserId(userId);

        Map<String, Object> paramMap = new HashMap<>(9, 1);
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("fetchAmt", fetchAmt);
        paramMap.put("feeAmt", new BigDecimal(0));
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));
        //构建自动提交表单
        return FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notifyWithdraw(Map<String, Object> paramMap) {

        String agentBillNo = MapUtil.get(paramMap, "agentBillNo", String.class);
        if (transFlowService.isTransNoExist(agentBillNo)) {
            log.warn("幂等性返回");
            return "fail";
        }

        String bindCode = MapUtil.get(paramMap, "bindCode", String.class);
        BigDecimal fetchAmt = MapUtil.get(paramMap, "fetchAmt", BigDecimal.class);

        //根据用户账户修改账户金额
        baseMapper.updateAccount(bindCode, fetchAmt.negate(), BigDecimal.ZERO);

        //增加交易流水
        transFlowService.saveTransFlow(new TransFlowBo(
                agentBillNo,
                bindCode,
                userBindService.getUserIdByBindCode(bindCode).toString(),
                fetchAmt,
                TransTypeEnum.WITHDRAW,
                fetchAmt + "元提现成功"
        ));

        return "success";
    }
}
