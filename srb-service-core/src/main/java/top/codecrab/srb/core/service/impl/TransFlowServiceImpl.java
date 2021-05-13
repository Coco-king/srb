package top.codecrab.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.codecrab.srb.core.entity.TransFlow;
import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.entity.bo.TransFlowBo;
import top.codecrab.srb.core.mapper.TransFlowMapper;
import top.codecrab.srb.core.mapper.UserInfoMapper;
import top.codecrab.srb.core.service.TransFlowService;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void saveTransFlow(TransFlowBo transFlowBo) {
        UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>()
                .eq("bind_code", transFlowBo.getBindCode()));

        TransFlow flow = new TransFlow();
        flow.setUserId(userInfo.getId());
        flow.setUserName(userInfo.getName());
        flow.setTransNo(transFlowBo.getAgentBillNo());
        flow.setTransType(transFlowBo.getTransTypeEnum().getTransType());
        flow.setTransTypeName(transFlowBo.getTransTypeEnum().getTransTypeName());
        flow.setTransAmount(transFlowBo.getAmount());
        flow.setMemo(transFlowBo.getMemo());
        flow.setWithId(transFlowBo.getWithId());
        baseMapper.insert(flow);
    }

    @Override
    public boolean isTransNoExist(String agentBillNo) {
        Integer count = baseMapper.selectCount(new QueryWrapper<TransFlow>()
                .eq("trans_no", agentBillNo));
        return count > 0;
    }

    @Override
    public List<TransFlow> selectByUserId(Long userId) {
        return baseMapper.selectList(new QueryWrapper<TransFlow>()
                .eq("user_id", userId)
                .orderByDesc("id"));
    }
}
