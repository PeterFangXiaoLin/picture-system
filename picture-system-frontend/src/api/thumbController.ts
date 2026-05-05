// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 点赞 POST /api/thumb/do */
export async function doThumbUsingPost(body: API.DoThumbRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/thumb/do', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 取消点赞 POST /api/thumb/undo */
export async function undoThumbUsingPost(
  body: API.DoThumbRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/thumb/undo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
