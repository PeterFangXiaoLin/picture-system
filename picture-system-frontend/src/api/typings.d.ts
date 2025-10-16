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

  type BaseResponseListCategoryVO_ = {
    code?: number
    data?: CategoryVO[]
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

  type deletePictureUsingPOSTParams = {
    /** id */
    id?: string
  }

  type DeleteRequest = {
    /** id */
    id?: string
  }

  type getCategoryByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getCategoryVOByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getPictureAdminVOByIdUsingGETParams = {
    /** id */
    id?: string
  }

  type getPictureVOByIdUsingGETParams = {
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

  type LoginUserVO = {
    /** 创建时间 */
    createTime?: string
    /** id */
    id?: string
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

  type PictureAdminVO = {
    category?: CategoryVO
    /** 分类 id */
    categoryId?: string
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
    /** 标签VO */
    tagVOList?: TagVO[]
    /** 标签ids */
    tags?: string
    /** 更新时间 */
    updateTime?: string
    /** 图片 url */
    url?: string
    user?: UserVO
    /** 用户 id */
    userId?: string
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
    tags?: string[]
  }

  type PictureQueryRequest = {
    /** 分类id */
    categoryId?: string
    /** 当前页号 */
    current?: number
    /** id */
    id?: string
    /** 简介 */
    introduction?: string
    /** 图片名称 */
    name?: string
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
    /** 搜索词（同时搜名称、简介等） */
    searchText?: string
    /** 排序字段 */
    sortField?: string
    /** 排序顺序（默认降序） */
    sortOrder?: string
    /** 标签 */
    tags?: string[]
    /** 用户 id */
    userId?: string
  }

  type PictureVO = {
    category?: CategoryVO
    /** 分类 id */
    categoryId?: string
    /** 创建时间 */
    createTime?: string
    /** 编辑时间 */
    editTime?: string
    /** id */
    id?: string
    /** 简介 */
    introduction?: string
    /** 图片名称 */
    name?: string
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
    /** 标签 */
    tagVOList?: TagVO[]
    /** 标签ids */
    tags?: string
    /** 更新时间 */
    updateTime?: string
    /** 图片 url */
    url?: string
    user?: UserVO
    /** 用户 id */
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
    /** 图片 id（用于修改） */
    id?: string
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
