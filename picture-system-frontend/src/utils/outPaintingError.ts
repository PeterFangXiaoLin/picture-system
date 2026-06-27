/** 扩图 API 错误码 -> 中文说明（阿里云百炼图像画面扩展 + 通用错误） */
const CODE_MESSAGES: Record<string, string> = {
  'InvalidParameter.JsonPhrase': '请求 JSON 格式错误，请检查参数后重试',
  'InvalidParameter.FileDownload': '输入图像下载失败，请确认图片链接可公网访问',
  'InvalidParameter.ImageFormat': '读取图像失败，请检查图片格式与文件是否损坏',
  'InvalidParameter.ImageContent': '图像内容未通过合规检测，请更换图片后重试',
  InvalidParameter: '扩图参数不符合要求，请检查各项取值范围',
  'InvalidParameter.DataInspection': '输出图像尺寸超限（大于 10MB），请开启限制图像大小或降低扩展幅度',
  'InternalError.Algo': '算法处理失败，请稍后重试',
  'InternalError.FileUpload': '结果文件上传失败，请稍后重试',
  Arrearage: '账号欠费或余额不足，请充值后重试',
  InvalidApiKey: 'API Key 无效或未配置，请联系管理员',
  'AccessDenied.Unpurchased': '未开通阿里云百炼服务，请先在控制台开通',
  'Model.AccessDenied': '无权限调用该模型，请检查账号授权',
  Throttling: '请求过于频繁，请稍后重试',
  'Throttling.RateQuota': '请求频率超限，请降低调用频率后重试',
  'Throttling.AllocationQuota': '调用配额已用尽，请稍后重试或提升配额',
  'DataInspectionFailed': '输入或输出内容可能包含敏感信息，请修改后重试',
  data_inspection_failed: '输入或输出内容可能包含敏感信息，请修改后重试',
  RequestTimeOut: '请求超时，请降低图片尺寸或稍后重试',
  internal_error: '服务内部错误，请稍后重试',
}

/** 英文 message 片段 -> 中文（按匹配优先级排列） */
const MESSAGE_PATTERNS: Array<{ pattern: RegExp; text: string }> = [
  { pattern: /input json error/i, text: '请求 JSON 格式错误' },
  { pattern: /download for input_image error|oss download error|download image failed/i, text: '输入图像下载失败，请确认图片可公网访问' },
  { pattern: /read image error/i, text: '读取图像失败，请检查图片格式与文件完整性' },
  { pattern: /green network verification|inappropriate content/i, text: '图像内容未通过合规检测' },
  { pattern: /parameters must conform to the specification/i, text: '扩图参数超出允许范围，请根据文档调整参数' },
  { pattern: /image size is not supported for the data inspection/i, text: '输出图像尺寸超限（大于 10MB）' },
  { pattern: /algorithm process error|inference internal error/i, text: '算法处理失败，请稍后重试' },
  { pattern: /oss upload error|failed to upload result/i, text: '结果上传失败，请稍后重试' },
  { pattern: /access denied.*good standing|arrearage/i, text: '账号欠费或余额不足，请充值后重试' },
  { pattern: /no api[- ]key provided|invalid api[- ]key/i, text: 'API Key 无效或未配置' },
  { pattern: /does not support synchronous calls/i, text: '接口需使用异步模式调用' },
  { pattern: /throttl|rate limit|too many requests/i, text: '请求过于频繁，请稍后重试' },
  { pattern: /timeout/i, text: '请求超时，请稍后重试' },
  { pattern: /the size of input image is too small or too large/i, text: '输入图像尺寸不符合要求（单边 512~4096 像素）' },
]

/**
 * 将扩图接口返回的英文错误转为用户可读的中文提示
 */
export function translateOutPaintingError(code?: string, message?: string): string {
  if (code && CODE_MESSAGES[code]) {
    return CODE_MESSAGES[code]
  }

  const rawMessage = message?.trim()
  if (rawMessage) {
    for (const { pattern, text } of MESSAGE_PATTERNS) {
      if (pattern.test(rawMessage)) {
        return text
      }
    }
  }

  if (code) {
    return `扩图失败（${code}）${rawMessage ? `：${rawMessage}` : ''}`
  }

  return rawMessage || '扩图失败，请稍后重试'
}
