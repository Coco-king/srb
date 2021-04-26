package top.codecrab.srb.core.mapper;

import top.codecrab.srb.core.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.codecrab.srb.core.entity.dto.ExcelDictDTO;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 批量插入数据字典
     *
     * @param list 数据字典列表
     * @return 数据库影响行数
     */
    int insertBatch(List<ExcelDictDTO> list);
}
