package top.codecrab.srb.core.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.core.entity.Lend;
import top.codecrab.srb.core.entity.LendItem;
import top.codecrab.srb.core.entity.TransFlow;
import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.entity.bo.TransFlowBo;
import top.codecrab.srb.core.entity.vo.InvestVo;
import top.codecrab.srb.core.enums.LendItemStatusEnum;
import top.codecrab.srb.core.enums.LendStatusEnum;
import top.codecrab.srb.core.enums.TransTypeEnum;
import top.codecrab.srb.core.hfb.FormHelper;
import top.codecrab.srb.core.hfb.HfbConst;
import top.codecrab.srb.core.hfb.RequestHelper;
import top.codecrab.srb.core.mapper.LendItemMapper;
import top.codecrab.srb.core.service.*;
import top.codecrab.srb.core.utils.LendNoUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Slf4j
@Service
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

    @Autowired
    private UserBindService userBindService;

    @Autowired
    private LendService lendService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private TransFlowService transFlowService;

    @Override
    public String commitInvest(InvestVo investVo) {

        //判断是否是投资人
        Long userId = investVo.getInvestUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        Assert.isTrue(userInfo.getUserType().equals(1), ResponseEnum.BORROWER_CANNOT_INVEST_ERROR);

        // 判断是否在募资中
        Long lendId = investVo.getLendId();
        Lend lend = lendService.getById(lendId);
        Assert.isTrue(
                LendStatusEnum.INVEST_RUN.getStatus().equals(lend.getStatus()),
                ResponseEnum.LEND_INVEST_ERROR
        );

        BigDecimal investAmount = new BigDecimal(investVo.getInvestAmount());

        //标的不能超卖 如果当前投标 + 已投 <= 满标 就不抛异常
        BigDecimal nowInvestAmount = investAmount.add(lend.getInvestAmount());
        Assert.isTrue(
                nowInvestAmount.compareTo(lend.getAmount()) < 1,
                ResponseEnum.LEND_FULL_SCALE_ERROR
        );

        //账户可用余额充足：当前用户的余额 >= 当前用户的投资金额（可以投资）
        BigDecimal account = userAccountService.getAccount(userId);
        Assert.isTrue(
                account.compareTo(investAmount) > -1,
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR
        );

        LendItem item = new LendItem();
        item.setLendItemNo(LendNoUtils.getLendItemNo());
        item.setLendId(lendId);
        item.setInvestUserId(userId);
        item.setInvestName(investVo.getInvestName());
        item.setInvestAmount(investAmount);
        item.setLendYearRate(lend.getLendYearRate());
        item.setInvestTime(LocalDateTime.now());
        item.setLendStartDate(lend.getLendStartDate());
        item.setLendEndDate(lend.getLendEndDate());

        BigDecimal count = lendService.getInterestCount(
                investAmount, item.getLendYearRate(), lend.getPeriod(), lend.getReturnMethod()
        );
        item.setExpectAmount(count);
        item.setRealAmount(BigDecimal.ZERO);
        item.setStatus(LendItemStatusEnum.DEFAULT.getStatus());
        baseMapper.insert(item);

        //封装提交至汇付宝的参数
        Map<String, Object> paramMap = new HashMap<>(15, 1);
        paramMap.put("agentId", HfbConst.AGENT_ID);
        //投资人的绑定号
        paramMap.put("voteBindCode", userInfo.getBindCode());
        //募资人的绑定号
        paramMap.put("benefitBindCode", userBindService.getBindCodeByUserId(lend.getUserId()));
        //项目标号
        paramMap.put("agentProjectCode", lend.getLendNo());
        paramMap.put("agentProjectName", lend.getTitle());

        //在资金托管平台上的投资订单的唯一编号，要和lendItemNo保持一致。
        paramMap.put("agentBillNo", item.getLendItemNo());
        paramMap.put("voteAmt", investVo.getInvestAmount());
        paramMap.put("votePrizeAmt", "0");
        paramMap.put("voteFeeAmt", "0");
        //标的总金额
        paramMap.put("projectAmt", lend.getAmount());
        paramMap.put("note", userInfo.getName() + "投标");
        paramMap.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        //构建充值自动提交表单
        return FormHelper.buildForm(HfbConst.INVEST_URL, paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notify(Map<String, Object> paramMap) {
        String agentBillNo = MapUtil.get(paramMap, "agentBillNo", String.class);

        if (transFlowService.isTransNoExist(agentBillNo)) {
            log.warn("幂等性返回");
            return "success";
        }

        String voteBindCode = MapUtil.get(paramMap, "voteBindCode", String.class);
        BigDecimal voteAmt = MapUtil.get(paramMap, "voteAmt", BigDecimal.class);

        //冻结用户资金
        userAccountService.updateAccount(voteBindCode, voteAmt.negate(), voteAmt);

        //修改投资状态
        LendItem lendItem = baseMapper.selectOne(new QueryWrapper<LendItem>()
                .eq("lend_item_no", agentBillNo));
        lendItem.setStatus(LendItemStatusEnum.PAID_RUN.getStatus());
        baseMapper.updateById(lendItem);

        Long userId = userBindService.getUserIdByBindCode(voteBindCode);
        Assert.isTrue(userId > 0, ResponseEnum.LOGIN_MOBILE_ERROR);

        //修改标的投标人数和金额
        Lend lend = lendService.getById(lendItem.getLendId());

        // 查询是否已经有该投资人对同一个标的的投资记录，如果有则不用增加人数
        int count = transFlowService.count(new QueryWrapper<TransFlow>()
                .eq("with_id", lend.getLendNo())
                .eq("user_id", userId));

        if (count <= 0) {
            lend.setInvestNum(lend.getInvestNum() + 1);
        }
        lend.setInvestAmount(lend.getInvestAmount().add(voteAmt));
        lendService.updateById(lend);

        //生成交易流水
        transFlowService.saveTransFlow(
                new TransFlowBo(
                        agentBillNo,
                        voteBindCode,
                        lend.getLendNo(),
                        voteAmt,
                        TransTypeEnum.INVEST_LOCK,
                        lendItem.getInvestName() + "投标；项目名称：" + lend.getTitle()
                )
        );

        return "success";
    }

    @Override
    public List<LendItem> selectByLendId(Long lendId) {
        return baseMapper.selectList(new QueryWrapper<LendItem>()
                .eq("lend_id", lendId));
    }
}
