package top.codecrab.srb.core.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.srb.core.entity.Lend;
import top.codecrab.srb.core.entity.LendItem;
import top.codecrab.srb.core.entity.LendItemReturn;
import top.codecrab.srb.core.entity.LendReturn;
import top.codecrab.srb.core.entity.bo.TransFlowBo;
import top.codecrab.srb.core.enums.LendReturnStatusEnum;
import top.codecrab.srb.core.enums.LendStatusEnum;
import top.codecrab.srb.core.enums.TransTypeEnum;
import top.codecrab.srb.core.hfb.FormHelper;
import top.codecrab.srb.core.hfb.HfbConst;
import top.codecrab.srb.core.hfb.RequestHelper;
import top.codecrab.srb.core.mapper.LendMapper;
import top.codecrab.srb.core.mapper.LendReturnMapper;
import top.codecrab.srb.core.service.*;
import top.codecrab.srb.core.utils.LendNoUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Service
public class LendReturnServiceImpl extends ServiceImpl<LendReturnMapper, LendReturn> implements LendReturnService {

    @Resource
    private LendMapper lendMapper;

    @Autowired
    private UserBindService userBindService;

    @Autowired
    private LendItemReturnService lendItemReturnService;

    @Autowired
    private TransFlowService transFlowService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private LendItemService lendItemService;

    @Autowired
    private BorrowInfoService borrowInfoService;

    @Override
    public List<LendReturn> selectByLendId(Long lendId) {
        return baseMapper.selectList(new QueryWrapper<LendReturn>()
                .eq("lend_id", lendId));
    }

    @Override
    public String commitReturn(LendReturn lendReturn, Long userId) {

        Lend lend = lendMapper.selectById(lendReturn.getLendId());

        String bindCode = userBindService.getBindCodeByUserId(userId);

        Map<String, Object> paramMap = new HashMap<>(12, 1);
        paramMap.put("agentId", HfbConst.AGENT_ID);
        //商户商品名称
        paramMap.put("agentGoodsName", lend.getTitle());
        //批次号
        paramMap.put("agentBatchNo", lendReturn.getReturnNo());
        //还款人绑定协议号
        paramMap.put("fromBindCode", bindCode);
        //还款总额
        paramMap.put("totalAmt", lendReturn.getTotal());
        paramMap.put("note", lend.getTitle() + "还款");
        //还款明细
        List<Map<String, Object>> lendItemReturnDetailList = lendItemReturnService.addReturnDetail(lendReturn, lend);
        paramMap.put("data", JSONUtil.toJsonStr(lendItemReturnDetailList));
        paramMap.put("voteFeeAmt", new BigDecimal(0));
        paramMap.put("notifyUrl", HfbConst.BORROW_RETURN_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.BORROW_RETURN_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        //构建自动提交表单
        return FormHelper.buildForm(HfbConst.BORROW_RETURN_URL, paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notify(Map<String, Object> paramMap) {
        //判断幂等性返回
        String returnNo = MapUtil.get(paramMap, "agentBatchNo", String.class);
        if (transFlowService.isTransNoExist(returnNo)) {
            log.warn("幂等性返回");
            return "fail";
        }

        //获取还款信息
        LendReturn lendReturn = baseMapper.selectOne(new QueryWrapper<LendReturn>()
                .eq("return_no", returnNo));

        //修改还款状态
        BigDecimal voteFeeAmt = MapUtil.get(paramMap, "voteFeeAmt", BigDecimal.class);
        lendReturn.setStatus(LendReturnStatusEnum.RETURNED.getStatus());
        lendReturn.setFee(voteFeeAmt);
        lendReturn.setRealReturnTime(LocalDateTime.now());
        baseMapper.updateById(lendReturn);

        //如果是最后一次还款，更新标的状态
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        if (lendReturn.getLast()) {
            lend.setStatus(LendStatusEnum.PAY_OK.getStatus());
            lendMapper.updateById(lend);

            // 逻辑删除申请信息，使用户可以提交下一个借款
            borrowInfoService.removeById(lend.getBorrowInfoId());
        }

        //借款人账号金额转出
        BigDecimal totalAmt = MapUtil.get(paramMap, "totalAmt", BigDecimal.class);
        String bindCode = userBindService.getBindCodeByUserId(lend.getUserId());
        userAccountService.updateAccount(bindCode, totalAmt.negate(), BigDecimal.ZERO);

        //借款人交易流水
        transFlowService.saveTransFlow(new TransFlowBo(
                returnNo,
                bindCode,
                lend.getUserId().toString(),
                totalAmt,
                TransTypeEnum.RETURN_DOWN,
                "借款人还款扣减，项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle())
        );

        //获取还款信息的还款列表
        List<LendItemReturn> itemReturnList = lendItemReturnService.list(new QueryWrapper<LendItemReturn>()
                .eq("lend_return_id", lendReturn.getId()));

        itemReturnList.forEach(lendItemReturn -> {
            //更新还款列表的状态
            lendItemReturn.setStatus(1);
            lendItemReturn.setRealReturnTime(LocalDateTime.now());
            lendItemReturnService.updateById(lendItemReturn);

            //更新回款列表的状态
            LendItem lendItem = lendItemService.getById(lendItemReturn.getLendItemId());
            lendItem.setRealAmount(lendItemReturn.getInterest());
            lendItemService.updateById(lendItem);

            //给投资人账户增加余额
            String investBindCode = userBindService.getBindCodeByUserId(lendItemReturn.getInvestUserId());
            userAccountService.updateAccount(investBindCode, lendItemReturn.getTotal(), BigDecimal.ZERO);

            //投资账号交易流水
            transFlowService.saveTransFlow(new TransFlowBo(
                    LendNoUtils.getReturnItemNo(),
                    investBindCode,
                    lendItemReturn.getInvestUserId().toString(),
                    lendItemReturn.getTotal(),
                    TransTypeEnum.INVEST_BACK,
                    "还款到账，项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle())
            );
        });

        return "success";
    }
}
