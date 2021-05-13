package top.codecrab.srb.core.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.codecrab.srb.core.enums.TransTypeEnum;

import java.math.BigDecimal;

/**
 * @author codecrab
 * @since 2021年05月10日 9:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransFlowBo {

    private String agentBillNo;
    private String bindCode;
    private String withId;
    private BigDecimal amount;
    private TransTypeEnum transTypeEnum;
    private String memo;

}
