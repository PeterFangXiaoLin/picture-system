import { saveAs } from 'file-saver'

/**
 * 格式化文件大小
 * @param size
 */
export const formatSize = (size?: number) => {
  if (!size) return '未知'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(2) + ' MB'
}

/**
 * 下载图片
 * @param url 图片下载地址
 * @param fileName 要保存为的文件名
 */
export function downloadImage(url?: string, fileName?: string) {
  if (!url) {
    return
  }
  saveAs(url, fileName)
}


/**
 * 将可能被压缩的 RGB 十六进制颜色值补全为标准 CSS 颜色格式 #RRGGBB。
 *
 * 部分云服务（如腾讯云数据万象主色提取）返回的 0x 颜色码会省略通道内的 00，
 * 例如 0xA10B2（5 位）应为 #a100b2，0x0A00（4 位）应为 #00a000。
 *
 * 算法按 R/G/B 三通道（各 2 位十六进制）依次补全：
 * - 长度为 3：仅可能为 000，补全为 #000000
 * - 长度为 6：已是完整 RGB，直接返回
 * - 长度为 4 或 5：逐通道解析——当前剩余串以 0 开头则该通道为 00 并消费 1 位，
 *   否则取前 2 位作为该通道；剩余为空时后续通道补 00
 *
 * @param input 颜色字符串，如 "0xA10B2"、"0x0A00"
 * @returns 标准 CSS 十六进制颜色，如 "#a100b2"
 */
export function toHexColor(input: string) {
  const colorValue = (input.startsWith('0x') ? input.slice(2) : input).toLowerCase()
  const len = colorValue.length

  if (len === 6) {
    return `#${colorValue}`
  }

  if (len === 3) {
    return '#000000'
  }

  if (len === 4 || len === 5) {
    let remaining = colorValue
    const channels: string[] = []

    for (let i = 0; i < 3; i++) {
      if (remaining.length === 0) {
        channels.push('00')
      } else if (remaining[0] === '0') {
        channels.push('00')
        remaining = remaining.slice(1)
      } else if (remaining.length >= 2) {
        channels.push(remaining.slice(0, 2))
        remaining = remaining.slice(2)
      } else {
        channels.push(remaining.padStart(2, '0'))
        remaining = ''
      }
    }

    return `#${channels.join('')}`
  }

  const numeric = parseInt(colorValue, 16)
  const hexColor = Number.isNaN(numeric)
    ? colorValue.padStart(6, '0').slice(-6)
    : numeric.toString(16).padStart(6, '0')
  return `#${hexColor}`
}
