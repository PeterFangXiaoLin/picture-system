// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 查询我的通知 GET /api/notification/list/my */
export async function listMyNotificationUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListUserNotification_>('/api/notification/list/my', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 查询未读通知数 GET /api/notification/unread/count */
export async function getUnreadNotificationCountUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseLong_>('/api/notification/unread/count', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 标记一条通知为已读 POST /api/notification/read */
export async function markNotificationReadUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/notification/read', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    data: body,
    ...(options || {}),
  })
}

/** 一键全部已读 POST /api/notification/read/all */
export async function markAllNotificationReadUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/notification/read/all', {
    method: 'POST',
    ...(options || {}),
  })
}
