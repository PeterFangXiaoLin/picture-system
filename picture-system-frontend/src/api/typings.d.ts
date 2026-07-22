declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseCategory_ = {
    code?: number
    data?: Category
    message?: string
  }

  type BaseResponseCategoryVO_ = {
    code?: number
    data?: CategoryVO
    message?: string
  }

  type BaseResponseGetOutPaintingTaskResponse_ = {
    code?: number
    data?: GetOutPaintingTaskResponse
    message?: string
  }

  type BaseResponseInt_ = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponseListCategoryVO_ = {
    code?: number
    data?: CategoryVO[]
    message?: string
  }

  type BaseResponseListImageSearchResult_ = {
    code?: number
    data?: ImageSearchResult[]
    message?: string
  }

  type BaseResponseListPictureVO_ = {
    code?: number
    data?: PictureVO[]
    message?: string
  }

  type BaseResponseListSpace_ = {
    code?: number
    data?: Space[]
    message?: string
  }

  type BaseResponseListSpaceCategoryAnalyzeResponse_ = {
    code?: number
    data?: SpaceCategoryAnalyzeResponse[]
    message?: string
  }

  type BaseResponseListSpaceLevel_ = {
    code?: number
    data?: SpaceLevel[]
    message?: string
  }

  type BaseResponseListSpaceSizeAnalyzeResponse_ = {
    code?: number
    data?: SpaceSizeAnalyzeResponse[]
    message?: string
  }

  type BaseResponseListSpaceTagAnalyzeResponse_ = {
    code?: number
    data?: SpaceTagAnalyzeResponse[]
    message?: string
  }

  type BaseResponseListSpaceUserAnalyzeResponse_ = {
    code?: number
    data?: SpaceUserAnalyzeResponse[]
    message?: string
  }

  type BaseResponseListSpaceUserVO_ = {
    code?: number
    data?: SpaceUserVO[]
    message?: string
  }

  type BaseResponseListUserNotification_ = {
    code?: number
    data?: UserNotification[]
    message?: string
  }

  type BaseResponseListTagVO_ = {
    code?: number
    data?: TagVO[]
    message?: string
  }

  type BaseResponseLoginUserVO_ = {
    code?: number
    data?: LoginUserVO
    message?: string
  }

  type BaseResponseLong_ = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponseOutPaintingQuotaVO_ = {
    code?: number
    data?: OutPaintingQuotaVO
    message?: string
  }

  type BaseResponseOutPaintingTaskStatisticsVO_ = {
    code?: number
    data?: OutPaintingTaskStatisticsVO
    message?: string
  }

  type BaseResponseOutPaintingTaskVO_ = {
    code?: number
    data?: OutPaintingTaskVO
    message?: string
  }

  type BaseResponsePageCategory_ = {
    code?: number
    data?: PageCategory_
    message?: string
  }

  type BaseResponsePageCategoryVO_ = {
    code?: number
    data?: PageCategoryVO_
    message?: string
  }

  type BaseResponsePageOutPaintingTaskVO_ = {
    code?: number
    data?: PageOutPaintingTaskVO_
    message?: string
  }

  type BaseResponsePagePictureAdminVO_ = {
    code?: number
    data?: PagePictureAdminVO_
    message?: string
  }

  type BaseResponsePagePictureVO_ = {
    code?: number
    data?: PagePictureVO_
    message?: string
  }

  type BaseResponsePageSpace_ = {
    code?: number
    data?: PageSpace_
    message?: string
  }

  type BaseResponsePageSpaceVO_ = {
    code?: number
    data?: PageSpaceVO_
    message?: string
  }

  type BaseResponsePageTag_ = {
    code?: number
    data?: PageTag_
    message?: string
  }

  type BaseResponsePageTagVO_ = {
    code?: number
    data?: PageTagVO_
    message?: string
  }

  type BaseResponsePageUserVO_ = {
    code?: number
    data?: PageUserVO_
    message?: string
  }

  type BaseResponsePictureAdminVO_ = {
    code?: number
    data?: PictureAdminVO
    message?: string
  }

  type BaseResponsePictureVO_ = {
    code?: number
    data?: PictureVO
    message?: string
  }

  type BaseResponseSpace_ = {
    code?: number
    data?: Space
    message?: string
  }

  type BaseResponseSpaceUsageAnalyzeResponse_ = {
    code?: number
    data?: SpaceUsageAnalyzeResponse
    message?: string
  }

  type BaseResponseSpaceUser_ = {
    code?: number
    data?: SpaceUser
    message?: string
  }

  type BaseResponseSpaceVO_ = {
    code?: number
    data?: SpaceVO
    message?: string
  }

  type BaseResponseString_ = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseTag_ = {
    code?: number
    data?: Tag
    message?: string
  }

  type BaseResponseTagVO_ = {
    code?: number
    data?: TagVO
    message?: string
  }

  type BaseResponseUser_ = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO_ = {
    code?: number
    data?: UserVO
    message?: string
  }

  type Category = {
    createTime?: string
    editTime?: string
    id?: string
    isDelete?: number
    name?: string
    updateTime?: string
    userId?: string
  }

  type CategoryAddRequest = {
    /** 分类名称 */
    name?: string
  }

  type CategoryQueryRequest = {
    /** 当前页号 */
    current?: number
    /** id */
    id?: string
    /** 标签名称 */
    name?: string
    /** 页面大小 */
    pageSize?: number
    /** 排序字段 */
    sortField?: string
    /** 排序顺序（默认降序） */
    sortOrder?: string
    /** 创建用户 id */
    userId?: string
  }

  type CategoryUpdateRequest = {
    /** id */
    id?: string
    /** 分类名称 */
    name?: string
  }

  type CategoryVO = {
    /** 创建时间 */
    createTime?: string
    /** id */
    id?: string
    /** 分类名称 */
    name?: string
    /** 更新时间 */
    updateTime?: string
    userId?: string
    userVO?: UserVO
  }

  type CreatePictureOutPaintingTaskRequest = {
    parameters?: Parameters
    pictureId?: string
  }

  type DeleteRequest = {
    /** id */
    id?: string
  }

  type DoThumbRequest = {
    /** 图片id */
    pictureId?: string
  }

  type getCategoryByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getCategoryVOByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getOutPaintingTaskByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type GetOutPaintingTaskResponse = {
    output?: Output
    requestId?: string
  }

  type getPictureAdminVOByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getPictureOutPaintingTaskUsingGETParams = {
    /** taskId */
    taskId?: string
  }

  type getPictureVOByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getSpaceByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getSpaceVOByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getTagByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getTagVOByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type ImageSearchResult = {
    fromUrl?: string
    thumbUrl?: string
  }

  type LoginUserVO = {
    /** 创建时间 */
    createTime?: string
    /** id */
    id?: string
    /** AI 扩图剩余次数 */
    outPaintingQuota?: number
    /** 更新时间 */
    updateTime?: string
    /** 账号 */
    userAccount?: string
    /** 用户头像 */
    userAvatar?: string
    /** 用户昵称 */
    userName?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色 */
    userRole?: string
  }

  type OutPaintingQuotaVO = {
    /** 剩余次数 */
    remaining?: number
    /** 是否不限次数（管理员） */
    unlimited?: boolean
  }

  type OutPaintingTaskQueryRequest = {
    /** 当前页号 */
    current?: number
    /** 结束创建时间 */
    endCreateTime?: string
    /** 任务记录 id */
    id?: string
    /** 页面大小 */
    pageSize?: number
    /** 原图片 id */
    pictureId?: string
    /** 排序字段 */
    sortField?: string
    /** 排序顺序（默认降序） */
    sortOrder?: string
    /** 开始创建时间 */
    startCreateTime?: string
    /** 阿里云任务 id */
    taskId?: string
    /** 任务状态 */
    taskStatus?: string
    /** 用户 id */
    userId?: string
  }

  type OutPaintingTaskStatisticsVO = {
    /** 失败任务数 */
    failedCount?: number
    /** 失败率（百分比，保留两位小数） */
    failureRate?: number
    /** 处理中任务数 */
    processingCount?: number
    /** 成功任务数 */
    successCount?: number
    /** 成功率（百分比，保留两位小数） */
    successRate?: number
    /** 任务总数 */
    totalCount?: number
  }

  type OutPaintingTaskVO = {
    /** 创建时间 */
    createTime?: string
    /** 错误码 */
    errorCode?: string
    /** 错误信息 */
    errorMessage?: string
    /** 任务记录 id */
    id?: string
    /** 原图 url */
    originalImageUrl?: string
    /** 输出图像 url */
    outputImageUrl?: string
    parameters?: Parameters
    /** 重试时关联的原任务记录 id */
    parentTaskId?: string
    /** 原图片 id */
    pictureId?: string
    /** 原图片名称 */
    pictureName?: string
    /** 阿里云任务 id */
    taskId?: string
    /** 任务状态 */
    taskStatus?: string
    /** 更新时间 */
    updateTime?: string
    /** 创建用户 id */
    userId?: string
  }

  type Output = {
    code?: string
    endTime?: string
    message?: string
    outputImageUrl?: string
    scheduledTime?: string
    submitTime?: string
    taskId?: string
    taskMetrics?: TaskMetrics
    taskStatus?: string
  }

  type PageCategory_ = {
    current?: number
    pages?: number
    records?: Category[]
    size?: number
    total?: number
  }

  type PageCategoryVO_ = {
    current?: number
    pages?: number
    records?: CategoryVO[]
    size?: number
    total?: number
  }

  type PageOutPaintingTaskVO_ = {
    current?: number
    pages?: number
    records?: OutPaintingTaskVO[]
    size?: number
    total?: number
  }

  type PagePictureAdminVO_ = {
    current?: number
    pages?: number
    records?: PictureAdminVO[]
    size?: number
    total?: number
  }

  type PagePictureVO_ = {
    current?: number
    pages?: number
    records?: PictureVO[]
    size?: number
    total?: number
  }

  type PageSpace_ = {
    current?: number
    pages?: number
    records?: Space[]
    size?: number
    total?: number
  }

  type PageSpaceVO_ = {
    current?: number
    pages?: number
    records?: SpaceVO[]
    size?: number
    total?: number
  }

  type PageTag_ = {
    current?: number
    pages?: number
    records?: Tag[]
    size?: number
    total?: number
  }

  type PageTagVO_ = {
    current?: number
    pages?: number
    records?: TagVO[]
    size?: number
    total?: number
  }

  type PageUserVO_ = {
    current?: number
    pages?: number
    records?: UserVO[]
    size?: number
    total?: number
  }

  type Parameters = {
    addWatermark?: boolean
    angle?: number
    bestQuality?: boolean
    bottomOffset?: number
    leftOffset?: number
    limitImageSize?: boolean
    outputRatio?: string
    rightOffset?: number
    topOffset?: number
    xScale?: number
    yScale?: number
  }

  type PictureAdminVO = {
    category?: CategoryVO
    /** 分类 id */
    categoryId?: string
    /** 压缩图 url */
    compressedUrl?: string
    /** 创建时间 */
    createTime?: string
    /** 编辑时间 */
    editTime?: string
    /** id */
    id?: string
    /** 简介 */
    introduction?: string
    /** 是否删除 */
    isDelete?: number
    /** 图片名称 */
    name?: string
    /** 图片主色调 */
    picColor?: string
    /** 图片格式 */
    picFormat?: string
    /** 图片高度 */
    picHeight?: number
    /** 图片比例 */
    picScale?: number
    /** 文件体积 */
    picSize?: number
    /** 图片宽度 */
    picWidth?: number
    /** 审核信息 */
    reviewMessage?: string
    /** 状态：0-待审核; 1-通过; 2-拒绝 */
    reviewStatus?: number
    /** 审核时间 */
    reviewTime?: string
    /** 审核人 id */
    reviewerId?: string
    /** space id */
    spaceId?: string
    /** 标签VO */
    tagVOList?: TagVO[]
    /** 标签ids */
    tags?: string
    /** 点赞数量 */
    thumbCount?: number
    /** 更新时间 */
    updateTime?: string
    /** 图片 url */
    url?: string
    user?: UserVO
    /** 用户 id */
    userId?: string
  }

  type PictureEditByBatchRequest = {
    /** 分类 */
    categoryId?: string
    /** 命名规则 */
    nameRule?: string
    /** 图片id列表 */
    pictureIdList?: number[]
    /** 空间id */
    spaceId?: string
    /** 标签 */
    tags?: number[]
  }

  type PictureEditRequest = {
    /** 分类id */
    categoryId?: string
    /** id */
    id?: string
    /** 简介 */
    introduction?: string
    /** 图片名称 */
    name?: string
    /** 标签 */
    tags?: number[]
  }

  type PictureQueryRequest = {
    /** 分类id */
    categoryId?: string
    /** 当前页号 */
    current?: number
    /** 结束编辑时间 */
    endEditTime?: string
    /** id */
    id?: string
    /** 简介 */
    introduction?: string
    /** 图片名称 */
    name?: string
    /** 是否只查询 spaceId 为 null 的图片 */
    nullSpaceId?: boolean
    /** 页面大小 */
    pageSize?: number
    /** 图片格式 */
    picFormat?: string
    /** 图片高度 */
    picHeight?: number
    /** 图片比例 */
    picScale?: number
    /** 文件体积 */
    picSize?: number
    /** 图片宽度 */
    picWidth?: number
    /** 审核信息 */
    reviewMessage?: string
    /** 状态：0-待审核; 1-通过; 2-拒绝 */
    reviewStatus?: number
    /** 审核人 id */
    reviewerId?: string
    /** 搜索词（同时搜名称、简介等） */
    searchText?: string
    /** 排序字段 */
    sortField?: string
    /** 排序顺序（默认降序） */
    sortOrder?: string
    /** 空间 id */
    spaceId?: string
    /** 开始编辑时间 */
    startEditTime?: string
    /** 标签 */
    tags?: number[]
    /** 用户 id */
    userId?: string
  }

  type PictureReviewRequest = {
    /** id */
    id?: string
    /** 审核信息 */
    reviewMessage?: string
    /** 状态：0-待审核, 1-通过, 2-拒绝 */
    reviewStatus?: number
  }

  type PictureUploadByBatchRequest = {
    /** 分类id */
    categoryId?: string
    /** 抓取数量 */
    count?: number
    /** 起始偏移量 */
    first?: number
    /** 名称前缀 */
    namePrefix?: string
    /** 搜索词 */
    searchText?: string
    /** 标签 */
    tags?: number[]
  }

  type PictureUploadRequest = {
    /** 文件地址 */
    fileUrl?: string
    /** 图片 id（用于修改） */
    id?: string
    /** 空间id */
    spaceId?: string
  }

  type PictureVO = {
    category?: CategoryVO
    /** 分类 id */
    categoryId?: string
    /** 压缩图 url */
    compressedUrl?: string
    /** 创建时间 */
    createTime?: string
    /** 编辑时间 */
    editTime?: string
    /** 是否已点赞 */
    hasThumb?: boolean
    /** id */
    id?: string
    /** 简介 */
    introduction?: string
    /** 图片名称 */
    name?: string
    /** 权限列表 */
    permissionList?: string[]
    /** 图片主色调 */
    picColor?: string
    /** 图片格式 */
    picFormat?: string
    /** 图片高度 */
    picHeight?: number
    /** 图片比例 */
    picScale?: number
    /** 文件体积 */
    picSize?: number
    /** 图片宽度 */
    picWidth?: number
    /** space id */
    spaceId?: string
    /** 标签 */
    tagVOList?: TagVO[]
    /** 标签ids */
    tags?: string
    /** 点赞数量 */
    thumbCount?: number
    /** 更新时间 */
    updateTime?: string
    /** 图片 url */
    url?: string
    user?: UserVO
    /** 用户 id */
    userId?: string
  }

  type RetryOutPaintingTaskRequest = {
    /** 任务记录 id */
    id: string
  }

  type SearchPictureByColorRequest = {
    /** 图片主色调 */
    picColor?: string
    /** 空间id */
    spaceId?: string
  }

  type SearchPictureByPictureRequest = {
    /** 图片id */
    pictureId?: string
  }

  type Space = {
    createTime?: string
    editTime?: string
    id?: string
    isDelete?: number
    maxCount?: number
    maxSize?: number
    spaceLevel?: number
    spaceName?: string
    spaceType?: number
    totalCount?: number
    totalSize?: number
    updateTime?: string
    userId?: string
  }

  type SpaceAddRequest = {
    /** 空间级别：0-普通版 1-专业版 2-旗舰版 */
    spaceLevel?: number
    /** 空间名称 */
    spaceName?: string
    /** 空间类型：0-私有空间，1-团队空间 */
    spaceType?: number
  }

  type SpaceCategoryAnalyzeRequest = {
    /** 全空间分析 */
    queryAll?: boolean
    /** 是否查询公共图库 */
    queryPublic?: boolean
    /** 空间 ID */
    spaceId?: string
  }

  type SpaceCategoryAnalyzeResponse = {
    /** 图片分类 */
    categoryId?: string
    /** 图片分类名称 */
    categoryName?: string
    /** 图片数量 */
    count?: number
    /** 分类图片总大小 */
    totalSize?: number
  }

  type SpaceEditRequest = {
    /** 空间 id */
    id?: string
    /** 空间名称 */
    spaceName?: string
  }

  type SpaceLevel = {
    /** 空间最大数量 */
    maxCount?: number
    /** 空间最大大小 */
    maxSize?: number
    /** 文本 */
    text?: string
    /** 值 */
    value?: number
  }

  type SpaceQueryRequest = {
    /** 当前页号 */
    current?: number
    /** id */
    id?: string
    /** 页面大小 */
    pageSize?: number
    /** 排序字段 */
    sortField?: string
    /** 排序顺序（默认降序） */
    sortOrder?: string
    /** 空间级别：0-普通版 1-专业版 2-旗舰版 */
    spaceLevel?: number
    /** 空间名称 */
    spaceName?: string
    /** 空间类型：0-私有空间，1-团队空间 */
    spaceType?: number
    /** 用户 id */
    userId?: string
  }

  type SpaceRankAnalyzeRequest = {
    /** 排名前 N 的空间 */
    topN?: number
  }

  type SpaceSizeAnalyzeRequest = {
    /** 全空间分析 */
    queryAll?: boolean
    /** 是否查询公共图库 */
    queryPublic?: boolean
    /** 空间 ID */
    spaceId?: string
  }

  type SpaceSizeAnalyzeResponse = {
    /** 图片数量 */
    count?: number
    /** 图片大小范围 */
    sizeRange?: string
  }

  type SpaceTagAnalyzeRequest = {
    /** 全空间分析 */
    queryAll?: boolean
    /** 是否查询公共图库 */
    queryPublic?: boolean
    /** 空间 ID */
    spaceId?: string
  }

  type SpaceTagAnalyzeResponse = {
    /** 使用次数 */
    count?: number
    /** 标签id */
    tagId?: string
    /** 标签名称 */
    tagName?: string
  }

  type SpaceUpdateRequest = {
    /** id */
    id?: string
    /** 空间图片的最大数量 */
    maxCount?: number
    /** 空间图片的最大总大小 */
    maxSize?: number
    /** 空间级别：0-普通版 1-专业版 2-旗舰版 */
    spaceLevel?: number
    /** 空间名称 */
    spaceName?: string
  }

  type SpaceUsageAnalyzeRequest = {
    /** 全空间分析 */
    queryAll?: boolean
    /** 是否查询公共图库 */
    queryPublic?: boolean
    /** 空间 ID */
    spaceId?: string
  }

  type SpaceUsageAnalyzeResponse = {
    /** 图片数量占比 */
    countUsageRatio?: number
    /** 最大图片数量 */
    maxCount?: number
    /** 总大小 */
    maxSize?: number
    /** 空间使用比例 */
    sizeUsageRatio?: number
    /** 当前图片数量 */
    usedCount?: number
    /** 已使用大小 */
    usedSize?: number
  }

  type SpaceUser = {
    createTime?: string
    createUserId?: string
    id?: string
    inviteStatus?: number
    spaceId?: string
    spaceRole?: string
    updateTime?: string
    userId?: string
  }

  type SpaceUserAddRequest = {
    /** 空间 id */
    spaceId?: string
    /** 空间角色：viewer/editor/admin */
    spaceRole?: string
    /** 用户id */
    userId?: string
  }

  type SpaceUserAnalyzeRequest = {
    /** 全空间分析 */
    queryAll?: boolean
    /** 是否查询公共图库 */
    queryPublic?: boolean
    /** 空间 ID */
    spaceId?: string
    /** 时间维度 */
    timeDimension?: string
    /** 用户id */
    userId?: string
  }

  type SpaceUserAnalyzeResponse = {
    /** 上传数量 */
    count?: number
    /** 时间区间 */
    period?: string
  }

  type SpaceUserEditRequest = {
    /** id */
    id?: string
    /** 空间角色：viewer/editor/admin */
    spaceRole?: string
  }

  type SpaceUserInviteReviewRequest = {
    /** 空间成员记录 id */
    id: string
    /** 邀请确认状态：1-接受；2-拒绝 */
    inviteStatus: number
  }

  type SpaceUserQueryRequest = {
    /** 邀请人 id */
    createUserId?: string
    /** id */
    id?: string
    /** 邀请确认状态：0-待确认；1-已接受；2-已拒绝 */
    inviteStatus?: number
    /** 空间 id */
    spaceId?: string
    /** 空间角色：viewer/editor/admin */
    spaceRole?: string
    /** 用户id */
    userId?: string
  }

  type SpaceUserVO = {
    /** 创建时间 */
    createTime?: string
    /** 邀请人 id */
    createUserId?: string
    /** id */
    id?: string
    /** 邀请确认状态：0-待确认；1-已接受；2-已拒绝 */
    inviteStatus?: number
    space?: SpaceVO
    /** 空间 id */
    spaceId?: string
    /** 空间角色：viewer/editor/admin */
    spaceRole?: string
    /** 更新时间 */
    updateTime?: string
    user?: UserVO
    /** 用户id */
    userId?: string
  }

  type UserNotification = {
    /** 可操作通知业务状态：0-待确认，1-已接受，2-已拒绝 */
    actionStatus?: number
    actorUserId?: string
    content?: string
    createTime?: string
    id?: string
    /** 是否已读：0-未读；1-已读 */
    isRead?: number
    /** 空间成员记录 id */
    relatedId?: string
    spaceId?: string
    title?: string
    type?: 'SPACE_INVITE' | 'SPACE_INVITE_ACCEPTED' | 'SPACE_INVITE_REJECTED' | string
    updateTime?: string
    userId?: string
  }

  type SpaceVO = {
    /** 创建时间 */
    createTime?: string
    /** 编辑时间 */
    editTime?: string
    /** id */
    id?: string
    /** 空间图片的最大数量 */
    maxCount?: number
    /** 空间图片的最大总大小 */
    maxSize?: number
    /** 权限列表 */
    permissionList?: string[]
    /** 空间级别：0-普通版 1-专业版 2-旗舰版 */
    spaceLevel?: number
    /** 空间名称 */
    spaceName?: string
    /** 空间类型：0-私有空间，1-团队空间 */
    spaceType?: number
    /** 当前空间下的图片数量 */
    totalCount?: number
    /** 当前空间下图片的总大小 */
    totalSize?: number
    /** 更新时间 */
    updateTime?: string
    user?: UserVO
    /** 创建用户 id */
    userId?: string
  }

  type Tag = {
    createTime?: string
    editTime?: string
    id?: string
    isDelete?: number
    name?: string
    updateTime?: string
    userId?: string
  }

  type TagAddRequest = {
    /** 标签名称 */
    name?: string
  }

  type TagQueryRequest = {
    /** 当前页号 */
    current?: number
    /** id */
    id?: string
    /** 标签名称 */
    name?: string
    /** 页面大小 */
    pageSize?: number
    /** 排序字段 */
    sortField?: string
    /** 排序顺序（默认降序） */
    sortOrder?: string
    /** 创建用户 id */
    userId?: string
  }

  type TagUpdateRequest = {
    /** id */
    id?: string
    /** 标签名称 */
    name?: string
  }

  type TagVO = {
    /** 创建时间 */
    createTime?: string
    /** 编辑时间 */
    editTime?: string
    /** id */
    id?: string
    /** 标签名称 */
    name?: string
    /** 更新时间 */
    updateTime?: string
    userId?: string
    userVO?: UserVO
  }

  type TaskMetrics = {
    failed?: number
    succeeded?: number
    total?: number
  }

  type testDownloadFileUsingGETParams = {
    /** filePath */
    filePath?: string
  }

  type updatePictureUsingPOSTParams = {
    /** 分类id */
    categoryId?: string
    /** id */
    id?: string
    /** 简介 */
    introduction?: string
    /** 图片名称 */
    name?: string
    /** 标签id */
    tags?: number[]
  }

  type uploadPictureUsingPOSTParams = {
    /** 文件地址 */
    fileUrl?: string
    /** 图片 id（用于修改） */
    id?: string
    /** 空间id */
    spaceId?: string
  }

  type User = {
    /** 创建时间 */
    createTime?: string
    /** 编辑时间 */
    editTime?: string
    /** id */
    id?: string
    /** 是否删除 */
    isDelete?: number
    /** AI 扩图剩余次数 */
    outPaintingQuota?: number
    /** 更新时间 */
    updateTime?: string
    /** 账号 */
    userAccount?: string
    /** 用户头像 */
    userAvatar?: string
    /** 用户昵称 */
    userName?: string
    /** 密码 */
    userPassword?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色：user/admin */
    userRole?: string
  }

  type UserAddRequest = {
    /** 账号 */
    userAccount?: string
    /** 用户头像 */
    userAvatar?: string
    /** 用户昵称 */
    userName?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色: user, admin */
    userRole?: string
  }

  type UserLoginRequest = {
    /** 用户账号 */
    userAccount?: string
    /** 用户密码 */
    userPassword?: string
  }

  type UserQueryRequest = {
    /** 当前页号 */
    current?: number
    /** id */
    id?: string
    /** 页面大小 */
    pageSize?: number
    /** 排序字段 */
    sortField?: string
    /** 排序顺序（默认降序） */
    sortOrder?: string
    /** 账号 */
    userAccount?: string
    /** 用户昵称 */
    userName?: string
    /** 简介 */
    userProfile?: string
    /** 用户角色：user/admin/ban */
    userRole?: string
  }

  type UserRegisterRequest = {
    /** 确认密码 */
    checkPassword?: string
    /** 用户账号 */
    userAccount?: string
    /** 用户密码 */
    userPassword?: string
  }

  type UserUpdateRequest = {
    /** id */
    id?: string
    /** AI 扩图剩余次数 */
    outPaintingQuota?: number
    /** 用户头像 */
    userAvatar?: string
    /** 用户昵称 */
    userName?: string
    /** 简介 */
    userProfile?: string
    /** 用户角色：user/admin */
    userRole?: string
  }

  type UserVO = {
    /** 创建时间 */
    createTime?: string
    /** id */
    id?: string
    /** AI 扩图剩余次数 */
    outPaintingQuota?: number
    /** 账号 */
    userAccount?: string
    /** 用户头像 */
    userAvatar?: string
    /** 用户昵称 */
    userName?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色：user/admin */
    userRole?: string
  }
}
