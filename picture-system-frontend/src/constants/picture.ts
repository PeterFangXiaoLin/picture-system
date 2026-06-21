export const PIC_REVIEW_STATUS_ENUM = {
  REVIEWING: 0,
  PASS: 1,
  REJECT: 2,
}

export const PIC_REVIEW_STATUS_MAP = {
  0: '待审核',
  1: '通过',
  2: '拒绝',
}

export const PIC_REVIEW_STATUS_OPTIONS = Object.keys(PIC_REVIEW_STATUS_MAP).map((key) => {
  return {
    label: PIC_REVIEW_STATUS_MAP[key],
    value: key,
  }
})

/** 常见图片格式（与 COS 图片信息返回的 format 字段一致） */
export const PIC_FORMAT_ENUM = {
  JPG: 'jpg',
  JPEG: 'jpeg',
  PNG: 'png',
  GIF: 'gif',
  WEBP: 'webp',
  BMP: 'bmp',
  SVG: 'svg',
  TIFF: 'tiff',
  ICO: 'ico',
} as const

export const PIC_FORMAT_MAP: Record<string, string> = {
  [PIC_FORMAT_ENUM.JPG]: 'JPG',
  [PIC_FORMAT_ENUM.JPEG]: 'JPEG',
  [PIC_FORMAT_ENUM.PNG]: 'PNG',
  [PIC_FORMAT_ENUM.GIF]: 'GIF',
  [PIC_FORMAT_ENUM.WEBP]: 'WEBP',
  [PIC_FORMAT_ENUM.BMP]: 'BMP',
  [PIC_FORMAT_ENUM.SVG]: 'SVG',
  [PIC_FORMAT_ENUM.TIFF]: 'TIFF',
  [PIC_FORMAT_ENUM.ICO]: 'ICO',
}

export const PIC_FORMAT_OPTIONS = Object.entries(PIC_FORMAT_MAP).map(([value, label]) => ({
  value,
  label,
}))
