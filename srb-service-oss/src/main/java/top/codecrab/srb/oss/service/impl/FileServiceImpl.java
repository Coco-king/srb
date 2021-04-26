package top.codecrab.srb.oss.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import org.springframework.stereotype.Service;
import top.codecrab.srb.oss.service.FileService;
import top.codecrab.srb.oss.utils.OssProperties;

import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * @author codecrab
 * @since 2021年04月26日 17:39
 */
@Service
public class FileServiceImpl implements FileService {

    @Override
    public String upload(InputStream inputStream, String module, String fileName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                OssProperties.ENDPOINT,
                OssProperties.KEY_ID,
                OssProperties.KEY_SECRET
        );

        // 判断桶是否存在
        if (!ossClient.doesBucketExist(OssProperties.BUCKET_NAME)) {
            // 不存在则创建桶
            ossClient.createBucket(OssProperties.BUCKET_NAME);
            // 授权
            ossClient.setBucketAcl(OssProperties.BUCKET_NAME, CannedAccessControlList.PublicRead);
        }

        // 组装文件路径  avatar/2021/04/26/fileName.jpg
        String filePath = module +
                DateUtil.format(LocalDateTime.now(), "/yyyy/MM/dd/") +
                IdUtil.fastSimpleUUID() + "." +
                FileUtil.getSuffix(fileName);

        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        ossClient.putObject(OssProperties.BUCKET_NAME, filePath, inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();
        // 阿里云文件绝对路径
        return StrUtil.format("https://{}.{}/{}", OssProperties.BUCKET_NAME, OssProperties.ENDPOINT, filePath);
    }

    @Override
    public void removeFile(String url) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                OssProperties.ENDPOINT,
                OssProperties.KEY_ID,
                OssProperties.KEY_SECRET
        );

        // 需要截断的：https://srb-service-file.oss-cn-beijing.aliyuncs.com/
        String host = StrUtil.format("https://{}.{}/", OssProperties.BUCKET_NAME, OssProperties.ENDPOINT);
        // 需要的：avatar/2021/04/26/8b40dde3d8ea4a2e8303fa3b279b8daa.jpg
        String objectName = url.substring(host.length());

        ossClient.deleteObject(OssProperties.BUCKET_NAME, objectName);

        ossClient.shutdown();
    }
}
