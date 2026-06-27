package com.my.picturesystembackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.picturesystembackend.api.aliyunai.AliYunAiApi;
import com.my.picturesystembackend.api.aliyunai.model.CreateOutPaintingTaskRequest;
import com.my.picturesystembackend.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.my.picturesystembackend.api.aliyunai.model.GetOutPaintingTaskResponse;
import com.my.picturesystembackend.constant.CommonConstant;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.mapper.OutPaintingTaskMapper;
import com.my.picturesystembackend.model.dto.outpainting.OutPaintingTaskQueryRequest;
import com.my.picturesystembackend.model.dto.outpainting.RetryOutPaintingTaskRequest;
import com.my.picturesystembackend.model.dto.picture.CreatePictureOutPaintingTaskRequest;
import com.my.picturesystembackend.model.entity.OutPaintingTask;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.enums.OutPaintingTaskStatusEnum;
import com.my.picturesystembackend.model.vo.OutPaintingQuotaVO;
import com.my.picturesystembackend.model.vo.OutPaintingTaskStatisticsVO;
import com.my.picturesystembackend.model.vo.OutPaintingTaskVO;
import com.my.picturesystembackend.service.OutPaintingTaskService;
import com.my.picturesystembackend.service.PictureService;
import com.my.picturesystembackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI 扩图任务记录 Service 实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OutPaintingTaskServiceImpl extends ServiceImpl<OutPaintingTaskMapper, OutPaintingTask>
        implements OutPaintingTaskService {

    private final AliYunAiApi aliYunAiApi;
    private final PictureService pictureService;
    private final UserService userService;

    @Override
    public OutPaintingTaskVO createOutPaintingTask(CreatePictureOutPaintingTaskRequest request, User loginUser) {
        ThrowUtils.throwIf(request == null || request.getPictureId() == null, ErrorCode.PARAMS_ERROR);
        Picture picture = Optional.ofNullable(pictureService.getById(request.getPictureId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR));
        pictureService.checkPictureAuth(loginUser, picture);

        boolean needDeductQuota = !userService.isAdmin(loginUser);
        if (needDeductQuota) {
            userService.deductOutPaintingQuota(loginUser);
        }

        try {
            CreateOutPaintingTaskRequest taskRequest = buildTaskRequest(request, picture);
            CreateOutPaintingTaskResponse response = aliYunAiApi.createOutPaintingTask(taskRequest);
            CreateOutPaintingTaskResponse.Output output = response.getOutput();
            ThrowUtils.throwIf(output == null || StrUtil.isBlank(output.getTaskId()), ErrorCode.OPERATION_ERROR, "创建扩图任务失败");

            OutPaintingTask task = new OutPaintingTask();
            task.setTaskId(output.getTaskId());
            task.setPictureId(picture.getId());
            task.setUserId(loginUser.getId());
            task.setTaskStatus(StrUtil.blankToDefault(output.getTaskStatus(), OutPaintingTaskStatusEnum.PENDING.getValue()));
            task.setOriginalImageUrl(picture.getUrl());
            task.setQuotaRefunded(0);
            if (request.getParameters() != null) {
                task.setParameters(JSONUtil.toJsonStr(request.getParameters()));
            }
            boolean saved = this.save(task);
            ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR, "保存扩图任务记录失败");
            return getOutPaintingTaskVO(task, picture.getName());
        } catch (Exception e) {
            if (needDeductQuota) {
                userService.refundOutPaintingQuota(loginUser.getId());
            }
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建扩图任务失败");
        }
    }

    @Override
    public OutPaintingTaskVO retryOutPaintingTask(RetryOutPaintingTaskRequest request, User loginUser) {
        ThrowUtils.throwIf(request == null || request.getId() == null, ErrorCode.PARAMS_ERROR);
        OutPaintingTask oldTask = Optional.ofNullable(this.getById(request.getId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "任务记录不存在"));
        checkTaskPermission(loginUser, oldTask);

        CreatePictureOutPaintingTaskRequest createRequest = new CreatePictureOutPaintingTaskRequest();
        createRequest.setPictureId(oldTask.getPictureId());
        if (StrUtil.isNotBlank(oldTask.getParameters())) {
            createRequest.setParameters(JSONUtil.toBean(oldTask.getParameters(),
                    CreateOutPaintingTaskRequest.Parameters.class));
        }
        OutPaintingTaskVO newTaskVO = createOutPaintingTask(createRequest, loginUser);
        this.lambdaUpdate()
                .eq(OutPaintingTask::getId, newTaskVO.getId())
                .set(OutPaintingTask::getParentTaskId, oldTask.getId())
                .update();
        newTaskVO.setParentTaskId(oldTask.getId());
        return newTaskVO;
    }

    @Override
    public GetOutPaintingTaskResponse getAndSyncOutPaintingTask(String taskId, User loginUser) {
        ThrowUtils.throwIf(StrUtil.isBlank(taskId), ErrorCode.PARAMS_ERROR);
        OutPaintingTask task = this.lambdaQuery()
                .eq(OutPaintingTask::getTaskId, taskId)
                .one();
        if (task != null) {
            checkTaskPermission(loginUser, task);
        }
        GetOutPaintingTaskResponse response = aliYunAiApi.getOutPaintingTask(taskId);
        if (task != null) {
            syncTaskFromRemoteResponse(task, response);
        }
        return response;
    }

    @Override
    public OutPaintingTaskVO getOutPaintingTaskVOById(Long id, User loginUser, boolean syncRemote) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        OutPaintingTask task = Optional.ofNullable(this.getById(id))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "任务记录不存在"));
        checkTaskPermission(loginUser, task);
        if (syncRemote && !OutPaintingTaskStatusEnum.isFinished(task.getTaskStatus())) {
            GetOutPaintingTaskResponse response = aliYunAiApi.getOutPaintingTask(task.getTaskId());
            syncTaskFromRemoteResponse(task, response);
            task = this.getById(id);
        }
        Picture picture = pictureService.getById(task.getPictureId());
        String pictureName = picture != null ? picture.getName() : null;
        return getOutPaintingTaskVO(task, pictureName);
    }

    @Override
    public Page<OutPaintingTaskVO> listOutPaintingTaskVOByPage(OutPaintingTaskQueryRequest queryRequest, User loginUser) {
        queryRequest.setUserId(loginUser.getId());
        return listOutPaintingTaskVOPage(queryRequest);
    }

    @Override
    public Page<OutPaintingTaskVO> listOutPaintingTaskVOByPageAdmin(OutPaintingTaskQueryRequest queryRequest) {
        return listOutPaintingTaskVOPage(queryRequest);
    }

    @Override
    public OutPaintingTaskStatisticsVO getOutPaintingTaskStatistics() {
        long totalCount = this.count();
        long successCount = this.lambdaQuery()
                .eq(OutPaintingTask::getTaskStatus, OutPaintingTaskStatusEnum.SUCCEEDED.getValue())
                .count();
        long failedCount = this.lambdaQuery()
                .eq(OutPaintingTask::getTaskStatus, OutPaintingTaskStatusEnum.FAILED.getValue())
                .count();
        long processingCount = totalCount - successCount - failedCount;

        OutPaintingTaskStatisticsVO statisticsVO = new OutPaintingTaskStatisticsVO();
        statisticsVO.setTotalCount(totalCount);
        statisticsVO.setSuccessCount(successCount);
        statisticsVO.setFailedCount(failedCount);
        statisticsVO.setProcessingCount(processingCount);
        statisticsVO.setSuccessRate(calculateRate(successCount, totalCount));
        statisticsVO.setFailureRate(calculateRate(failedCount, totalCount));
        return statisticsVO;
    }

    @Override
    public OutPaintingQuotaVO getOutPaintingQuota(User loginUser) {
        return userService.getOutPaintingQuotaVO(loginUser);
    }

    @Override
    public QueryWrapper<OutPaintingTask> getQueryWrapper(OutPaintingTaskQueryRequest queryRequest) {
        QueryWrapper<OutPaintingTask> queryWrapper = new QueryWrapper<>();
        if (queryRequest == null) {
            return queryWrapper;
        }
        Long id = queryRequest.getId();
        String taskId = queryRequest.getTaskId();
        Long pictureId = queryRequest.getPictureId();
        Long userId = queryRequest.getUserId();
        String taskStatus = queryRequest.getTaskStatus();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(taskId), "taskId", taskId);
        queryWrapper.eq(ObjUtil.isNotNull(pictureId), "pictureId", pictureId);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        queryWrapper.eq(StrUtil.isNotBlank(taskStatus), "taskStatus", taskStatus);
        queryWrapper.ge(ObjUtil.isNotNull(queryRequest.getStartCreateTime()), "createTime", queryRequest.getStartCreateTime());
        queryWrapper.le(ObjUtil.isNotNull(queryRequest.getEndCreateTime()), "createTime", queryRequest.getEndCreateTime());
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
        queryWrapper.orderByDesc(StrUtil.isBlank(sortField), "createTime");
        return queryWrapper;
    }

    private Page<OutPaintingTaskVO> listOutPaintingTaskVOPage(OutPaintingTaskQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<OutPaintingTask> taskPage = this.page(new Page<>(current, size), getQueryWrapper(queryRequest));
        Page<OutPaintingTaskVO> voPage = new Page<>(current, size, taskPage.getTotal());
        List<OutPaintingTask> records = taskPage.getRecords();
        if (records.isEmpty()) {
            return voPage;
        }
        Set<Long> pictureIds = records.stream().map(OutPaintingTask::getPictureId).collect(Collectors.toSet());
        Map<Long, String> pictureNameMap = pictureService.listByIds(pictureIds).stream()
                .collect(Collectors.toMap(Picture::getId, Picture::getName, (a, b) -> a));
        List<OutPaintingTaskVO> voList = records.stream()
                .map(task -> getOutPaintingTaskVO(task, pictureNameMap.get(task.getPictureId())))
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    private CreateOutPaintingTaskRequest buildTaskRequest(CreatePictureOutPaintingTaskRequest request, Picture picture) {
        CreateOutPaintingTaskRequest taskRequest = new CreateOutPaintingTaskRequest();
        CreateOutPaintingTaskRequest.Input input = new CreateOutPaintingTaskRequest.Input();
        input.setImageUrl(picture.getUrl());
        taskRequest.setInput(input);
        taskRequest.setParameters(request.getParameters());
        return taskRequest;
    }

    private void syncTaskFromRemoteResponse(OutPaintingTask task, GetOutPaintingTaskResponse response) {
        if (response == null || response.getOutput() == null) {
            return;
        }
        GetOutPaintingTaskResponse.Output output = response.getOutput();
        String oldStatus = task.getTaskStatus();
        String newStatus = output.getTaskStatus();
        OutPaintingTask updateTask = new OutPaintingTask();
        updateTask.setId(task.getId());
        if (StrUtil.isNotBlank(newStatus)) {
            updateTask.setTaskStatus(newStatus);
        }
        if (StrUtil.isNotBlank(output.getOutputImageUrl())) {
            updateTask.setOutputImageUrl(output.getOutputImageUrl());
        }
        if (StrUtil.isNotBlank(output.getCode())) {
            updateTask.setErrorCode(output.getCode());
        }
        if (StrUtil.isNotBlank(output.getMessage())) {
            updateTask.setErrorMessage(output.getMessage());
        }
        this.updateById(updateTask);

        boolean becameFailed = OutPaintingTaskStatusEnum.FAILED.getValue().equals(newStatus)
                && !OutPaintingTaskStatusEnum.FAILED.getValue().equals(oldStatus);
        boolean notRefunded = task.getQuotaRefunded() == null || task.getQuotaRefunded() == 0;
        if (becameFailed && notRefunded) {
            userService.refundOutPaintingQuota(task.getUserId());
            this.lambdaUpdate()
                    .eq(OutPaintingTask::getId, task.getId())
                    .set(OutPaintingTask::getQuotaRefunded, 1)
                    .update();
        }
    }

    private OutPaintingTaskVO getOutPaintingTaskVO(OutPaintingTask task, String pictureName) {
        OutPaintingTaskVO vo = new OutPaintingTaskVO();
        BeanUtil.copyProperties(task, vo, "parameters");
        vo.setPictureName(pictureName);
        if (StrUtil.isNotBlank(task.getParameters())) {
            vo.setParameters(JSONUtil.toBean(task.getParameters(), CreateOutPaintingTaskRequest.Parameters.class));
        }
        return vo;
    }

    private void checkTaskPermission(User loginUser, OutPaintingTask task) {
        if (!task.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    private double calculateRate(long part, long total) {
        if (total <= 0) {
            return 0D;
        }
        return BigDecimal.valueOf(part * 100D / total)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
