declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseLong_ = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageUserRespVO_ = {
    code?: number
    data?: PageUserRespVO_
    message?: string
  }

  type BaseResponseString_ = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUser_ = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserRespVO_ = {
    code?: number
    data?: UserRespVO
    message?: string
  }

  type DeleteRequest = {
    id?: number
  }

  type getUserByIdUsingGETParams = {
    /** id */
    id: number
  }

  type getUserVOByIdUsingGETParams = {
    /** id */
    id: number
  }

  type OrderItem = {
    asc?: boolean
    column?: string
  }

  type PageUserRespVO_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: UserRespVO[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type User = {
    createTime?: string
    editTime?: string
    id?: number
    isDelete?: number
    updateTime?: string
    userAccount?: string
    userAvatar?: string
    userName?: string
    userPassword?: string
    userProfile?: string
    userRole?: string
  }

  type UserAddReqVO = {
    userAccount?: string
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserEditReqVO = {
    id?: number
    userAvatar?: string
    userName?: string
    userProfile?: string
  }

  type UserLoginReqVO = {
    userAccount?: string
    userPassword?: string
  }

  type UserQueryReqVO = {
    current?: number
    id?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    userAccount?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterReqVO = {
    checkPassword?: string
    userAccount?: string
    userPassword?: string
  }

  type UserResetPasswordReqVO = {
    id?: number
    newPassword?: string
    oldPassword?: string
  }

  type UserRespVO = {
    createTime?: string
    id?: number
    updateTime?: string
    userAccount?: string
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserUpdateReqVO = {
    id?: number
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }
}
