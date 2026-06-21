package com.my.picturesystembackend;

import cn.hutool.core.util.StrUtil;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 图片主色调测试
 */
@SpringBootTest
@Slf4j
public class PicturePicColorTest {

    @Autowired
    private PictureService pictureService;

    /**
     * 查询数据库全部图片，逐个获取主色调并回写数据库
     */
    @Test
    void fillPicColorForAllPictures() {
        List<Picture> pictureList = pictureService.list();
        log.info("共查询到 {} 条图片数据", pictureList.size());
        int successCount = 0;
        int skipCount = 0;
        int failCount = 0;
        for (Picture picture : pictureList) {
            String url = picture.getUrl();
            if (StrUtil.isBlank(url)) {
                log.warn("图片 id={} 的 url 为空，跳过", picture.getId());
                skipCount++;
                continue;
            }
            if (StrUtil.isNotBlank(picture.getPicColor())) {
                log.info("图片 id={} 已有主色调 {}，跳过", picture.getId(), picture.getPicColor());
                skipCount++;
                continue;
            }
            try {
                String picColor = pictureService.getPicturePicColor(url);
                log.info("图片 id={}, url={}, picColor={}", picture.getId(), url, picColor);
                Picture updatePicture = new Picture();
                updatePicture.setId(picture.getId());
                updatePicture.setPicColor(picColor);
                pictureService.updateById(updatePicture);
                successCount++;
            } catch (Exception e) {
                log.error("获取主色调失败, id={}, url={}", picture.getId(), url, e);
                failCount++;
            }
        }
        log.info("主色调补全完成，成功 {} 条，跳过 {} 条，失败 {} 条", successCount, skipCount, failCount);
    }
}
