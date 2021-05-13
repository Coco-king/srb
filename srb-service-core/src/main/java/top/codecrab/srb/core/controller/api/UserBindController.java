package top.codecrab.srb.core.controller.api;


import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.excetion.BusinessException;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.vo.UserBindVo;
import top.codecrab.srb.core.hfb.RequestHelper;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "会员账号绑定")
@Slf4j
@RestController
@RequestMapping("/api/core/userBind")
public class UserBindController extends CoreBaseController {

    @ApiOperation("会员绑定")
    @PostMapping("/auth/bind")
    public Result bind(
            @ApiParam(value = "用户绑定VO模型")
            @RequestBody UserBindVo userBindVo
    ) {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        String formStr = userBindService.commitBindUser(userBindVo, userId);
        return Result.ok("formStr", formStr);
    }

    @ApiOperation("会员绑定汇付宝回调")
    @PostMapping("/notify")
    public String bindNotify() {

        Map<String, Object> params = RequestHelper.switchMap(request.getParameterMap());
        String message = JSONUtil.toJsonStr(params);
        log.info("接收到的回调参数为：{}", message);

        if (!RequestHelper.isSignEquals(params)) {
            log.error("用户绑定异步回调验证签名失败：{}", message);
            return "fail";
        }

        userBindService.notify(params);

        return "success";
    }

}
