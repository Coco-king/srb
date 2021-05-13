package top.codecrab.srb.core.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import top.codecrab.srb.core.entity.Dict;
import top.codecrab.srb.core.entity.IntegralGrade;
import top.codecrab.srb.core.service.DictService;
import top.codecrab.srb.core.service.IntegralGradeService;

import java.util.concurrent.TimeUnit;

/**
 * @author codecrab
 * @since 2021年04月26日 11:05
 */
@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DictService dictService;
    @Autowired
    private IntegralGradeService integralGradeService;

    @Test
    void testSave() {
        Dict dict = dictService.getById(1L);
        redisTemplate.opsForValue().set("dict", dict, 5, TimeUnit.MINUTES);
    }

    @Test
    void testGet() {
        Dict dict = (Dict) redisTemplate.opsForValue().get("dict");
        System.out.println(dict);
    }

    @Test
    void name() {
        QueryWrapper<IntegralGrade> wrapper = new QueryWrapper<IntegralGrade>()
                .ge("integral_end", 11000)
                .le("integral_start", 11000);

        IntegralGrade one = integralGradeService.getOne(wrapper);
        System.out.println(one);

        String segment = wrapper.getCustomSqlSegment();
        System.out.println(segment);
    }
}
