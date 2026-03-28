package org.example.dormrepairsystem.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.CannedAccessControlList;
import org.example.dormrepairsystem.config.OSSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class OSSUtil {
    
    @Autowired
    private OSS ossClient;
    
    @Autowired
    private OSSConfig ossConfig;
    
    /**
     * 上传图片到OSS
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        // 生成唯一文件名
        String fileName = generateFileName(file.getOriginalFilename());
        String objectName = folder + "/" + fileName;
        
        // 设置文件元数据
        ObjectMetadata metadata = new ObjectMetadata();//oss文件元数据对象，用来描述文件属性
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        
        // 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest request = new PutObjectRequest(ossConfig.getBucketName(), objectName, inputStream, metadata);
            ossClient.putObject(request);
        }
        
        // 返回文件URL
        return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint().replace("https://", "") + "/" + objectName;
    }
    
    /**
     * 删除OSS上的图片
     */
    public void deleteImage(String imageUrl) {
        // 从URL中提取objectName
        String objectName = imageUrl.substring(imageUrl.indexOf(ossConfig.getBucketName()) + ossConfig.getBucketName().length() + 1);
        ossClient.deleteObject(ossConfig.getBucketName(), objectName);
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFilename) {
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return timestamp + "_" + uuid + suffix;
    }
}