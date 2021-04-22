package top.codecrab.srb.core.controller.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.IntegralGrade;
import top.codecrab.srb.core.service.IntegralGradeService;

import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController extends CoreBaseController {

    @GetMapping("/list")
    public List<IntegralGrade> list() {
        return integralGradeService.list();
    }

}
