package top.codecrab.srb.core.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.codecrab.srb.core.entity.Dict;

import java.util.ArrayList;
import java.util.List;

/**
 * @author codecrab
 * @since 2021年04月26日 8:33
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Dict视图增强对象", description = "数据字典增强对象 添加了children和hasChildren字段")
public class DictVo extends Dict {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(value = "子节点列表")
    private List<Dict> children = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "是否有子节点")
    private Boolean hasChildren;
}
