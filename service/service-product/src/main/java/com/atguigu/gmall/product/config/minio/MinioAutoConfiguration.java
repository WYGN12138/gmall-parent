package com.atguigu.gmall.product.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio 自动配置类
 */
@Configuration //是容器中的组件
public class MinioAutoConfiguration {

    /**
     未来想要进行上传的人，自动注入MinioClient
     */
    @Autowired
    MinioProperties minioProperties;
    @Bean
    public MinioClient minioClient() throws Exception {
        //1.创建客户端
        MinioClient minioClient = new MinioClient(
                minioProperties.getEndpoint(),
                minioProperties.getAk(),
                minioProperties.getSk()
        );
        //2。获得桶名
        String bucketName = minioProperties.getBucketName();
        if (!minioClient.bucketExists(bucketName)) {//如果桶不存在
            minioClient.makeBucket(bucketName);//创建桶
        }
        return minioClient;
    }
}
