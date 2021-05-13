package top.codecrab.srb.core.controller.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.Lend;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "标的")
@Slf4j
@RestController
@RequestMapping("/api/core/lend")
public class LendController extends CoreBaseController {

    @ApiOperation("标的列表")
    @GetMapping("/list")
    public Result list() {
        List<Lend> lendList = lendService.selectList();
        return Result.ok("list", lendList);
    }

    @ApiOperation("获取标的信息")
    @GetMapping("/show/{id}")
    public Result show(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long id) {
        Map<String, Object> lendDetail = lendService.selectMap(id);
        return Result.ok(lendDetail);
    }

    @ApiOperation("计算投资收益")
    @GetMapping("/getInterestCount/{invest}/{yearRate}/{totalMonth}/{returnMethod}")
    public Result getInterestCount(
            @ApiParam(value = "投资金额", required = true)
            @PathVariable("invest") BigDecimal invest,

            @ApiParam(value = "年化收益", required = true)
            @PathVariable("yearRate") BigDecimal yearRate,

            @ApiParam(value = "期数", required = true)
            @PathVariable("totalMonth") Integer totalMonth,

            @ApiParam(value = "还款方式", required = true)
            @PathVariable("returnMethod") Integer returnMethod
    ) {

        BigDecimal interestCount = lendService.getInterestCount(invest, yearRate, totalMonth, returnMethod);
        return Result.ok("interestCount", interestCount);
    }
}
