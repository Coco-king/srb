package top.codecrab.srb.core.controller.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.codecrab.srb.core.service.DictService;
import top.codecrab.srb.core.service.IntegralGradeService;
import top.codecrab.srb.core.service.UserInfoService;

/**
 * @author codecrab
 * @since 2021年04月22日 18:10
 */
public class CoreBaseController {
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;
    @Autowired
    protected IntegralGradeService integralGradeService;
    @Autowired
    protected DictService dictService;
    @Autowired
    protected UserInfoService userInfoService;
}
