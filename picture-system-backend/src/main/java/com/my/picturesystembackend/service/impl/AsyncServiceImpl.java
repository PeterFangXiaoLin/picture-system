package com.my.picturesystembackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.my.picturesystembackend.config.CosClientConfig;
import com.my.picturesystembackend.constant.PictureConstant;
import com.my.picturesystembackend.manager.CosManager;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.service.AsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncServiceImpl implements AsyncService {

    private final StringRedisTemplate stringRedisTemplate;

    private final CosManager cosManager;

    private final CosClientConfig cosClientConfig;

    @Override
    @Async
    public void asyncDeleteCache(Long pictureId) {
        if (pictureId == null) {
            return;
        }

        try {
            // 延迟一段时间再次删除缓存，通常建议50-100ms
            Thread.sleep(100);

            // 清楚所有缓存
            Set<String> keys = stringRedisTemplate.keys(PictureConstant.REDIS_KEY_PREFIX + "*");
            if (CollUtil.isNotEmpty(keys)) {
                stringRedisTemplate.delete(keys);
                log.info("Cleared cache for picture ID: {}", pictureId);
            }
            log.info("Executed delayed cache deletion for picture ID: {}", pictureId);
        } catch (InterruptedException e) {
            log.error("Delayed cache deletion interrupted for picture ID: {}", pictureId, e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    @Async
    public void clearPictureFile(Picture oldPicture) {
        if (oldPicture == null) {
            return;
        }
        deleteManagedObject(oldPicture.getUrl(), "picture");
        deleteManagedObject(oldPicture.getCompressedUrl(), "compressed picture");
    }

    /**
     * 只删除当前 COS 域名下的对象，避免把外链错误转换为桶内 key。
     */
    private void deleteManagedObject(String url, String objectType) {
        if (StrUtil.isBlank(url)) {
            return;
        }
        String host = StrUtil.removeSuffix(cosClientConfig.getHost(), "/");
        if (StrUtil.isBlank(host) || !url.startsWith(host + "/")) {
            log.debug("Skipped deleting external {} URL: {}", objectType, url);
            return;
        }
        String key = url.substring(host.length());
        cosManager.deleteObject(key);
        log.info("Deleted {} file: {}", objectType, key);
    }
}
