package top.codecrab.srb.core.controller.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.LendItemReturn;

import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "回款计划")
@Slf4j
@RestController
@RequestMapping("/api/core/lendItemReturn")
public class LendItemReturnController extends CoreBaseController {

    @ApiOperation("获取标的出借回款记录列表")
    @GetMapping("/auth/list/{lendId}")
    public Result list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId
    ) {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        List<LendItemReturn> list = lendItemReturnService.selectByLendId(lendId, userId);
        return Result.ok().data("list", list);
    }
}
