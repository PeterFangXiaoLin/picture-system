package com.my.picturesystembackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.constant.CommonConstant;
import com.my.picturesystembackend.constant.PictureConstant;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.manager.upload.FilePictureUpload;
import com.my.picturesystembackend.manager.upload.PictureUploadTemplate;
import com.my.picturesystembackend.manager.upload.UrlPictureUpload;
import com.my.picturesystembackend.mapper.PictureMapper;
import com.my.picturesystembackend.model.dto.file.UploadPictureResult;
import com.my.picturesystembackend.model.dto.picture.*;
import com.my.picturesystembackend.model.entity.Category;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.entity.Tag;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.enums.PictureReviewStatusEnum;
import com.my.picturesystembackend.model.vo.PictureAdminVO;
import com.my.picturesystembackend.model.vo.PictureVO;
import com.my.picturesystembackend.service.CategoryService;
import com.my.picturesystembackend.service.PictureService;
import com.my.picturesystembackend.service.TagService;
import com.my.picturesystembackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.my.picturesystembackend.constant.UploadPictureConstant.MAX_UPLOAD_PICTURE_COUNT;

/**
* @author helloworld
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-10-11 19:19:46
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    private final UserService userService;

    private final CategoryService categoryService;

    private final TagService tagService;

    private final FilePictureUpload filePictureUpload;

    private final UrlPictureUpload urlPictureUpload;

    private final PictureMapper pictureMapper;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void validPicture(Picture picture) {
        if (picture == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id不能为空，有参数时则校验
        ThrowUtils.throwIf(id == null || id <= 0L, ErrorCode.PARAMS_ERROR, "id不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 1024, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        if (inputSource == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片为空");
        }

        User loginUser = userService.getLoginUser(request);
        // 判断是新增还是更新
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新，则判断图片是否存在
        if (pictureId != null) {
            // 开放用户上传功能
            // 校验仅本人或管理员可编辑
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(oldPicture.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 上传图片，得到图片信息
        // 按照用户id划分目录
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        // 根据inputSource 类型区分上传方式
        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        // 把图片信息保存到数据库
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());

        // 补充审核参数
        this.fillReviewParams(picture, loginUser);

        // 操作数据库
        // 如果pictureId 不为空，表示更新，否则是新增
        if (pictureId != null) {
            // 如果是更新，则需要补充id和editTime
            picture.setId(pictureId);
            picture.setEditTime(LocalDateTime.now());
        }
        // 利用mybatis-plus的方法即可新增和更新
        boolean result = this.saveOrUpdate(picture);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片上传失败，数据库插入失败");
        }
        return this.getPictureVO(picture);
    }

    @Override
    public boolean deletePicture(DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断旧照片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员和图片拥有者可以删除
        if (!loginUser.getId().equals(oldPicture.getUserId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = this.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除图片失败");
        }
        return true;
    }

    @Override
    public boolean updatePicture(PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request) {
        // 本来是有一个picture_tag关联表，但是在写更新的时候有点难写
        // 因为标签可以随便修改，只有删除，重写添加这样的一个操作，不是去掉关联表，因为每次更新都需要去操作关联表，太麻烦了
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = BeanUtil.toBean(pictureUpdateRequest, Picture.class);
        picture.setTags(StrUtil.join(StrUtil.COMMA, pictureUpdateRequest.getTags()));
        // 校验参数
        validPicture(picture);
        // 判断是否存在
        long id = picture.getId();
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);

        // 补充审核参数
        User loginUser = userService.getLoginUser(request);
        this.fillReviewParams(picture, loginUser);

        // 第一次删除缓存
        this.clearPictureCache(id);

        // 更新数据库
        boolean update = this.updateById(picture);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新图片失败");
        }

        // 第二次删除缓存
        this.asyncDeleteCache(id);
        return true;
    }

    @Override
    public PictureAdminVO getPictureAdminVOById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0L, ErrorCode.PARAMS_ERROR);
        PictureAdminVO pictureAdminVO = pictureMapper.getPictureAdminVO(id);
        String tags = pictureAdminVO.getTags();
        if (StrUtil.isNotBlank(tags)) {
            List<String> split = StrUtil.split(tags, StrUtil.COMMA);
            if (CollUtil.isNotEmpty(split)) {
                List<Long> idList = split.stream().map(Long::parseLong).collect(Collectors.toList());
                List<Tag> tagList = tagService.listByIds(idList);
                pictureAdminVO.setTagVOList(tagService.getTagVOList(tagList));
            }
        }
        return pictureAdminVO;
    }

    @Override
    public PictureVO getPictureVOById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0L, ErrorCode.PARAMS_ERROR);
        Picture picture = this.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        return this.getPictureVO(picture);
    }

    @Override
    public PictureVO getPictureVO(Picture picture) {
        if (picture == null) {
            return null;
        }
        PictureVO pictureVO = BeanUtil.toBean(picture, PictureVO.class);
        // 关联查询用户信息，标签信息，分类信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0L) {
            User user = userService.getById(userId);
            pictureVO.setUser(userService.getUserVO(user));
        }
        Long categoryId = picture.getCategoryId();
        if (categoryId != null && categoryId > 0L) {
            Category category = categoryService.getById(categoryId);
            pictureVO.setCategory(categoryService.getCategoryVO(category));
        }
        String tags = picture.getTags();
        if (StrUtil.isNotBlank(tags)) {
            List<String> split = StrUtil.split(tags, StrUtil.COMMA);
            if (CollUtil.isNotEmpty(split)) {
                List<Long> ids = split.stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                List<Tag> tagList = tagService.listByIds(ids);
                pictureVO.setTagVOList(tagService.getTagVOList(tagList));
            }
        }
        return pictureVO;
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        Long categoryId = pictureQueryRequest.getCategoryId();
        List<Long> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();

        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.eq(ObjUtil.isNotNull(categoryId), "categoryId", categoryId);
        queryWrapper.eq(ObjUtil.isNotNull(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotNull(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotNull(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotNull(picScale), "picScale", picScale);
        queryWrapper.eq(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(ObjUtil.isNotNull(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotNull(reviewerId), "reviewerId", reviewerId);

        // 标签查询
        if (CollUtil.isNotEmpty(tags)) {
            for (Long tagId : tags) {
                queryWrapper.like("tags", tagId);
            }
        }

        // 排序
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        
        return queryWrapper;
    }

    @Override
    public Page<PictureAdminVO> listPictureAdminVOByPage(PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        Page<PictureAdminVO> page = new Page<>(current, size);
        Page<PictureAdminVO> pictureAdminVOPage = pictureMapper.listPictureAdminVOByPage(page, pictureQueryRequest);
        
        // 填充标签列表（优化：批量查询）
        List<PictureAdminVO> records = pictureAdminVOPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pictureAdminVOPage;
        }
        
        // 1. 收集所有需要查询的标签 ID（去重）
        List<Long> allTagIds = records.stream()
                .map(PictureAdminVO::getTags)
                .filter(StrUtil::isNotBlank)
                .flatMap(tags -> StrUtil.split(tags, StrUtil.COMMA).stream())
                .map(Long::parseLong)
                .distinct()
                .collect(Collectors.toList());
        
        // 2. 批量查询所有标签（只查一次数据库）
        if (CollUtil.isEmpty(allTagIds)) {
            return pictureAdminVOPage;
        }
        List<Tag> allTags = tagService.listByIds(allTagIds);
        
        // 3. 将标签按 ID 分组为 Map，方便快速查找
        Map<Long, Tag> tagMap = allTags.stream()
                .collect(Collectors.toMap(Tag::getId, tag -> tag));
        
        // 4. 为每条记录填充标签列表
        for (PictureAdminVO record : records) {
            String tags = record.getTags();
            if (StrUtil.isBlank(tags)) {
                continue;
            }
            
            List<Tag> recordTags = StrUtil.split(tags, StrUtil.COMMA).stream()
                    .map(Long::parseLong)
                    .map(tagMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            record.setTagVOList(tagService.getTagVOList(recordTags));
        }
        
        return pictureAdminVOPage;
    }

    @Override
    public Page<PictureVO> listPictureVOByPage(PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        // 补充只查出审核通过的图片
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());

        Page<PictureVO> page = new Page<>(current, size);
        Page<PictureVO> pictureVOPage = pictureMapper.listPictureVOByPage(page, pictureQueryRequest);
        
        // 填充标签列表（优化：批量查询）
        List<PictureVO> records = pictureVOPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pictureVOPage;
        }
        
        // 1. 收集所有需要查询的标签 ID（去重）
        List<Long> allTagIds = records.stream()
                .map(PictureVO::getTags)
                .filter(StrUtil::isNotBlank)
                .flatMap(tags -> StrUtil.split(tags, StrUtil.COMMA).stream())
                .map(Long::parseLong)
                .distinct()
                .collect(Collectors.toList());
        
        // 2. 批量查询所有标签（只查一次数据库）
        if (CollUtil.isEmpty(allTagIds)) {
            return pictureVOPage;
        }
        List<Tag> allTags = tagService.listByIds(allTagIds);
        
        // 3. 将标签按 ID 分组为 Map，方便快速查找
        Map<Long, Tag> tagMap = allTags.stream()
                .collect(Collectors.toMap(Tag::getId, tag -> tag));
        
        // 4. 为每条记录填充标签列表
        for (PictureVO record : records) {
            String tags = record.getTags();
            if (StrUtil.isBlank(tags)) {
                continue;
            }
            
            List<Tag> recordTags = StrUtil.split(tags, StrUtil.COMMA).stream()
                    .map(Long::parseLong)
                    .map(tagMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            record.setTagVOList(tagService.getTagVOList(recordTags));
        }
        
        return pictureVOPage;
    }

    @Override
    public Page<PictureVO> listPictureVOByPageWithCache(PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        // 补充只查出审核通过的图片
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());

        // 构建缓存key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String redisKey = PictureConstant.REDIS_KEY_PREFIX + hashKey;
        // 从缓存中查询
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        String cachedValue = valueOps.get(redisKey);
        if (StrUtil.isNotBlank(cachedValue)) {
            // 命中缓存，直接返回
            return JSONUtil.toBean(cachedValue, new TypeReference<Page<PictureVO>>() {}, false);
        }

        Page<PictureVO> page = new Page<>(current, size);
        Page<PictureVO> pictureVOPage = pictureMapper.listPictureVOByPage(page, pictureQueryRequest);

        // 填充标签列表（优化：批量查询）
        List<PictureVO> records = pictureVOPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pictureVOPage;
        }

        // 1. 收集所有需要查询的标签 ID（去重）
        List<Long> allTagIds = records.stream()
                .map(PictureVO::getTags)
                .filter(StrUtil::isNotBlank)
                .flatMap(tags -> StrUtil.split(tags, StrUtil.COMMA).stream())
                .map(Long::parseLong)
                .distinct()
                .collect(Collectors.toList());

        // 2. 批量查询所有标签（只查一次数据库）
        if (CollUtil.isEmpty(allTagIds)) {
            return pictureVOPage;
        }
        List<Tag> allTags = tagService.listByIds(allTagIds);

        // 3. 将标签按 ID 分组为 Map，方便快速查找
        Map<Long, Tag> tagMap = allTags.stream()
                .collect(Collectors.toMap(Tag::getId, tag -> tag));

        // 4. 为每条记录填充标签列表
        for (PictureVO record : records) {
            String tags = record.getTags();
            if (StrUtil.isBlank(tags)) {
                continue;
            }

            List<Tag> recordTags = StrUtil.split(tags, StrUtil.COMMA).stream()
                    .map(Long::parseLong)
                    .map(tagMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            record.setTagVOList(tagService.getTagVOList(recordTags));
        }

        // 存入缓存
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        // 5-10分钟随机过期，防止缓存雪崩
        int expireSeconds = RandomUtil.randomInt(5 * 60, 10 * 60);
        valueOps.set(redisKey, cacheValue, expireSeconds, TimeUnit.SECONDS);

        return pictureVOPage;
    }

    @Override
    public boolean editPicture(PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        if (pictureEditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = BeanUtil.toBean(pictureEditRequest, Picture.class);
        picture.setTags(StrUtil.join(StrUtil.COMMA, pictureEditRequest.getTags()));
        picture.setEditTime(LocalDateTime.now());
        // 校验参数
        validPicture(picture);
        User loginUser = userService.getLoginUser(request);
        Long id = picture.getId();
        Picture oldPicture = this.getById(id);
        if (oldPicture == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人和管理员可编辑
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 补充审核参数
        this.fillReviewParams(picture, loginUser);

        // 第一次删除缓存
        this.clearPictureCache(id);

        // 操作数据库
        boolean update = this.updateById(picture);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }

        // 第二次删除缓存
        this.asyncDeleteCache(id);
        return true;
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, HttpServletRequest request) {
        // 1. 校验参数
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if (id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取登录用户
        User loginUser = userService.getLoginUser(request);

        // 2. 判断图片是否存在
        Picture oldPicture = this.getById(id);
        if (oldPicture == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 校验审核状态是否已是该状态
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
        }
        // 4. 更新数据库
        Picture updatePicture = BeanUtil.toBean(pictureReviewRequest, Picture.class);
        updatePicture.setEditTime(LocalDateTime.now());
        updatePicture.setReviewerId(loginUser.getId());
        boolean update = this.updateById(updatePicture);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
    }

    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            // 管理员默认审核通过
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage(PictureConstant.ADMIN_AUTO_REVIEW_PASS);
            picture.setReviewTime(LocalDateTime.now());
        } else {
            // 非管理员，为待审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, HttpServletRequest request) {
        // 校验参数
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, ErrorCode.PARAMS_ERROR);

        String searchText = pictureUploadByBatchRequest.getSearchText();
        // 名称前缀，如果不填，则使用搜索词代替
        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        if (StrUtil.isBlank(namePrefix)) {
            namePrefix = searchText;
        }
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtils.throwIf(count > MAX_UPLOAD_PICTURE_COUNT || count < 0, ErrorCode.PARAMS_ERROR, "抓取数量最多30条，最少1条");

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 要抓取的地址
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        // 增加起始偏移量
        Integer first = pictureUploadByBatchRequest.getFirst();
        if (ObjUtil.isNotNull(first) && first > 0) {
            fetchUrl = String.format("%s&first=%s", fetchUrl, first);
        }

        // Jsoup 解析
        Document document = null;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("获取页面失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
        }
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isNull(div)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
        }
        Elements imgElementList = div.select("a.iusc");
        int uploadCount = 0;
        List<Picture> pictureList = new ArrayList<>();
        for (Element imgElement : imgElementList) {
            JSONObject data = JSONUtil.parseObj(imgElement.attr("m"));
            String fileUrl = data.getStr("murl");
            if (StrUtil.isBlank(fileUrl)) {
                log.info("当前链接为空，已跳过：{}", fileUrl);
                continue;
            }

            // 处理图片上传地址, 防止出现转义
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }

            try {
                // 上传图片，得到图片信息
                // 按照用户id划分目录
                String uploadPathPrefix = String.format("public/%s", loginUser.getId());
                // 根据inputSource 类型区分上传方式
                UploadPictureResult uploadPictureResult = urlPictureUpload.uploadPicture(fileUrl, uploadPathPrefix);
                // 把图片信息保存到数据库
                Picture picture = new Picture();
                picture.setUrl(uploadPictureResult.getUrl());
                picture.setName(uploadPictureResult.getPicName());
                if (StrUtil.isNotBlank(namePrefix)) {
                    picture.setName(namePrefix + (uploadCount + 1));
                }
                picture.setPicSize(uploadPictureResult.getPicSize());
                picture.setPicWidth(uploadPictureResult.getPicWidth());
                picture.setPicHeight(uploadPictureResult.getPicHeight());
                picture.setPicScale(uploadPictureResult.getPicScale());
                picture.setPicFormat(uploadPictureResult.getPicFormat());
                picture.setUserId(loginUser.getId());
                Long categoryId = pictureUploadByBatchRequest.getCategoryId();
                if (categoryId != null) {
                    picture.setCategoryId(categoryId);
                }
                List<Long> tags = pictureUploadByBatchRequest.getTags();
                if (CollUtil.isNotEmpty(tags)) {
                    picture.setTags(StrUtil.join(StrUtil.COMMA, tags));
                }

                // 补充审核参数
                this.fillReviewParams(picture, loginUser);

                pictureList.add(picture);

                uploadCount++;
            } catch (Exception e) {
                log.error("图片上传失败", e);
                continue;
            }
            if (uploadCount >= count) {
                break;
            }
        }

        boolean result = this.saveBatch(pictureList);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库插入失败");
        }
        return uploadCount;
    }

    @Override
    public void clearPictureCache(Long pictureId) {
        if (pictureId == null) {
            return;
        }

        // 清楚所有缓存
        Set<String> keys = stringRedisTemplate.keys(PictureConstant.REDIS_KEY_PREFIX + "*");
        if (CollUtil.isNotEmpty(keys)) {
            stringRedisTemplate.delete(keys);
            log.debug("Cleared cache for picture ID: {}", pictureId);
        }
    }

    @Override
    @Async
    public void asyncDeleteCache(Long pictureId) {
        try {
            // 延迟一段时间再次删除缓存，通常建议50-100ms
            Thread.sleep(100);
            clearPictureCache(pictureId);
            log.debug("Executed delayed cache deletion for picture ID: {}", pictureId);
        } catch (InterruptedException e) {
            log.error("Delayed cache deletion interrupted for picture ID: {}", pictureId, e);
            Thread.currentThread().interrupt();
        }
    }
}




