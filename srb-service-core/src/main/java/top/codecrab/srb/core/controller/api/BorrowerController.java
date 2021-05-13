package top.codecrab.srb.core.controller.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.Borrower;
import top.codecrab.srb.core.entity.vo.BorrowerVo;
import top.codecrab.srb.core.enums.BorrowerStatusEnum;

import java.util.Map;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "借款人")
@RestController
@RequestMapping("/api/core/borrower")
public class BorrowerController extends CoreBaseController {

    @ApiOperation("保存借款人信息")
    @PostMapping("/auth/save")
    public Result save(
            @ApiParam("借款人信息")
            @RequestBody BorrowerVo borrowerVo
    ) {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        borrowerService.saveBorrowerVoByUserId(borrowerVo, userId);
        return Result.ok("信息提交成功");
    }

    @ApiOperation("获取借款人认证状态")
    @GetMapping("/auth/getBorrowerStatus")
    public Result getBorrowerStatus() {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        Map<String, Object> map = borrowerService.getMap(new QueryWrapper<Borrower>()
                .select("status").eq("user_id", userId));
        return map == null ? Result.ok("status", BorrowerStatusEnum.NO_AUTH.getStatus()) : Result.ok(map);
    }

}
