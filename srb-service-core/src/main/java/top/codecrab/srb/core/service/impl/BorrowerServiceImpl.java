package top.codecrab.srb.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.core.entity.Borrower;
import top.codecrab.srb.core.entity.BorrowerAttach;
import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.entity.UserIntegral;
import top.codecrab.srb.core.entity.vo.BorrowerApprovalVo;
import top.codecrab.srb.core.entity.vo.BorrowerAttachVo;
import top.codecrab.srb.core.entity.vo.BorrowerDetailVo;
import top.codecrab.srb.core.entity.vo.BorrowerVo;
import top.codecrab.srb.core.enums.BorrowerStatusEnum;
import top.codecrab.srb.core.enums.IntegralEnum;
import top.codecrab.srb.core.mapper.BorrowerAttachMapper;
import top.codecrab.srb.core.mapper.BorrowerMapper;
import top.codecrab.srb.core.mapper.UserInfoMapper;
import top.codecrab.srb.core.mapper.UserIntegralMapper;
import top.codecrab.srb.core.service.BorrowerService;
import top.codecrab.srb.core.service.DictService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Autowired
    private DictService dictService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBorrowerVoByUserId(BorrowerVo borrowerVo, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.WEIXIN_FETCH_USERINFO_ERROR);

        Borrower borrower = new Borrower();
        BeanUtil.copyProperties(borrowerVo, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        baseMapper.insert(borrower);

        borrowerVo.getBorrowerAttachList().forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        //更新会员状态，更新为认证中
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        UserInfo user = userInfoMapper.selectById(userId);
        Assert.notNull(user, ResponseEnum.WEIXIN_FETCH_USERINFO_ERROR);
        return user.getBorrowAuthStatus();
    }

    @Override
    public Page<Borrower> listPage(Page<Borrower> page, String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return baseMapper.selectPage(page, Wrappers.emptyWrapper());
        }
        return baseMapper.selectPage(page, new QueryWrapper<Borrower>()
                .like("name", keyword)
                .or().like("id_card", keyword)
                .or().like("mobile", keyword));
    }

    @Override
    public BorrowerDetailVo getBorrowerDetailVo(Long id, boolean hasAttach) {
        Borrower borrower = baseMapper.selectById(id);
        BorrowerDetailVo detailVo = BeanUtil.copyProperties(borrower, BorrowerDetailVo.class,
                "education", "industry", "income", "returnSource", "contactsRelation", "moneyUse");

        //婚否
        detailVo.setMarry(borrower.getMarry() ? "是" : "否");
        //性别
        detailVo.setSex(borrower.getSex() == 1 ? "男" : "女");

        //计算下拉列表选中内容
        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrower.getIndustry());
        String industry = dictService.getNameByParentDictCodeAndValue("industry", borrower.getIndustry());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String contactsRelation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());

        //设置下拉列表选中内容
        detailVo.setEducation(education);
        detailVo.setIndustry(industry);
        detailVo.setIncome(income);
        detailVo.setReturnSource(returnSource);
        detailVo.setContactsRelation(contactsRelation);
        detailVo.setMoneyUse(moneyUse);
        //模糊手机号
        detailVo.setMobile(Convert.convert(String.class, PhoneUtil.hideBetween(detailVo.getMobile())));

        //审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        detailVo.setStatus(status);

        if (hasAttach) {
            List<BorrowerAttach> attaches = borrowerAttachMapper.selectList(new QueryWrapper<BorrowerAttach>()
                    .eq("borrower_id", id));
            List<BorrowerAttachVo> voList = attaches.stream()
                    .map(attach -> BeanUtil.copyProperties(attach, BorrowerAttachVo.class))
                    .collect(Collectors.toList());
            detailVo.setBorrowerAttachVoList(voList);
        }
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approval(BorrowerApprovalVo vo) {
        Integer status = vo.getStatus();
        Borrower borrower = baseMapper.selectById(vo.getBorrowerId());
        borrower.setStatus(status);
        baseMapper.updateById(borrower);

        Long userId = borrower.getUserId();
        Integer totalIntegral = vo.getInfoIntegral();

        UserIntegral integral = new UserIntegral();
        integral.setUserId(userId);
        integral.setIntegral(vo.getInfoIntegral());
        integral.setContent(IntegralEnum.BORROWER_INFO.getMsg());
        userIntegralMapper.insert(integral);

        if (vo.getIsIdCardOk()) {
            integral = new UserIntegral();
            integral.setUserId(userId);
            integral.setIntegral(IntegralEnum.BORROWER_ID_CARD.getIntegral());
            integral.setContent(IntegralEnum.BORROWER_ID_CARD.getMsg());
            userIntegralMapper.insert(integral);
            totalIntegral += IntegralEnum.BORROWER_ID_CARD.getIntegral();
        }

        if (vo.getIsCarOk()) {
            integral = new UserIntegral();
            integral.setUserId(userId);
            integral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            integral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(integral);
            totalIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
        }

        if (vo.getIsHouseOk()) {
            integral = new UserIntegral();
            integral.setUserId(userId);
            integral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            integral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(integral);
            totalIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }

        UserInfo userInfo = userInfoMapper.selectById(userId);
        totalIntegral += userInfo.getIntegral();
        userInfo.setIntegral(totalIntegral);
        userInfo.setBorrowAuthStatus(status);
        userInfoMapper.updateById(userInfo);
    }
}
