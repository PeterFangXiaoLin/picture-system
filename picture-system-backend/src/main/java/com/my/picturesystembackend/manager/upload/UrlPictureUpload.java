package com.my.picturesystembackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.my.picturesystembackend.config.CosClientConfig;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.manager.CosManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.my.picturesystembackend.constant.UploadPictureConstant.*;

@Service
public class UrlPictureUpload extends PictureUploadTemplate {

    public UrlPictureUpload(CosManager cosManager, CosClientConfig cosClientConfig) {
        super(cosManager, cosClientConfig);
    }

    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;

        // 1. 校验非空
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件url不能为空");

        // 2. 校验url格式
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件url格式错误");
        }

        // 3. 校验URL 协议
        ThrowUtils.throwIf(!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"),
                ErrorCode.PARAMS_ERROR, "文件url协议错误");

        // 4. 发送 HEAD 请求验证文件是否存在
        HttpResponse httpResponse = null;
        try {
            httpResponse = HttpUtil.createRequest(Method.HEAD, fileUrl)
                    .execute();
            // 未正常返回，无需执行其他判断
            if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 5. 文件存在，校验文件类型
            String contentType = httpResponse.header("Content-Type");
            // 不为空，才校验是否合法
            if (StrUtil.isNotBlank(contentType)) {
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "文件类型错误");
            }

            // 6. 文件存在，校验文件大小
            long contentLength = httpResponse.contentLength();
            ThrowUtils.throwIf(contentLength > 5 * ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小错误");
        } finally {
            // 关闭响应
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        String originFilename = FileUtil.getName(fileUrl);
        String fileNameSuffix = FileUtil.getSuffix(originFilename);
        // 不包含点
        if (StrUtil.isBlank(fileNameSuffix)) {
            // head 请求获取 Content-Type 补充
            try (HttpResponse httpResponse = HttpUtil.createRequest(Method.HEAD, fileUrl)
                    .execute()) {
                // 未正常返回，无需执行其他判断
                if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                    return originFilename;
                }
                // 获取Content-Type
                String contentType = httpResponse.header("Content-Type");
                if (StrUtil.isNotBlank(contentType)) {
                    if (CONTENT_TYPE_TO_FILE_FORMAT.containsKey(contentType)) {
                        originFilename = originFilename + StrUtil.DOT + CONTENT_TYPE_TO_FILE_FORMAT.get(contentType);
                    }
                }
            }
        }

        return originFilename;
    }

    @Override
    protected void processFile(Object inputSource, File file) throws IOException {
        String fileUrl = (String) inputSource;
        HttpUtil.downloadFile(fileUrl, file);
    }
}
