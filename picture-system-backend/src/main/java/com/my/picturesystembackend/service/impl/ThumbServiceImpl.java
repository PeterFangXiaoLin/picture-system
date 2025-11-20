package com.my.picturesystembackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.picturesystembackend.constant.ThumbConstant;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.mapper.ThumbMapper;
import com.my.picturesystembackend.model.dto.thumb.DoThumbRequest;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.entity.Thumb;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.service.PictureService;
import com.my.picturesystembackend.service.ThumbService;
import com.my.picturesystembackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author helloworld
 * @description 针对表【thumb(点赞记录表)】的数据库操作Service实现
 * @createDate 2025-10-29 14:59:09
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb>
        implements ThumbService {

    private final UserService userService;

    private final PictureService pictureService;

    private final TransactionTemplate transactionTemplate;

    private final RedissonClient redisson;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getPictureId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 不校验图片是否存在，因为图片不存在，点赞会失败
        /*Long pictureId = doThumbRequest.getPictureId();
        boolean exists = pictureService.lambdaQuery()
                .eq(Picture::getId, pictureId)
                .exists();
        if (!exists) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }*/

        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }

        RLock lock = redisson.getLock(ThumbConstant.DO_THUMB_KEY + loginUser.getId());
        try {
            // 获取锁，等待时间为0，持有时间为看门狗
            boolean isLock = lock.tryLock(0, -1, TimeUnit.SECONDS);
            if (!isLock) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "操作过于频繁，请稍后再试");
            }

            // 执行点赞业务逻辑
            // 查询是否存在点赞记录
            // 更新点赞数量
            // 增加点赞记录
            // 更新成功才执行
            return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
                Long pictureId = doThumbRequest.getPictureId();
                // 查询是否存在点赞记录
                /*boolean exists = this.lambdaQuery()
                        .eq(Thumb::getUserId, loginUser.getId())
                        .eq(Thumb::getPictureId, pictureId)
                        .exists();*/
                // 修改为查redis
                boolean exists = this.hasThumb(pictureId, loginUser.getId());
                if (exists) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已点赞");
                }

                // 更新点赞数量
                boolean update = pictureService.lambdaUpdate()
                        .eq(Picture::getId, pictureId)
                        .setSql("thumbCount = thumbCount + 1")
                        .update();

                // 增加点赞记录
                Thumb thumb = new Thumb();
                thumb.setUserId(loginUser.getId());
                thumb.setPictureId(pictureId);

                // 更新成功才执行
                boolean success = update && this.save(thumb);
                // 点赞存入redis
                if (success) {
                    redisTemplate.opsForHash()
                            .put(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId().toString(),
                                    pictureId.toString(),
                                    thumb.getId());
                }

                return success;
            }));

        } catch (InterruptedException e) {
            log.error("获取分布式锁失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常");
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getPictureId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }

        RLock lock = redisson.getLock(ThumbConstant.UNDO_THUMB_KEY + loginUser.getId());
        try {
            // 获取锁，等待时间为0，持有时间为看门狗
            boolean isLock = lock.tryLock(0, -1, TimeUnit.SECONDS);
            if (!isLock) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "操作过于频繁，请稍后再试");
            }

            // 执行取消点赞业务逻辑
            // 减少点赞数量
            // 删除点赞记录
            // 删除成功才执行
            // 编程式事务
            return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
                Long pictureId = doThumbRequest.getPictureId();
                // 查询点赞记录是否存在
                Thumb thumb = this.lambdaQuery()
                        .eq(Thumb::getUserId, loginUser.getId())
                        .eq(Thumb::getPictureId, pictureId)
                        .one();
                if (thumb == null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未点赞");
                }
                // 减少点赞数量
                boolean update = pictureService.lambdaUpdate()
                        .eq(Picture::getId, pictureId)
                        .setSql("thumbCount = thumbCount - 1")
                        .update();

                return update && this.removeById(thumb.getId());
            }));
        } catch (InterruptedException e) {
            log.error("获取分布式锁失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常");
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 判断用户是否已点赞
     * @param pictureId 图片id
     * @param userId 用户id
     * @return true 已点赞，false 未点赞
     */
    @Override
    public boolean hasThumb(Long pictureId, Long userId) {
        return redisTemplate.opsForHash().hasKey(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, pictureId.toString());
    }
}




