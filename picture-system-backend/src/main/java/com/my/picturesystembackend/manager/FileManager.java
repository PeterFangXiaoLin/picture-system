package com.my.picturesystembackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.my.picturesystembackend.cofig.CosClientConfig;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

/**
 * 通用文件上传
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileManager {

    private final CosClientConfig cosClientConfig;

    private final CosManager cosManager;

    private static final long ONE_MB = 1024 * 1024L;

    private static final Set<String> ALLOW_FILE_FORMAT = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");

    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // 校验图片
        validPicture(multipartFile);
        // 构造图片上传key（因为相同文件名称会覆盖）
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);
        // multipartFile转file（需要删除临时文件）
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);

            // 上传文件
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);

            // 获取图片信息:https://cloud.tencent.com/document/product/436/55377
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            String format = imageInfo.getFormat();
            int width = imageInfo.getWidth();
            int height = imageInfo.getHeight();
            double picScale = NumberUtil.round((double) width / height, 2).doubleValue();

            // 封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost() + uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(uploadFileName));
            uploadPictureResult.setPicSize(multipartFile.getSize());
            uploadPictureResult.setPicWidth(width);
            uploadPictureResult.setPicHeight(height);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(format);

            return uploadPictureResult;
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 删除临时文件
            deleteTempFile(file);
        }
    }

    /**
     * 删除临时文件
     * @param file 文件
     */
    public void deleteTempFile(File file) {
        if (file != null) {
            boolean delete = file.delete();
            if (!delete) {
                log.error("file delete error, filepath = {}", file.getAbsolutePath());
            }
        }
    }

    /**
     * 校验图片
     * @param multipartFile 图片文件
     */
    private void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        // 检验图片大小
        long fileSize = multipartFile.getSize();
        ThrowUtils.throwIf(fileSize > 5 * ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小不能超过5MB");
        // 校验文件后缀
        String originalFilename = multipartFile.getOriginalFilename();
        ThrowUtils.throwIf(originalFilename == null, ErrorCode.PARAMS_ERROR, "文件名称不能为空");
        String fileSuffix = FileUtil.getSuffix(originalFilename);
        ThrowUtils.throwIf(!ALLOW_FILE_FORMAT.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件格式错误");
    }

}
