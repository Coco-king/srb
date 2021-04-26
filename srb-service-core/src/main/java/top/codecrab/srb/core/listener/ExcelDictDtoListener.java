package top.codecrab.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.codecrab.srb.common.excetion.BusinessException;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.core.entity.dto.ExcelDictDTO;
import top.codecrab.srb.core.mapper.DictMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author codecrab
 * @since 2021年04月25日 15:33
 */
@Slf4j
@NoArgsConstructor
public class ExcelDictDtoListener extends AnalysisEventListener<ExcelDictDTO> {

    /**
     * excel数据的临时数组
     */
    private static final List<ExcelDictDTO> EXCEL_DICT_LIST = new ArrayList<>();

    /**
     * 批量导入的最大值，超过就先清空数组后再次插入
     */
    private static final int BATCH_COUNT = 3000;

    private DictMapper dictMapper;

    public ExcelDictDtoListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {
        log.info("读取到一条记录：{}", excelDictDTO);
        EXCEL_DICT_LIST.add(excelDictDTO);
        // 当list中的数据条数大于等于BATCH_COUNT，就执行保存
        if (EXCEL_DICT_LIST.size() >= BATCH_COUNT) {
            saveExcelData();
            // 清空list集合
            EXCEL_DICT_LIST.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 防止还有不足BATCH_COUNT的数据未保存，在invoke执行完毕后，将剩余的数据保存
        saveExcelData();
        log.info("excel读取完毕");
    }

    private void saveExcelData() {
        log.info("{} 条数据保存中...", EXCEL_DICT_LIST.size());
        int result;
        try {
            result = dictMapper.insertBatch(EXCEL_DICT_LIST);
        } catch (Exception e) {
            EXCEL_DICT_LIST.clear();
            throw new BusinessException(ResponseEnum.IMPORT_DATA_ERROR, e);
        }
        log.info("{} 条数据保存成功！", result);
    }
}
