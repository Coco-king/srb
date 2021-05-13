package top.codecrab.srb.core.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.BorrowInfo;
import top.codecrab.srb.core.entity.vo.BorrowInfoApprovalVo;

import java.util.List;
import java.util.Map;

/**
 * @author codecrab
 * @since 2021年05月07日 16:05
 */
@Api(tags = "借款管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/borrowInfo")
public class AdminBorrowInfoController extends CoreBaseController {

    @ApiOperation("借款信息列表")
    @GetMapping("/list")
    public Result list() {
        List<BorrowInfo> borrowInfoList = borrowInfoService.selectList();
        return Result.ok("list", borrowInfoList);
    }

    @ApiOperation("获取借款信息")
    @GetMapping("/show/{id}")
    public Result show(
            @ApiParam(value = "借款id", required = true)
            @PathVariable Long id) {
        Map<String, Object> borrowInfoDetail = borrowInfoService.getBorrowInfoDetail(id);
        return Result.ok(borrowInfoDetail);
    }

    @ApiOperation("审批借款信息")
    @PostMapping("/approval")
    public Result approval(@RequestBody BorrowInfoApprovalVo borrowInfoApprovalVo) {
        borrowInfoService.approval(borrowInfoApprovalVo);
        return Result.ok("审批完成");
    }
}
