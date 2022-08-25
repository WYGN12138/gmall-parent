package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;

@SpringBootTest
public class UploadTest {
    @Test
    public void uploadFile() throws Exception {
        try {
            //Minio是一个时间敏感的中间件，
            //The difference between the request time and the server's time is too large
            // 1、使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient("http://192.168.200.100:9000",
                    "admin",
                    "admin123456"); //新建minio客户端配置信息
            //2.检查储存桶对象是否存在了
            boolean isExist = minioClient.bucketExists("sph-trademark");

            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                //3、如果桶不存在需要先创建一个桶
                minioClient.makeBucket("sph-trademark");
            }

            //4、使用putObject上传一个文件到存储桶中。
            /**
             * String bucketName, 桶名
             * String objectName, 对象名，也就是文件名
             * InputStream stream, 文件流  D:\0310\尚品汇\资料\03 商品图片\品牌\pingguo.png
             * PutObjectOptions options, 上传的参数设置
             */
            //文件流
            FileInputStream inputStream = new FileInputStream("E:\\尚硅谷20220310JAVA\\雷神\\1.尚品汇\\资料\\03 商品图片\\品牌\\pingguo.png");
            //文件上传参数：long objectSize, long partSize
            PutObjectOptions options = new PutObjectOptions(inputStream.available(), -1L);
            options.setContentType("image/png");
            //告诉Minio上传的这个文件的内容类型
            minioClient.putObject("sph-trademark", "pingguo.png",
                    inputStream,
                    options
            );
            System.out.println("上传成功");
        } catch (
                MinioException e) {
            System.err.println("发生错误: " + e);
        }
    }


}
