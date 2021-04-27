package top.codecrab.srb.oss.controller.base;

import org.springframework.beans.factory.annotation.Autowired;
import top.codecrab.srb.oss.service.FileService;

/**
 * @author codecrab
 * @since 2021年04月26日 17:54
 */
public class OssBaseController {
    @Autowired
    protected FileService fileService;
}
