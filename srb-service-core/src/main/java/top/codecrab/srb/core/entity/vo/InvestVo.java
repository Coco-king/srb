package top.codecrab.srb.core.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author codecrab
 * @since 2021年05月10日 17:59
 */
@Data
@ApiModel(description = "投标信息")
public class InvestVo {

    /**
     * 标的id
     */
    @NotNull(message = "标的ID不能为空")
    @Range(min = 1, message = "标的ID有误")
    private Long lendId;

    /**
     * 投标金额
     */
    @NotBlank(message = "投标金额不能为空")
    private String investAmount;

    /**
     * 用户id
     */
    private Long investUserId;

    /**
     * 用户姓名
     */
    private String investName;
}
