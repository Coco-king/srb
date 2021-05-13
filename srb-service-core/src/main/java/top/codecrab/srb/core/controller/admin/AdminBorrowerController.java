package top.codecrab.srb.core.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.common.utils.ValidationUtil;
import top.codecrab.srb.core.controller.base.CoreBaseController;
import top.codecrab.srb.core.entity.Borrower;
import top.codecrab.srb.core.entity.vo.BorrowerApprovalVo;
import top.codecrab.srb.core.entity.vo.BorrowerDetailVo;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Api(tags = "借款人管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController extends CoreBaseController {

    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{size}")
    public Result listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long size,
            @ApiParam(value = "查询关键字")
            @RequestParam String keyword
    ) {
        Page<Borrower> pageModel = borrowerService.listPage(new Page<>(page, size), keyword);
        return Result.ok("pageModel", pageModel);
    }

    @ApiOperation("获取借款人信息")
    @GetMapping("/show/{id}")
    public Result show(
            @ApiParam(value = "借款人id", required = true)
            @PathVariable Long id
    ) {
        BorrowerDetailVo detailVo = borrowerService.getBorrowerDetailVo(id, true);
        return Result.ok("borrowerDetailVo", detailVo);
    }

    @ApiOperation("借款额度审批")
    @PostMapping("/approval")
    public Result approval(@RequestBody BorrowerApprovalVo borrowerApprovalVo) {
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(borrowerApprovalVo);
        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }
        borrowerService.approval(borrowerApprovalVo);
        return Result.ok("审批完成");
    }
}
