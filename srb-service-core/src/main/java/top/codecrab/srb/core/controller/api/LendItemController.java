package top.codecrab.srb.core.controller.api;


import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.config.Constants;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.common.utils.ValidationUtil;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.LendItem;
import top.codecrab.srb.core.entity.vo.InvestVo;
import top.codecrab.srb.core.hfb.RequestHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/api/core/lendItem")
public class LendItemController extends CoreBaseController {

    @ApiOperation("会员投资提交数据")
    @PostMapping("/auth/commitInvest")
    public Result commitInvest(@RequestBody InvestVo investVo) {

        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(investVo);
        if (validResult.hasErrors()) {
            Result.fail(validResult.getErrors());
        }

        String investAmount = investVo.getInvestAmount();
        // 投标金额不能为空
        Assert.notBlank(investAmount, ResponseEnum.LEND_AMOUNT_NULL_ERROR);

        // 如果传入的金额不是一百的倍数
        boolean flag = new BigDecimal(investAmount).divideAndRemainder(Constants.ONE_HUNDRED)[1].compareTo(BigDecimal.ZERO) == 0;
        Assert.isTrue(flag, ResponseEnum.LEND_AMOUNT_MUST_BE_MULTIPLES_OF_ONE_HUNDRED);

        String token = request.getHeader("token");
        investVo.setInvestUserId(JwtUtils.getUserId(token));
        investVo.setInvestName(JwtUtils.getUserName(token));
        String formStr = lendItemService.commitInvest(investVo);
        return Result.ok("formStr", formStr);
    }

    @ApiOperation(value = "用户投标异步回调")
    @PostMapping("/notify")
    public String hfbNotify() {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户投标异步回调：" + JSONUtil.toJsonStr(paramMap));

        //校验签名
        if (RequestHelper.isSignEquals(paramMap)) {
            //充值成功交易
            String successCode = "0001";
            String returnCodeKey = "resultCode";
            if (successCode.equals(paramMap.get(returnCodeKey))) {
                return lendItemService.notify(paramMap);
            } else {
                log.info("用户投标异步回调充值失败：" + JSONUtil.toJsonStr(paramMap));
                return "fail";
            }
        } else {
            log.info("用户投标异步回调签名错误：" + JSONUtil.toJsonStr(paramMap));
            return "fail";
        }
    }

    @ApiOperation("获取列表")
    @GetMapping("/list/{lendId}")
    public Result list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId) {
        List<LendItem> list = lendItemService.selectByLendId(lendId);
        return Result.ok().data("list", list);
    }
}
