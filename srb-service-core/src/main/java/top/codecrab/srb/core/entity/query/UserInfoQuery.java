package top.codecrab.srb.core.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author codecrab
 * @since 2021年04月29日 9:09
 */
@Data
@ApiModel(description = "会员搜索对象")
public class UserInfoQuery {

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "1：出借人 2：借款人")
    private Integer userType;
}
