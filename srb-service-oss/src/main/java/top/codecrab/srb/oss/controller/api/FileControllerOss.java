package top.codecrab.srb.oss.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.codecrab.srb.common.excetion.BusinessException;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.common.response.Result;
import top.codecrab.srb.oss.controller.base.OssBaseController;

import java.io.IOException;

/**
 * @author codecrab
 * @since 2021年04月26日 17:52
 */
@Api(tags = "阿里云文件管理")
@RestController
@RequestMapping("/api/oss/file")
public class FileControllerOss extends OssBaseController {

    /**
     * 文件上传
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result upload(
            @ApiParam(value = "文件", required = true)
            @RequestParam("file") MultipartFile file,
            @ApiParam(value = "模块", required = true)
            @RequestParam("module") String module
    ) {
        try {
            String uploadUrl = fileService.upload(file.getInputStream(), module, file.getOriginalFilename());
            return Result.ok("文件上传成功", "url", uploadUrl);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @ApiOperation("删除阿里云OSS文件")
    @DeleteMapping("/remove")
    public Result remove(
            @ApiParam(value = "要删除的文件路径", required = true)
            @RequestParam("url") String url
    ) {
        fileService.removeFile(url);
        return Result.ok("文件删除成功");
    }
}
