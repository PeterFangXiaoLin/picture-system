package com.my.picturesystembackend.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.my.picturesystembackend.config.CosClientConfig;
import com.my.picturesystembackend.model.enums.PictureFileSuffixEnum;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
     * https://cloud.tencent.com/document/product/436/55377
     *
     * @param key 唯一键
     * @param file 文件
     * @return 响应结果
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 添加图片处理规则
        List<PicOperations.Rule> ruleList = new ArrayList<>();

        // 非webp图片才进行压缩
        String suffix = FileUtil.getSuffix(key);
        if (StrUtil.isNotBlank(suffix) && !PictureFileSuffixEnum.WEBP.equals(PictureFileSuffixEnum.getEnumByValue(suffix.toLowerCase()))) {
            // 图片压缩 （转成webp格式）
            PicOperations.Rule compressRule = new PicOperations.Rule();
            compressRule.setBucket(cosClientConfig.getBucket());
            compressRule.setFileId(FileUtil.mainName(key) + ".webp");
            compressRule.setRule("imageMogr2/format/webp");
            ruleList.add(compressRule);
        }

        picOperations.setRules(ruleList);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    public void deleteObject(String key) {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }

    /**
     * 根据图片 URL 获取主色调
     * https://cloud.tencent.com/document/product/460/6928
     *
     * @param pictureUrl 图片 URL
     * @return 主色调，格式 0xRRGGBB
     */
    public String getImageDominantColor(String pictureUrl) {
        String key = parseObjectKeyFromUrl(pictureUrl);
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        getObjectRequest.putCustomQueryParameter("imageAve", null);
        COSObject cosObject = null;
        try {
            cosObject = cosClient.getObject(getObjectRequest);
            try (COSObjectInputStream objectContent = cosObject.getObjectContent()) {
                String responseBody = IoUtil.readUtf8(objectContent);
                JSONObject jsonObject = JSONUtil.parseObj(responseBody);
                return jsonObject.getStr("RGB");
            }
        } catch (Exception e) {
            throw new RuntimeException("获取图片主色调失败, url=" + pictureUrl, e);
        } finally {
            IoUtil.close(cosObject);
        }
    }

    /**
     * 从图片 URL 解析 COS 对象 key
     *
     * @param pictureUrl 图片 URL
     * @return 对象 key
     */
    private String parseObjectKeyFromUrl(String pictureUrl) {
        if (StrUtil.isBlank(pictureUrl)) {
            throw new IllegalArgumentException("图片地址不能为空");
        }
        String host = StrUtil.removeSuffix(cosClientConfig.getHost().trim(), "/");
        String url = pictureUrl.trim();
        if (!url.startsWith(host)) {
            throw new IllegalArgumentException("非本系统的图片地址");
        }
        String key = url.substring(host.length());
        key = key.replaceAll("^/+", "/");
        if (!key.startsWith("/")) {
            key = "/" + key;
        }
        return key;
    }
}
