package top.codecrab.srb.core.controller.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.TransFlow;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 交易流水表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "交易流水")
@RestController
@RequestMapping("/api/core/transFlow")
public class TransFlowController extends CoreBaseController {

    @ApiOperation("获取列表")
    @GetMapping("/list")
    public Result list() {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        List<TransFlow> list = transFlowService.selectByUserId(userId);
        return Result.ok().data("list", list);
    }

}
