package top.codecrab.srb.core.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author codecrab
 * @since 2021年04月30日 8:52
 */
@Data
@ApiModel(description = "账户绑定")
public class UserBindVo {

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @JsonProperty("name")
    @ApiModelProperty(value = "用户姓名")
    private String personalName;

    @ApiModelProperty(value = "银行类型")
    private String bankType;

    @ApiModelProperty(value = "银行卡号")
    private String bankNo;

    @ApiModelProperty(value = "手机号")
    private String mobile;
}
