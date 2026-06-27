package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.api.aliyunai.model.GetOutPaintingTaskResponse;
import com.my.picturesystembackend.model.dto.outpainting.OutPaintingTaskQueryRequest;
import com.my.picturesystembackend.model.dto.outpainting.RetryOutPaintingTaskRequest;
import com.my.picturesystembackend.model.dto.picture.CreatePictureOutPaintingTaskRequest;
import com.my.picturesystembackend.model.entity.OutPaintingTask;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.vo.OutPaintingQuotaVO;
import com.my.picturesystembackend.model.vo.OutPaintingTaskStatisticsVO;
import com.my.picturesystembackend.model.vo.OutPaintingTaskVO;

/**
 * AI 扩图任务记录 Service
 */
public interface OutPaintingTaskService extends IService<OutPaintingTask> {

    /**
     * 创建扩图任务并保存记录
     */
    OutPaintingTaskVO createOutPaintingTask(CreatePictureOutPaintingTaskRequest request, User loginUser);

    /**
     * 重试扩图任务
     */
    OutPaintingTaskVO retryOutPaintingTask(RetryOutPaintingTaskRequest request, User loginUser);

    /**
     * 根据阿里云任务 id 查询并同步状态
     */
    GetOutPaintingTaskResponse getAndSyncOutPaintingTask(String taskId, User loginUser);

    /**
     * 根据任务记录 id 查询并同步状态
     */
    OutPaintingTaskVO getOutPaintingTaskVOById(Long id, User loginUser, boolean syncRemote);

    /**
     * 分页查询当前用户的扩图任务
     */
    Page<OutPaintingTaskVO> listOutPaintingTaskVOByPage(OutPaintingTaskQueryRequest queryRequest, User loginUser);

    /**
     * 分页查询所有扩图任务（管理员）
     */
    Page<OutPaintingTaskVO> listOutPaintingTaskVOByPageAdmin(OutPaintingTaskQueryRequest queryRequest);

    /**
     * 获取扩图任务统计（管理员）
     */
    OutPaintingTaskStatisticsVO getOutPaintingTaskStatistics();

    /**
     * 获取当前用户 AI 扩图剩余次数
     */
    OutPaintingQuotaVO getOutPaintingQuota(User loginUser);

    /**
     * 构建查询条件
     */
    QueryWrapper<OutPaintingTask> getQueryWrapper(OutPaintingTaskQueryRequest queryRequest);
}
