package top.codecrab.srb.core.controller.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.base.utils.JwtUtils;
import top.codecrab.srb.common.config.Constants;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.excetion.BusinessException;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.common.utils.CommonUtils;
import top.codecrab.srb.common.utils.ValidationUtil;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.UserInfo;
import top.codecrab.srb.core.entity.vo.LoginVo;
import top.codecrab.srb.core.entity.vo.RegisterVo;
import top.codecrab.srb.core.entity.vo.UserIndexVo;
import top.codecrab.srb.core.entity.vo.UserInfoVo;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "会员接口")
@Slf4j
@RestController
@RequestMapping("/api/core/userInfo")
public class UserInfoController extends CoreBaseController {

    @ApiOperation("会员注册")
    @PostMapping("/register")
    public Result register(
            @ApiParam(value = "用户注册对象", required = true)
            @RequestBody RegisterVo registerVo
    ) {
        // 使用hibernate进行验证
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(registerVo);
        if (validResult.hasErrors()) {
            throw new BusinessException(validResult.getErrors());
        }
        // 取出用户手机号
        String mobile = registerVo.getMobile();

        // 取出缓存的验证码
        Object code = redisTemplate.opsForValue().get(Constants.REDIS_SRB_SMS_CODE_KEY + mobile);
        Assert.equals(code, registerVo.getCode(), ResponseEnum.CODE_ERROR);

        // 调用service层进行注册
        userInfoService.register(registerVo);

        // 清除redis的验证码
        redisTemplate.delete(Constants.REDIS_SRB_SMS_CODE_KEY + mobile);
        return Result.ok();
    }

    @ApiOperation("会员登录")
    @PostMapping("/login")
    public Result login(
            @ApiParam(value = "用户登录对象", required = true)
            @RequestBody LoginVo loginVo,
            HttpServletRequest request
    ) {
        // 使用hibernate进行验证
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(loginVo);
        if (validResult.hasErrors()) {
            throw new BusinessException(validResult.getErrors());
        }

        String ip = CommonUtils.getRemoteHost(request);
        UserInfoVo userInfoVo = userInfoService.login(loginVo, ip);
        return Result.ok("userInfo", userInfoVo);
    }

    @ApiOperation("校验用户是否登录")
    @GetMapping("/checkToken")
    public Result checkToken() {
        String token = request.getHeader("token");
        boolean checkToken = JwtUtils.checkToken(token);
        return checkToken ? Result.ok("校验通过") : Result.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
    }

    @ApiOperation("校验手机号是否已注册")
    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobile(
            @ApiParam(value = "用户手机号", required = true)
            @PathVariable("mobile") String mobile
    ) {
        return userInfoService.count(new QueryWrapper<UserInfo>()
                .eq("mobile", mobile)) > 0;
    }

    @ApiOperation("获取个人空间用户信息")
    @GetMapping("/auth/getIndexUserInfo")
    public Result getIndexUserInfo() {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        UserIndexVo userInfoVo = userInfoService.getIndexUserInfo(userId);
        return Result.ok("userIndexVO", userInfoVo);
    }

}
