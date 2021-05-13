package top.codecrab.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.entity.query.UserInfoQuery;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "会员管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/userInfo")
public class AdminUserInfoController extends CoreBaseController {

    @ApiOperation("分页查询用户列表")
    @GetMapping("/list/{page}/{size}")
    public Result listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(value = "每页条数", required = true)
            @PathVariable Integer size,
            @ApiParam(value = "查询对象")
                    UserInfoQuery userInfoQuery
    ) {
        IPage<UserInfo> pageModel = userInfoService.listPage(new Page<>(page, size), userInfoQuery);
        return Result.ok("pageModel", pageModel);
    }

    @ApiOperation("解锁/锁定用户")
    @PutMapping("/lock/{id}/{status}")
    public Result lock(
            @ApiParam(value = "用户id", required = true)
            @PathVariable Long id,
            @ApiParam(value = "状态（0：锁定 1：正常）", required = true)
            @PathVariable Integer status
    ) {
        userInfoService.lock(id, status);
        return Result.ok(status == 0 ? "锁定成功" : "解锁成功");
    }

}
