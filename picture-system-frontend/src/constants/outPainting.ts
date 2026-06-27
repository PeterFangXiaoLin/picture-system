/** AI 扩图任务状态 */
export const OUT_PAINTING_TASK_STATUS_ENUM = {
  PENDING: 'PENDING',
  RUNNING: 'RUNNING',
  SUSPENDED: 'SUSPENDED',
  SUCCEEDED: 'SUCCEEDED',
  FAILED: 'FAILED',
  UNKNOWN: 'UNKNOWN',
} as const

export const OUT_PAINTING_TASK_STATUS_MAP: Record<string, string> = {
  PENDING: '排队中',
  RUNNING: '处理中',
  SUSPENDED: '挂起',
  SUCCEEDED: '成功',
  FAILED: '失败',
  UNKNOWN: '未知',
}

export const OUT_PAINTING_TASK_STATUS_COLOR: Record<string, string> = {
  PENDING: 'default',
  RUNNING: 'processing',
  SUSPENDED: 'warning',
  SUCCEEDED: 'success',
  FAILED: 'error',
  UNKNOWN: 'default',
}

export const OUT_PAINTING_TASK_STATUS_OPTIONS = Object.entries(OUT_PAINTING_TASK_STATUS_MAP).map(
  ([value, label]) => ({ value, label }),
)

export const isOutPaintingTaskFinished = (status?: string) => {
  return (
    status === OUT_PAINTING_TASK_STATUS_ENUM.SUCCEEDED ||
    status === OUT_PAINTING_TASK_STATUS_ENUM.FAILED ||
    status === OUT_PAINTING_TASK_STATUS_ENUM.UNKNOWN
  )
}
