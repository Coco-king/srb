package top.codecrab.srb.core.service;

import org.springframework.web.multipart.MultipartFile;
import top.codecrab.srb.core.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.srb.core.entity.dto.ExcelDictDTO;
import top.codecrab.srb.core.entity.vo.DictVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface DictService extends IService<Dict> {

    /**
     * 导入excel文件数据到数据库
     *
     * @param file excel文件
     */
    void importData(MultipartFile file);

    /**
     * 查询所有的Dict并转换为ExcelDictDTO返回
     *
     * @return ExcelDictDTO列表
     */
    List<ExcelDictDTO> findAllExcelDictDTO();

    /**
     * 根据父Id查询子列表
     *
     * @param parentId 父级Id
     * @param isLazy   是否懒加载，默认采用立即加载
     * @return 数据字典的视图增强对象列表
     */
    List<DictVo> listByParentId(Long parentId, Boolean isLazy);

    /**
     * 根据dictCode获取下级节点
     *
     * @param dictCode 字典编码
     * @return 字典列表
     */
    List<DictVo> findByDictCode(String dictCode);

    /**
     * 根据dictCodes批量获取下级节点
     *
     * @param dictCodes 字典编码
     * @return 字典列表
     */
    Map<String, Object> findByDictCodes(List<String> dictCodes);

    /**
     * 根据字典编码和字典值获取字典的名称
     *
     * @param dictCode 字典编码
     * @param value    字典的值
     * @return 字典名称
     */
    String getNameByParentDictCodeAndValue(String dictCode, Integer value);
}
