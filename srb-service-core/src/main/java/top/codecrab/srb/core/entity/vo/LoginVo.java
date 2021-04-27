package top.codecrab.srb.core.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author codecrab
 * @since 2021年04月27日 17:06
 */
@Data
@ApiModel(description = "登录对象")
public class LoginVo {

    @NotNull(message = "用户类型不能为空")
    @Range(min = 1, max = 2, message = "用户类型有误")
    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    @NotBlank(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机号码长度不符")
    @Pattern(regexp = "^(?:0|86|\\+86)?1[3-9]\\d{9}$", message = "手机号格式有误")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 24, message = "密码长度6-24个字符")
    @ApiModelProperty(value = "密码")
    private String password;
}
