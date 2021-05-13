package top.codecrab.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.codecrab.srb.core.entity.Lend;
import top.codecrab.srb.core.entity.LendItem;
import top.codecrab.srb.core.entity.LendItemReturn;
import top.codecrab.srb.core.entity.LendReturn;
import top.codecrab.srb.core.mapper.LendItemMapper;
import top.codecrab.srb.core.mapper.LendItemReturnMapper;
import top.codecrab.srb.core.service.LendItemReturnService;
import top.codecrab.srb.core.service.UserBindService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {

    @Resource
    private LendItemMapper lendItemMapper;

    @Autowired
    private UserBindService userBindService;

    @Override
    public List<LendItemReturn> selectByLendId(Long lendId, Long userId) {
        return baseMapper.selectList(new QueryWrapper<LendItemReturn>()
                .eq("lend_id", lendId)
                .eq("invest_user_id", userId)
                .orderByAsc("current_period"));
    }

    @Override
    public List<Map<String, Object>> addReturnDetail(LendReturn lendReturn, Lend lend) {
        List<LendItemReturn> lendItemReturns = baseMapper.selectList(new QueryWrapper<LendItemReturn>()
                .eq("lend_return_id", lendReturn.getId()));

        List<Map<String, Object>> result = new ArrayList<>();
        lendItemReturns.forEach(lendItemReturn -> {

            LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());
            String bindCode = userBindService.getBindCodeByUserId(lendItem.getInvestUserId());

            Map<String, Object> map = new HashMap<>(7, 1);
            //项目编号
            map.put("agentProjectCode", lend.getLendNo());
            //出借编号
            map.put("voteBillNo", lendItem.getLendItemNo());
            //收款人（出借人）
            map.put("toBindCode", bindCode);
            //还款金额
            map.put("transitAmt", lendItemReturn.getTotal());
            //还款本金
            map.put("baseAmt", lendItemReturn.getPrincipal());
            //还款利息
            map.put("benifitAmt", lendItemReturn.getInterest());
            //商户手续费
            map.put("feeAmt", new BigDecimal("0"));

            result.add(map);
        });
        return result;
    }
}
