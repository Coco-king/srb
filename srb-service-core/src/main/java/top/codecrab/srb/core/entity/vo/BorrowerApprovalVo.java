package top.codecrab.srb.core.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author codecrab
 * @since 2021年05月06日 11:25
 */
@Data
@ApiModel(description = "借款人审批")
public class BorrowerApprovalVo {

    @ApiModelProperty(value = "id")
    private Long borrowerId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "身份证信息是否正确")
    private Boolean isIdCardOk;

    @ApiModelProperty(value = "房产信息是否正确")
    private Boolean isHouseOk;

    @ApiModelProperty(value = "车辆信息是否正确")
    private Boolean isCarOk;

    @Range(min = 30, max = 100, message = "基本信息获取积分范围在 30 至 100 之间")
    @ApiModelProperty(value = "基本信息积分")
    private Integer infoIntegral;
}
