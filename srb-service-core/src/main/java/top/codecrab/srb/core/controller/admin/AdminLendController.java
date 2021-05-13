package top.codecrab.srb.core.controller.admin;


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
@Api(tags = "标的管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/lend")
public class AdminLendController extends CoreBaseController {

    @ApiOperation("标的列表")
    @GetMapping("/list")
    public Result list() {
        List<Lend> lendList = lendService.selectList();
        return Result.ok("list", lendList);
    }

    @ApiOperation("标的详情")
    @GetMapping("/show/{id}")
    public Result show(@PathVariable Long id) {
        Map<String, Object> map = lendService.selectMap(id);
        return Result.ok(map);
    }

    @ApiOperation("放款")
    @GetMapping("/makeLoan/{id}")
    public Result makeLoan(
            @ApiParam(value = "标的id", required = true)
            @PathVariable("id") Long id) {
        lendService.makeLoan(id);
        return Result.ok("放款成功");
    }
}
