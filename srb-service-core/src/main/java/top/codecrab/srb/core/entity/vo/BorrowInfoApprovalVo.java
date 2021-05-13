package top.codecrab.srb.core.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author codecrab
 * @since 2021年05月07日 17:54
 */
@Data
@ApiModel(description = "借款信息审批")
public class BorrowInfoApprovalVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "审批内容")
    private String content;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "年化利率")
    private BigDecimal lendYearRate;

    @ApiModelProperty(value = "平台服务费率")
    private BigDecimal serviceRate;

    @ApiModelProperty(value = "开始日期")
    private String lendStartDate;

    @ApiModelProperty(value = "描述信息")
    private String lendInfo;
}
