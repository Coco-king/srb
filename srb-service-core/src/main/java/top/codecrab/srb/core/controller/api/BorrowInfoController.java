package top.codecrab.srb.core.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.common.utils.ValidationUtil;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.BorrowInfo;
import top.codecrab.srb.core.enums.BorrowInfoStatusEnum;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "借款信息")
@Slf4j
@RestController
@RequestMapping("/api/core/borrowInfo")
public class BorrowInfoController extends CoreBaseController {

    @ApiOperation("获取借款额度")
    @GetMapping("/auth/getBorrowAmount")
    public Result getBorrowAmount() {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return Result.ok("borrowAmount", borrowAmount);
    }

    @ApiOperation("保存借款额度申请")
    @PostMapping("/auth/save")
    public Result save(
            @ApiParam("借款额度bean对象")
            @RequestBody BorrowInfo borrowInfo
    ) {
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(borrowInfo);
        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }

        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        borrowInfoService.saveBorrowInfo(borrowInfo, userId);
        return Result.ok();
    }

    @ApiOperation("获取借款额度申请状态")
    @GetMapping("/auth/getBorrowInfoStatus")
    public Result getBorrowInfoStatus() {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        Map<String, Object> map = borrowInfoService.getMap(new QueryWrapper<BorrowInfo>()
                .select("status").eq("user_id", userId));
        return map == null ? Result.ok("status", BorrowInfoStatusEnum.NO_AUTH.getStatus()) : Result.ok(map);
    }
}
