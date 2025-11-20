package com.my.picturesystembackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.my.picturesystembackend.config.CosClientConfig;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.manager.CosManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.my.picturesystembackend.constant.UploadPictureConstant.ALLOW_FILE_FORMAT;
import static com.my.picturesystembackend.constant.UploadPictureConstant.ONE_MB;

@Service
public class FilePictureUpload extends PictureUploadTemplate {

    public FilePictureUpload(CosManager cosManager, CosClientConfig cosClientConfig) {
        super(cosManager, cosClientConfig);
    }

    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
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

    @Override
    protected String getOriginFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void processFile(Object inputSource, File file) throws IOException {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }
}
