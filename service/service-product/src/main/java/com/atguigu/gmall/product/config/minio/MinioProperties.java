package com.atguigu.gmall.product.config.minio;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.minio")
//和配置文件绑定
//自动把配置文件钟的app.minio属性值和javabean钟的属性值一一对应
public class MinioProperties {
    @Value("${app.minio.endpoint}")
    String endpoint;
    String ak;
    String sk;
    String bucketName;
    //加配置 。属性类加属性
}
