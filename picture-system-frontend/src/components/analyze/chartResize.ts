import type { Ref } from 'vue'
import { onBeforeUnmount, onMounted } from 'vue'
import type VChart from 'vue-echarts'

export type VChartInstance = InstanceType<typeof VChart>

export const debounce = <T extends (...args: never[]) => void>(fn: T, delay = 200) => {
  let timer: ReturnType<typeof setTimeout> | undefined

  return (...args: Parameters<T>) => {
    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => fn(...args), delay)
  }
}

export const useChartResize = (chartRef: Ref<VChartInstance | null>, delay = 200) => {
  const resizeChart = debounce(() => {
    chartRef.value?.resize()
  }, delay)

  onMounted(() => {
    window.addEventListener('resize', resizeChart)
  })

  onBeforeUnmount(() => {
    window.removeEventListener('resize', resizeChart)
  })

  return resizeChart
}
