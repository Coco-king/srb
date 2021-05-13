package top.codecrab.srb.core.controller.api;


import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.hfb.RequestHelper;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "会员账户")
@Slf4j
@RestController
@RequestMapping("/api/core/userAccount")
public class UserAccountController extends CoreBaseController {

    @ApiOperation("充值")
    @PostMapping("/auth/commitCharge/{chargeAmt}")
    public Result commitCharge(
            @ApiParam(value = "充值金额", required = true)
            @PathVariable BigDecimal chargeAmt
    ) {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        String formStr = userAccountService.commitCharge(chargeAmt, userId);
        return Result.ok("formStr", formStr);
    }

    @ApiOperation(value = "用户充值异步回调")
    @PostMapping("/notify")
    public String hfbNotify() {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户充值异步回调：" + JSONUtil.toJsonStr(paramMap));

        //校验签名
        if (RequestHelper.isSignEquals(paramMap)) {
            //充值成功交易
            String successCode = "0001";
            String returnCodeKey = "resultCode";
            if (successCode.equals(paramMap.get(returnCodeKey))) {
                return userAccountService.notify(paramMap);
            } else {
                log.info("用户充值异步回调充值失败：" + JSONUtil.toJsonStr(paramMap));
                return "fail";
            }
        } else {
            log.info("用户充值异步回调签名错误：" + JSONUtil.toJsonStr(paramMap));
            return "fail";
        }
    }

    @ApiOperation("查询账户余额")
    @GetMapping("/auth/getAccount")
    public Result getAccount() {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        BigDecimal account = userAccountService.getAccount(userId);
        return Result.ok("account", account);
    }

    @ApiOperation("用户提现")
    @PostMapping("/auth/commitWithdraw/{fetchAmt}")
    public Result commitWithdraw(
            @ApiParam(value = "金额", required = true)
            @PathVariable BigDecimal fetchAmt
    ) {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        String formStr = userAccountService.commitWithdraw(fetchAmt, userId);
        return Result.ok().data("formStr", formStr);
    }

    @ApiOperation("用户提现异步回调")
    @PostMapping("/notifyWithdraw")
    public String notifyWithdraw() {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("提现异步回调：" + JSONUtil.toJsonStr(paramMap));

        //校验签名
        if (RequestHelper.isSignEquals(paramMap)) {
            //提现成功交易
            String resultCode = "resultCode";
            String successCode = "0001";
            if (successCode.equals(paramMap.get(resultCode))) {
                return userAccountService.notifyWithdraw(paramMap);
            } else {
                log.info("提现异步回调充值失败：" + JSONUtil.toJsonStr(paramMap));
                return "fail";
            }
        } else {
            log.info("提现异步回调签名错误：" + JSONUtil.toJsonStr(paramMap));
            return "fail";
        }
    }
}
