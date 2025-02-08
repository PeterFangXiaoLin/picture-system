// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** addUser POST /api/user/add */
export async function addUserUsingPost(body: API.UserAddReqVO, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong_>('/api/user/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** deleteUser POST /api/user/delete */
export async function deleteUserUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/user/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** editUserInfo POST /api/user/edit */
export async function editUserInfoUsingPost(
  body: API.UserEditReqVO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/user/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getUserById GET /api/user/get */
export async function getUserByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUser_>('/api/user/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** getUserVOById GET /api/user/get/vo */
export async function getUserVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUserRespVO_>('/api/user/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** getLoginUser GET /api/user/getLoginUser */
export async function getLoginUserUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseUserRespVO_>('/api/user/getLoginUser', {
    method: 'GET',
    ...(options || {}),
  })
}

/** userLogin POST /api/user/login */
export async function userLoginUsingPost(
  body: API.UserLoginReqVO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUserRespVO_>('/api/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** userLogout POST /api/user/logout */
export async function userLogoutUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/user/logout', {
    method: 'POST',
    ...(options || {}),
  })
}

/** pageUserVO POST /api/user/page/vo */
export async function pageUserVoUsingPost(
  body: API.UserQueryReqVO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageUserRespVO_>('/api/user/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** userRegister POST /api/user/register */
export async function userRegisterUsingPost(
  body: API.UserRegisterReqVO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** resetPassword POST /api/user/resetPassword */
export async function resetPasswordUsingPost(
  body: API.UserResetPasswordReqVO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/user/resetPassword', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** updateUser POST /api/user/update */
export async function updateUserUsingPost(
  body: API.UserUpdateReqVO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/user/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
