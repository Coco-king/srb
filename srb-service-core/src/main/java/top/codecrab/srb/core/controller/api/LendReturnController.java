package top.codecrab.srb.core.controller.api;


import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.LendReturn;
import top.codecrab.srb.core.hfb.RequestHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "还款记录")
@Slf4j
@RestController
@RequestMapping("/api/core/lendReturn")
public class LendReturnController extends CoreBaseController {

    @ApiOperation("获取还款记录列表")
    @GetMapping("/list/{lendId}")
    public Result list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId) {
        List<LendReturn> list = lendReturnService.selectByLendId(lendId);
        return Result.ok().data("list", list);
    }

    @ApiOperation("用户还款")
    @PostMapping("/auth/commitReturn/{lendReturnId}")
    public Result commitReturn(
            @ApiParam(value = "还款计划id", required = true)
            @PathVariable Long lendReturnId
    ) {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));

        BigDecimal account = userAccountService.getAccount(userId);
        LendReturn lendReturn = lendReturnService.getById(lendReturnId);

        Assert.isTrue(lendReturn.getTotal().compareTo(account) < 0, ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        String formStr = lendReturnService.commitReturn(lendReturn, userId);
        return Result.ok().data("formStr", formStr);
    }

    @ApiOperation("还款异步回调")
    @PostMapping("/notifyUrl")
    public String notifyUrl() {
        Map<String, Object> result = RequestHelper.switchMap(request.getParameterMap());
        String jsonStr = JSONUtil.toJsonStr(result);
        log.info("还款异步回调数据：{}", jsonStr);

        if (RequestHelper.isSignEquals(result)) {
            String resultCode = "resultCode";
            String successCode = "0001";
            if (successCode.equals(result.get(resultCode))) {
                return lendReturnService.notify(result);
            } else {
                log.info("还款异步回调失败：" + jsonStr);
                return "fail";
            }
        } else {
            log.info("还款异步回调签名错误：" + jsonStr);
            return "fail";
        }
    }
}
