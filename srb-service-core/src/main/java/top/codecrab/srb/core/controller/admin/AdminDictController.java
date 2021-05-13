package top.codecrab.srb.core.controller.admin;


import com.alibaba.excel.EasyExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.dto.ExcelDictDTO;
import top.codecrab.srb.core.entity.vo.DictVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "数据字典管理")
@RestController
@RequestMapping("/admin/core/dict")
public class AdminDictController extends CoreBaseController {

    @ApiOperation("批量导入Excel数据字典")
    @PostMapping("/import")
    public Result batchImportData(
            @ApiParam(value = "数据字典对象", required = true)
            @RequestParam("file") MultipartFile file
    ) {
        dictService.importData(file);
        return Result.ok("数据字典批量导入成功");
    }

    @ApiOperation("导出Excel数据字典")
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyExcel没有关系
        String fileName = URLEncoder.encode("数据字典文件", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        // 获取字典列表
        List<ExcelDictDTO> list = dictService.findAllExcelDictDTO();
        EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(list);
    }

    @ApiOperation("根据数据字典的父级Id查询子列表")
    @GetMapping("/listByParentId")
    public Result listByParentId(
            @ApiParam(value = "数据字典列表的父级Id")
            @RequestParam(value = "parentId", required = false) Long parentId,
            @ApiParam(value = "是否采用懒加载")
            @RequestParam(value = "isLazy", required = false) Boolean isLazy
    ) {
        List<DictVo> dictVos = dictService.listByParentId(parentId, isLazy);
        return Result.ok("list", dictVos);
    }
}
