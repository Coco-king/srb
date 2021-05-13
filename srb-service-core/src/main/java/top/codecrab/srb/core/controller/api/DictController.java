package top.codecrab.srb.core.controller.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.Dict;
import top.codecrab.srb.core.entity.vo.DictVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "数据字典")
@RestController
@RequestMapping("/api/core/dict")
public class DictController extends CoreBaseController {

    @ApiOperation("根据dictCode获取下级节点")
    @GetMapping("/findByDictCode/{dictCode}")
    public Result findByDictCode(
            @ApiParam(value = "节点编码", required = true)
            @PathVariable String dictCode) {
        List<DictVo> list = dictService.findByDictCode(dictCode);
        return Result.ok("list", list);
    }

    @ApiOperation("根据dictCodes批量获取下级节点")
    @GetMapping("/findByDictCodes")
    public Result findByDictCodes(
            @ApiParam(value = "节点编码", required = true)
            @RequestParam List<String> dictCodes) {
        Map<String, Object> dictMap = dictService.findByDictCodes(dictCodes);
        return Result.ok(dictMap);
    }
}
