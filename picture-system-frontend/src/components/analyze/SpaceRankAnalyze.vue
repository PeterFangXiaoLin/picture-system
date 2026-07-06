<template>
  <div class="space-rank-analyze">
    <a-card title="空间使用排行分析">
      <v-chart
        ref="chartRef"
        :option="options"
        style="height: 320px; max-width: 100%"
        :loading="loading"
      />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import VChart from 'vue-echarts'
import 'echarts'
import { computed, ref, watchEffect } from 'vue'
import { getSpaceRankAnalyzeUsingPost } from '@/api/spaceAnalyzeController.ts'
import { message } from 'ant-design-vue'
import { useChartResize, type VChartInstance } from './chartResize'

interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: string
}

withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

// 图表数据
const chartRef = ref<VChartInstance | null>(null)
const dataList = ref<API.Space[]>([])
const loading = ref(true)
useChartResize(chartRef)

/**
 * 加载数据
 */
const fetchData = async () => {
  loading.value = true
  const res = await getSpaceRankAnalyzeUsingPost({
    topN: 10, // 后端默认10
  })
  if (res.data.code === 0) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}

// 图标选项
const options = computed(() => {
  const spaceNames = dataList.value.map((item) => item.spaceName)
  const usageData = dataList.value.map((item) => ((item.totalSize ?? 0) / (1024 * 1024)).toFixed(2)) // 转为 MB

  return {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: spaceNames,
    },
    yAxis: {
      type: 'value',
      name: '空间使用量 (MB)',
    },
    series: [
      {
        name: '空间使用量 (MB)',
        type: 'bar',
        data: usageData,
        itemStyle: {
          color: '#5470C6', // 自定义柱状图颜色
        },
      },
    ],
  }
})

/**
 * 监听变量变化
 */
watchEffect(() => {
  fetchData()
})
</script>

<style scoped></style>
