package com.my.picturesystembackend.manager;

import com.my.picturesystembackend.cofig.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

/**
 * 对象存储常用方法
 */
@Component
@RequiredArgsConstructor
public class CosManager {

    private final CosClientConfig cosClientConfig;

    private final COSClient cosClient;

    /**
     * 上传本地文件
     * @param key 唯一键
     * @param file 本地文件
     * @return 响应结果
     */
    public PutObjectResult putLocalObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传流文件
     * @param key 唯一键
     * @param inputStream 流文件
     * @param objectMetadata 元数据
     * @return 响应结果
     */
    public PutObjectResult putStreamObject(String key, InputStream inputStream, ObjectMetadata objectMetadata) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, inputStream, objectMetadata);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 分块上传
     * @param request 请求
     * @return 响应结果
     */
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request) {
        return null;
    }

    /**
     * 获取下载输入流
     * @param key 唯一键
     * @return 文件流
     */
    public COSObject getCosObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传对象（附带图片信息）
     * https://cloud.tencent.com/document/product/436/55377
     *
     * @param key 唯一键
     * @param file 文件
     * @return 响应结果
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }
}
