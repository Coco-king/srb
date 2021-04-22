package top.codecrab.srb.core.controller.base;

import org.springframework.beans.factory.annotation.Autowired;
import top.codecrab.srb.core.service.IntegralGradeService;

/**
 * @author codecrab
 * @since 2021年04月22日 18:10
 */
public class CoreBaseController {
    @Autowired
    protected IntegralGradeService integralGradeService;
}
