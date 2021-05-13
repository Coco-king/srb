package top.codecrab.srb.core.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.srb.common.config.Constants;
import top.codecrab.srb.common.excetion.BusinessException;
import top.codecrab.srb.core.entity.*;
import top.codecrab.srb.core.entity.bo.TransFlowBo;
import top.codecrab.srb.core.entity.vo.BorrowInfoApprovalVo;
import top.codecrab.srb.core.entity.vo.BorrowerDetailVo;
import top.codecrab.srb.core.enums.*;
import top.codecrab.srb.core.hfb.HfbConst;
import top.codecrab.srb.core.hfb.RequestHelper;
import top.codecrab.srb.core.mapper.LendMapper;
import top.codecrab.srb.core.service.*;
import top.codecrab.srb.core.utils.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Slf4j
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Autowired
    private DictService dictService;

    @Autowired
    private BorrowerService borrowerService;

    @Autowired
    private UserBindService userBindService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private TransFlowService transFlowService;

    @Autowired
    private LendItemService lendItemService;

    @Autowired
    private LendReturnService lendReturnService;

    @Autowired
    private LendItemReturnService lendItemReturnService;

    @Override
    public void createLend(BorrowInfoApprovalVo vo, BorrowInfo borrowInfo) {
        Lend lend = new Lend();
        lend.setUserId(borrowInfo.getUserId());
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setLendNo(LendNoUtils.getLendNo());
        lend.setTitle(vo.getTitle());
        lend.setAmount(borrowInfo.getAmount());
        lend.setPeriod(borrowInfo.getPeriod());
        lend.setLendYearRate(vo.getLendYearRate().divide(Constants.ONE_HUNDRED, 2, BigDecimal.ROUND_DOWN));
        lend.setServiceRate(vo.getServiceRate().divide(Constants.ONE_HUNDRED, 8, BigDecimal.ROUND_DOWN));
        lend.setReturnMethod(borrowInfo.getReturnMethod());
        lend.setLowestAmount(Constants.ONE_HUNDRED);
        lend.setInvestAmount(BigDecimal.ZERO);
        lend.setInvestNum(0);
        lend.setPublishDate(LocalDateTime.now());

        // 起息日期
        LocalDate lendStartDate = LocalDate.parse(vo.getLendStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        lend.setLendStartDate(lendStartDate);
        lend.setLendEndDate(lendStartDate.plusMonths(borrowInfo.getPeriod()));
        lend.setLendInfo(vo.getLendInfo());

        // 平台预期收益率 = 年化 / 12 * 期数
        BigDecimal rate = lend.getServiceRate().divide(Constants.TWELVE, 8, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(borrowInfo.getPeriod()));
        lend.setExpectAmount(lend.getAmount().multiply(rate));

        lend.setRealAmount(BigDecimal.ZERO);
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());
        lend.setCheckTime(LocalDateTime.now());
        lend.setCheckAdminId(Constants.ADMIN_ID);
        baseMapper.insert(lend);
    }

    @Override
    public List<Lend> selectList() {
        return baseMapper.selectList(Wrappers.emptyWrapper())
                .stream()
                .peek(this::packagingLendParams)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> selectMap(Long id) {
        Lend lend = baseMapper.selectById(id);
        this.packagingLendParams(lend);

        Borrower borrower = borrowerService.getOne(new QueryWrapper<Borrower>()
                .eq("user_id", lend.getUserId()));

        BorrowerDetailVo detailVo = borrowerService.getBorrowerDetailVo(borrower.getId(), false);

        Map<String, Object> map = new HashMap<>(16);
        map.put("lend", lend);
        map.put("borrower", detailVo);
        return map;
    }

    @Override
    public BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalMonth, Integer returnMethod) {
        BigDecimal interestCount;
        if (ReturnMethodEnum.ONE.getMethod().equals(returnMethod)) {
            interestCount = Amount1Helper.getInterestCount(invest, yearRate, totalMonth);
        } else if (ReturnMethodEnum.TWO.getMethod().equals(returnMethod)) {
            interestCount = Amount2Helper.getInterestCount(invest, yearRate, totalMonth);
        } else if (ReturnMethodEnum.THREE.getMethod().equals(returnMethod)) {
            interestCount = Amount3Helper.getInterestCount(invest, yearRate, totalMonth);
        } else {
            interestCount = Amount4Helper.getInterestCount(invest, yearRate, totalMonth);
        }
        return interestCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void makeLoan(Long id) {
        Lend lend = baseMapper.selectById(id);

        //封装请求数据
        Map<String, Object> params = new HashMap<>(7, 1);
        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentProjectCode", lend.getLendNo());
        String agentBillNo = LendNoUtils.getLoanNo();
        params.put("agentBillNo", agentBillNo);

        //商户手续费 年化转月化
        BigDecimal mouthRate = lend.getServiceRate().divide(Constants.TWELVE, 8, BigDecimal.ROUND_DOWN);
        //实际收益
        BigDecimal realAmount = lend.getAmount().multiply(mouthRate).multiply(new BigDecimal(lend.getPeriod()));
        params.put("mchFee", realAmount);
        params.put("note", lend.getTitle() + "放款");
        params.put("timestamp", RequestHelper.getTimestamp());
        params.put("sign", RequestHelper.getSign(params));

        log.info("发送同步远程调用，参数为：{}", JSONUtil.toJsonStr(params));
        JSONObject result = RequestHelper.sendRequest(params, HfbConst.MAKE_LOAD_URL);
        log.info("返回数据为：{}", result.toString());

        String successCode = "0000";
        String resultCode = "resultCode";
        String resultMsg = "resultMsg";
        if (!successCode.equals(result.getStr(resultCode))) {
            throw new BusinessException(result.getStr(resultMsg));
        }

        //（1）标的状态和标的平台收益
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setRealAmount(realAmount);
        lend.setPaymentAdminId(Constants.ADMIN_ID);
        lend.setPaymentTime(LocalDateTime.now());
        baseMapper.updateById(lend);

        //（2）给借款账号转入金额
        Long userId = lend.getUserId();
        String bindCode = userBindService.getBindCodeByUserId(userId);
        //给借款人转入金额
        BigDecimal total = new BigDecimal(result.getStr("voteAmt"));
        userAccountService.updateAccount(bindCode, total, BigDecimal.ZERO);

        //（3）增加借款交易流水
        transFlowService.saveTransFlow(
                new TransFlowBo(
                        agentBillNo,
                        bindCode,
                        TransTypeEnum.BORROW_BACK.getTransTypeName(),
                        total,
                        TransTypeEnum.BORROW_BACK,
                        "项目名称：" + lend.getTitle()
                )
        );

        List<LendItem> lendItems = lendItemService.list(new QueryWrapper<LendItem>()
                .eq("lend_id", lend.getId())
                .eq("status", LendItemStatusEnum.PAID_RUN.getStatus()));

        lendItems.forEach(lendItem -> {
            //（4）解冻并扣除投资人资金
            Long investUserId = lendItem.getInvestUserId();
            String investBindCode = userBindService.getBindCodeByUserId(investUserId);
            BigDecimal investAmount = lendItem.getInvestAmount();
            userAccountService.updateAccount(investBindCode, BigDecimal.ZERO, investAmount.negate());

            //（5）增加投资人交易流水
            transFlowService.saveTransFlow(
                    new TransFlowBo(
                            LendNoUtils.getTransNo(),
                            investBindCode,
                            TransTypeEnum.INVEST_UNLOCK.getTransTypeName(),
                            investAmount,
                            TransTypeEnum.INVEST_UNLOCK,
                            "冻结资金转出，出借放款，编号：" + lend.getLendNo()
                    )
            );
        });

        //（6）生成借款人还款计划和出借人回款计划
        this.repaymentPlan(lend);
    }

    private void packagingLendParams(Lend lend) {
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod());
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        lend.getParam().put("returnMethod", returnMethod);
        lend.getParam().put("status", status);
    }

    /**
     * 还款计划
     *
     * @param lend 标的
     */
    private void repaymentPlan(Lend lend) {
        int count = lend.getPeriod();

        List<LendReturn> lendReturnList = new ArrayList<>();
        // 每期生成一条对应的还款记录
        for (int i = 1; i <= count; i++) {
            LendReturn lendReturn = new LendReturn();
            lendReturn.setLendId(lend.getId());
            lendReturn.setBorrowInfoId(lend.getBorrowInfoId());
            lendReturn.setReturnNo(LendNoUtils.getReturnNo());
            lendReturn.setUserId(lend.getUserId());
            lendReturn.setAmount(lend.getAmount());
            lendReturn.setBaseAmount(lend.getInvestAmount());
            lendReturn.setCurrentPeriod(i);
            lendReturn.setLendYearRate(lend.getLendYearRate());
            lendReturn.setReturnMethod(lend.getReturnMethod());
            lendReturn.setFee(BigDecimal.ZERO);
            lendReturn.setReturnDate(lend.getLendStartDate().plusMonths(i));
            lendReturn.setOverdue(false);
            lendReturn.setOverdueTotal(BigDecimal.ZERO);
            lendReturn.setStatus(LendReturnStatusEnum.NOT_RETURNED.getStatus());
            //最后一个月
            lendReturn.setLast(i == count);
            lendReturnList.add(lendReturn);
        }
        //批量更新
        lendReturnService.saveBatch(lendReturnList);

        //获取lendReturnList中还款期数与还款计划id对应map
        Map<Integer, Long> lendReturnMap = lendReturnList.stream().collect(
                Collectors.toMap(LendReturn::getCurrentPeriod, LendReturn::getId)
        );

        //获取所有投资者，生成回款计划
        List<LendItem> lendItems = lendItemService.list(new QueryWrapper<LendItem>()
                .eq("lend_id", lend.getId())
                .eq("status", LendItemStatusEnum.PAID_RUN.getStatus()));

        List<LendItemReturn> lendItemReturnAllList = new ArrayList<>();

        //拼装该标的每一期和其对应的所有还款计划记录
        lendItems.forEach(lendItem -> lendItemReturnAllList.addAll(this.returnInvest(lendItem, lendReturnMap, lend)));

        lendReturnList.forEach(lendReturn -> {
            BigDecimal sumPrincipal = lendItemReturnAllList.stream()
                    //过滤条件：当回款计划中的还款计划id == 当前还款计划id的时候
                    .filter(lendItemReturn -> lendItemReturn.getLendReturnId().equals(lendReturn.getId()))
                    .map(LendItemReturn::getPrincipal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal sumInterest = lendItemReturnAllList.stream()
                    //过滤条件：当回款计划中的还款计划id == 当前还款计划id的时候
                    .filter(lendItemReturn -> lendItemReturn.getLendReturnId().equals(lendReturn.getId()))
                    .map(LendItemReturn::getInterest)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal sumTotal = lendItemReturnAllList.stream()
                    //过滤条件：当回款计划中的还款计划id == 当前还款计划id的时候
                    .filter(lendItemReturn -> lendItemReturn.getLendReturnId().equals(lendReturn.getId()))
                    .map(LendItemReturn::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            lendReturn.setPrincipal(sumPrincipal);
            lendReturn.setInterest(sumInterest);
            lendReturn.setTotal(sumTotal);
        });
        lendReturnService.updateBatchById(lendReturnList);
    }

    /**
     * 回款计划
     *
     * @param lendItem      投标记录
     * @param lendReturnMap 还款期数与还款计划id对应map
     * @param lend          投标
     * @return 生成的回款投标记录
     */
    public List<LendItemReturn> returnInvest(LendItem lendItem, Map<Integer, Long> lendReturnMap, Lend lend) {

        Integer returnMethod = lend.getReturnMethod();
        BigDecimal invest = lendItem.getInvestAmount();
        BigDecimal yearRate = lendItem.getLendYearRate();
        Integer totalMonth = lend.getPeriod();

        Map<Integer, BigDecimal> interestMap;
        Map<Integer, BigDecimal> principalMap;

        if (ReturnMethodEnum.ONE.getMethod().equals(returnMethod)) {
            interestMap = Amount1Helper.getPerMonthInterest(invest, yearRate, totalMonth);
            principalMap = Amount1Helper.getPerMonthPrincipal(invest, yearRate, totalMonth);
        } else if (ReturnMethodEnum.TWO.getMethod().equals(returnMethod)) {
            interestMap = Amount2Helper.getPerMonthInterest(invest, yearRate, totalMonth);
            principalMap = Amount2Helper.getPerMonthPrincipal(invest, totalMonth);
        } else if (ReturnMethodEnum.THREE.getMethod().equals(returnMethod)) {
            interestMap = Amount3Helper.getPerMonthInterest(invest, yearRate, totalMonth);
            principalMap = Amount3Helper.getPerMonthPrincipal(invest, totalMonth);
        } else {
            interestMap = Amount4Helper.getPerMonthInterest(invest, yearRate, totalMonth);
            principalMap = Amount4Helper.getPerMonthPrincipal(invest);
        }

        List<LendItemReturn> lendItemReturnList = new ArrayList<>();
        //创建回款计划列表
        interestMap.forEach((period, interest) -> {
            //根据还款期数获取还款计划的id
            Long lendReturnId = lendReturnMap.get(period);

            LendItemReturn lendItemReturn = new LendItemReturn();
            lendItemReturn.setLendReturnId(lendReturnId);
            lendItemReturn.setLendItemId(lendItem.getId());
            lendItemReturn.setLendId(lend.getId());
            lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
            lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
            lendItemReturn.setCurrentPeriod(period);
            lendItemReturn.setLendYearRate(lendItem.getLendYearRate());
            lendItemReturn.setReturnMethod(lend.getReturnMethod());

            //最后一次本金计算
            if (lendItemReturnList.size() > 0 && period.equals(totalMonth)) {

                //前面几期的所有本金之和
                BigDecimal otherPrincipal = lendItemReturnList.stream()
                        .map(LendItemReturn::getPrincipal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                //最后一期本金 = 总金额 - 其它几期之和
                lendItemReturn.setPrincipal(lendItem.getInvestAmount().subtract(otherPrincipal));

                //计算利息
                BigDecimal otherInterest = lendItemReturnList.stream()
                        .map(LendItemReturn::getInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                //最后一期本金 = 总金额 - 其它几期之和
                lendItemReturn.setInterest(lendItem.getExpectAmount().subtract(otherInterest));
            } else {
                lendItemReturn.setPrincipal(principalMap.get(period));
                lendItemReturn.setInterest(interest);
            }
            lendItemReturn.setTotal(lendItemReturn.getPrincipal().add(lendItemReturn.getInterest()));
            lendItemReturn.setFee(BigDecimal.ZERO);
            lendItemReturn.setReturnDate(lendItem.getLendStartDate().plusMonths(period));
            lendItemReturn.setOverdue(false);
            lendItemReturn.setOverdueTotal(BigDecimal.ZERO);
            lendItemReturn.setStatus(LendReturnStatusEnum.NOT_RETURNED.getStatus());
            lendItemReturnList.add(lendItemReturn);
        });
        lendItemReturnService.saveBatch(lendItemReturnList);
        return lendItemReturnList;
    }
}
