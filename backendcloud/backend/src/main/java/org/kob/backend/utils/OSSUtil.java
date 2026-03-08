package org.kob.backend.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
public class OSSUtil {
    // 这些@Value注解能正常读取application.properties里的配置
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    // 上传文件到OSS，返回公网URL
    public String uploadAvatar(MultipartFile file) throws Exception {
        // 1. 生成唯一文件名（避免重复）
        String originalName = file.getOriginalFilename();
        String suffix = null;
        if (originalName != null) {
            suffix = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = "avatar/" + UUID.randomUUID() + suffix; // 存在 avatar文件夹下

        // 2. 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 3. 上传文件到OSS
        ossClient.putObject(bucketName, fileName, file.getInputStream());

        // 4. 生成公网可访问的URL（核心！阿里云自动生成）
        String avatarUrl = "https://" + bucketName + "." + endpoint + "/" + fileName;

        // 5. 关闭客户端
        ossClient.shutdown();

        return avatarUrl;
    }
}