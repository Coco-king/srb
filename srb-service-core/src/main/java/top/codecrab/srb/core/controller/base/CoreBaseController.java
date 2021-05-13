package top.codecrab.srb.core.controller.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.codecrab.srb.core.service.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author codecrab
 * @since 2021年04月22日 18:10
 */
public class CoreBaseController {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;
    @Autowired
    protected IntegralGradeService integralGradeService;
    @Autowired
    protected DictService dictService;
    @Autowired
    protected UserInfoService userInfoService;
    @Autowired
    protected UserLoginRecordService userLoginRecordService;
    @Autowired
    protected UserBindService userBindService;
    @Autowired
    protected BorrowerService borrowerService;
    @Autowired
    protected BorrowInfoService borrowInfoService;
    @Autowired
    protected LendService lendService;
    @Autowired
    protected UserAccountService userAccountService;
    @Autowired
    protected LendItemService lendItemService;
    @Autowired
    protected LendReturnService lendReturnService;
    @Autowired
    protected LendItemReturnService lendItemReturnService;
    @Autowired
    protected TransFlowService transFlowService;
}
