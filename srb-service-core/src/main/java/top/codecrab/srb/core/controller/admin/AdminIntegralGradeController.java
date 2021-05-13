package top.codecrab.srb.core.controller.admin;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.IntegralGrade;

import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "积分等级管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController extends CoreBaseController {

    @ApiOperation("积分等级列表")
    @GetMapping("/list")
    public Result list() {
        List<IntegralGrade> grades = integralGradeService.list();
        return Result.ok("list", grades);
    }

    @ApiOperation("根据id查询积分等级")
    @GetMapping("/get/{id}")
    public Result getById(
            @ApiParam(value = "数据id", example = "1", required = true)
            @PathVariable Long id) {
        IntegralGrade grade = integralGradeService.getById(id);
        return Result.ok("record", grade);
    }

    @ApiOperation(value = "根据id删除积分等级", notes = "逻辑删除")
    @DeleteMapping("/remove/{id}")
    public Result removeById(
            @ApiParam(value = "数据id", example = "1", required = true)
            @PathVariable Long id
    ) {
        boolean result = integralGradeService.removeById(id);
        return result ? Result.ok("删除成功") : Result.fail("删除失败");
    }

    @ApiOperation("保存积分等级")
    @PostMapping("/save")
    public Result save(
            @ApiParam(value = "积分等级对象", required = true)
            @RequestBody IntegralGrade integralGrade
    ) {
        Assert.notNull(integralGrade.getBorrowAmount(), ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        Assert.notNull(integralGrade.getIntegralStart(), ResponseEnum.INTEGRAL_START_AMOUNT_NULL_ERROR);
        Assert.notNull(integralGrade.getIntegralEnd(), ResponseEnum.INTEGRAL_END_AMOUNT_NULL_ERROR);
        boolean result = integralGradeService.save(integralGrade);
        return result ? Result.ok("保存成功") : Result.fail("保存失败");
    }

    @ApiOperation("更新积分等级")
    @PutMapping("/update")
    public Result updateById(
            @ApiParam(value = "积分等级对象", required = true)
            @RequestBody IntegralGrade integralGrade
    ) {
        boolean result = integralGradeService.updateById(integralGrade);
        return result ? Result.ok("更新成功") : Result.fail("更新失败");
    }

}
