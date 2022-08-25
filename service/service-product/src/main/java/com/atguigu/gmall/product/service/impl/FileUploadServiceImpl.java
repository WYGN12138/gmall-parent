package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.product.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Service

public class FileUploadServiceImpl implements FileUploadService {
    /**
     * 上传文件
     * @param file
     * @return 返回地址
     */

    @Resource
    MinioClient minioClient;
    @Resource
    MinioProperties minioProperties;
    @Override
    public String fileUpload(MultipartFile file) throws Exception {

        //未来其他代码上传还需要new
        //1.创建一个客户端

        //2.判断桶是否存在
        boolean b = minioClient.bucketExists(minioProperties.getBucketName());
        if (!b){
            //桶不存在
            minioClient.makeBucket(minioProperties.getBucketName());
        }
        //3.给桶里上传文件
        String name = file.getName();//input框的名字
        //得到唯一文件名
        String dateStr = DateUtil.formatDate(new Date());
        String filename = UUID.randomUUID().toString().replace("-","")+"_"+file.getOriginalFilename();//文件名
        InputStream inputStream = file.getInputStream();
        PutObjectOptions options = new PutObjectOptions(file.getSize(),-1L);
        //设置属性告诉浏览器是图片 不要下载
        options.setContentType(file.getContentType());
        minioClient.putObject(
                minioProperties.getBucketName(),
                dateStr+"/"+filename,//自己制定的唯一名
                inputStream,
                options);
        //返回地址  ：http://192.168.200.100:9000/sph-trademark/filename
        String url = minioProperties.getEndpoint()+"/"+minioProperties.getBucketName()+"/"+dateStr+"/"+filename;
        /*
            优化
                1.重名文件会覆盖 ---UUID 唯一名
                2.文件过多不易归档----以日期为文件夹名
         */
        return url;
    }
}
