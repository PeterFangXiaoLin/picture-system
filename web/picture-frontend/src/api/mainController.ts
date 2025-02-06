// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** health GET /api/health */
export async function healthUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/health', {
    method: 'GET',
    ...(options || {}),
  })
}

/** testRedirect GET /api/test */
export async function testRedirectUsingGet(options?: { [key: string]: any }) {
  return request<any>('/api/test', {
    method: 'GET',
    ...(options || {}),
  })
}
