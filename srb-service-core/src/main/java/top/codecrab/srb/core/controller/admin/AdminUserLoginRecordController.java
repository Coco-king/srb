package top.codecrab.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import top.codecrab.srb.core.entity.UserLoginRecord;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "登录日志管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController extends CoreBaseController {

    @ApiOperation("获取登陆日志")
    @GetMapping("/listTop/{id}/{count}")
    public Result listTop(
            @ApiParam(value = "用户id", required = true)
            @PathVariable Long id,
            @ApiParam(value = "显示数量", required = true)
            @PathVariable Integer count
    ) {
        List<UserLoginRecord> records = userLoginRecordService.list(new QueryWrapper<UserLoginRecord>()
                .eq("user_id", id)
                .orderByDesc("id")
                .last(" limit " + count)
        );
        return Result.ok("list", records);
    }

}
