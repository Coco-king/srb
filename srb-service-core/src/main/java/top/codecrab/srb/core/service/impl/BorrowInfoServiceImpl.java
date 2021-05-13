package top.codecrab.srb.core.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.codecrab.srb.common.config.Constants;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.core.entity.BorrowInfo;
import top.codecrab.srb.core.entity.Borrower;
import top.codecrab.srb.core.entity.IntegralGrade;
import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.entity.vo.BorrowInfoApprovalVo;
import top.codecrab.srb.core.entity.vo.BorrowerDetailVo;
import top.codecrab.srb.core.enums.BorrowInfoStatusEnum;
import top.codecrab.srb.core.enums.BorrowerStatusEnum;
import top.codecrab.srb.core.enums.UserBindEnum;
import top.codecrab.srb.core.mapper.BorrowInfoMapper;
import top.codecrab.srb.core.mapper.IntegralGradeMapper;
import top.codecrab.srb.core.mapper.UserInfoMapper;
import top.codecrab.srb.core.service.BorrowInfoService;
import top.codecrab.srb.core.service.BorrowerService;
import top.codecrab.srb.core.service.DictService;
import top.codecrab.srb.core.service.LendService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Autowired
    private BorrowerService borrowerService;

    @Autowired
    private DictService dictService;

    @Autowired
    private LendService lendService;

    @Override
    public BigDecimal getBorrowAmount(Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);

        Integer integral = userInfo.getIntegral();

        // 查询出最高积分的额度
        IntegralGrade integralMaxEnd = integralGradeMapper.selectOne(new QueryWrapper<IntegralGrade>()
                .orderByDesc("integral_end").last(" limit 1"));

        // 如果用户积分大于最高积分，就直接返回最高额度
        if (integral > integralMaxEnd.getIntegralEnd()) {
            return integralMaxEnd.getBorrowAmount();
        }

        List<IntegralGrade> gradeList = integralGradeMapper.selectList(new QueryWrapper<IntegralGrade>()
                .ge("integral_end", integral)
                .le("integral_start", integral));

        if (CollectionUtil.isEmpty(gradeList) || gradeList.size() != 1) {
            return new BigDecimal(0);
        }
        return gradeList.get(0).getBorrowAmount();
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        // 判断用户是否绑定
        Assert.isTrue(
                UserBindEnum.BIND_OK.getStatus().equals(userInfo.getBindStatus()),
                ResponseEnum.USER_NO_BIND_ERROR
        );
        // 判断用户是否已通过认证
        Assert.isTrue(
                BorrowerStatusEnum.AUTH_OK.getStatus().equals(userInfo.getBorrowAuthStatus()),
                ResponseEnum.USER_NO_AMOUNT_ERROR
        );

        // 判断用户可申请额度是否足够
        BigDecimal borrowAmount = this.getBorrowAmount(userId);
        Assert.isTrue(
                borrowInfo.getAmount().compareTo(borrowAmount) < 1,
                ResponseEnum.USER_AMOUNT_LESS_ERROR
        );

        // 年化利率百分比转小数，保留两位小数
        BigDecimal yearRate = borrowInfo.getBorrowYearRate().divide(Constants.ONE_HUNDRED, 2, BigDecimal.ROUND_DOWN);
        borrowInfo.setBorrowYearRate(yearRate);
        borrowInfo.setUserId(userId);
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);
    }

    @Override
    public List<BorrowInfo> selectList() {
        List<BorrowInfo> infos = baseMapper.selectBorrowInfoList();
        return infos.stream().peek(this::packagingBorrowInfo).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {

        BorrowInfo borrowInfo = baseMapper.selectById(id);
        this.packagingBorrowInfo(borrowInfo);

        // 根据借款申请的用户id查询借款信息
        Borrower borrower = borrowerService.getOne(new QueryWrapper<Borrower>()
                .eq("user_id", borrowInfo.getUserId()));

        // 组装借款人对象
        BorrowerDetailVo borrowerDetailVo = borrowerService.getBorrowerDetailVo(borrower.getId(), false);

        Map<String, Object> result = new HashMap<>(16);
        result.put("borrowInfo", borrowInfo);
        result.put("borrower", borrowerDetailVo);
        return result;
    }

    @Override
    public void approval(BorrowInfoApprovalVo vo) {

        // 修改申请状态
        BorrowInfo borrowInfo = baseMapper.selectById(vo.getId());
        borrowInfo.setStatus(vo.getStatus());
        baseMapper.updateById(borrowInfo);

        // 审核通过，新建标的
        if (BorrowInfoStatusEnum.CHECK_OK.getStatus().equals(vo.getStatus())) {
            lendService.createLend(vo, borrowInfo);
        }
    }

    /**
     * 封装BorrowInfo的Param数据
     *
     * @param borrowInfo 原BorrowInfo对象
     */
    private void packagingBorrowInfo(BorrowInfo borrowInfo) {
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", status);
    }
}
