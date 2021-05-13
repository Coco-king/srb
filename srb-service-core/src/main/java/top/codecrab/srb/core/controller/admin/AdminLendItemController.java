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
import top.codecrab.srb.core.entity.LendItem;

import java.util.List;

/**
 * <p>
 * 标的出借记录表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "标的的投资")
@Slf4j
@RestController
@RequestMapping("/admin/core/lendItem")
public class AdminLendItemController extends CoreBaseController {

    @ApiOperation("获取投标对应的列表")
    @GetMapping("/list/{lendId}")
    public Result list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId
    ) {
        List<LendItem> list = lendItemService.selectByLendId(lendId);
        return Result.ok("list", list);
    }

}
