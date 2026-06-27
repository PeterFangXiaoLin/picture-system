<template>
  <div id="out-painting-task-page">
    <div class="page-header">
      <div>
        <h2>我的扩图任务</h2>
        <p class="page-desc">查看历史扩图任务、任务结果，并支持重试失败任务</p>
      </div>
      <a-tag v-if="quotaInfo?.unlimited" color="blue">扩图次数：不限</a-tag>
      <a-tag v-else-if="quotaInfo" :color="(quotaInfo.remaining ?? 0) > 0 ? 'green' : 'red'">
        剩余扩图次数：{{ quotaInfo.remaining ?? 0 }}
      </a-tag>
    </div>

    <div class="bg-white p-6 rounded-lg shadow-sm mb-4">
      <a-form :model="searchParams" @finish="doSearch">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item label="任务状态">
              <a-select
                v-model:value="searchParams.taskStatus"
                :options="OUT_PAINTING_TASK_STATUS_OPTIONS"
                placeholder="全部状态"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="原图 ID">
              <a-input
                v-model:value="searchParams.pictureId"
                placeholder="请输入原图 ID"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :span="12" class="button-col">
            <a-form-item>
              <a-space>
                <a-button type="primary" html-type="submit" :loading="loading">搜索</a-button>
                <a-button @click="doReset">重置</a-button>
                <a-button :loading="loading" @click="fetchData">刷新列表</a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class="bg-white p-6 rounded-lg shadow-sm">
      <a-table
        :columns="columns"
        :data-source="dataList"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="doTableChange"
        :scroll="{ x: 1200 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'originalImage'">
            <a-image
              v-if="record.originalImageUrl"
              :src="record.originalImageUrl"
              :width="72"
              :height="72"
              class="rounded-lg object-cover"
            />
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'outputImage'">
            <a-image
              v-if="record.outputImageUrl"
              :src="record.outputImageUrl"
              :width="72"
              :height="72"
              class="rounded-lg object-cover"
            />
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'pictureName'">
            <div class="max-w-[160px] truncate" :title="record.pictureName">
              {{ record.pictureName || '-' }}
            </div>
          </template>
          <template v-if="column.key === 'taskStatus'">
            <a-tag :color="OUT_PAINTING_TASK_STATUS_COLOR[record.taskStatus]">
              {{ OUT_PAINTING_TASK_STATUS_MAP[record.taskStatus] || record.taskStatus }}
            </a-tag>
          </template>
          <template v-if="column.key === 'errorMessage'">
            <div
              v-if="record.errorMessage"
              class="max-w-[180px] truncate text-red-500"
              :title="translateOutPaintingError(record.errorCode, record.errorMessage)"
            >
              {{ translateOutPaintingError(record.errorCode, record.errorMessage) }}
            </div>
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ record.createTime ? dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="openDetail(record.id)">详情</a-button>
              <a-button
                v-if="!isOutPaintingTaskFinished(record.taskStatus)"
                type="link"
                size="small"
                :loading="refreshingId === record.id"
                @click="refreshRecord(record)"
              >
                刷新
              </a-button>
              <a-button type="link" size="small" @click="handleRetry(record)">重试</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <OutPaintingTaskDetailModal ref="detailModalRef" @refreshed="fetchData" @retried="fetchData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  listOutPaintingTaskByPageUsingPost,
  getOutPaintingTaskByIdUsingGet,
  retryOutPaintingTaskUsingPost,
  getOutPaintingQuotaUsingGet,
} from '@/api/pictureController'
import OutPaintingTaskDetailModal from '@/components/OutPaintingTaskDetailModal.vue'
import {
  OUT_PAINTING_TASK_STATUS_COLOR,
  OUT_PAINTING_TASK_STATUS_MAP,
  OUT_PAINTING_TASK_STATUS_OPTIONS,
  isOutPaintingTaskFinished,
} from '@/constants/outPainting'
import { translateOutPaintingError } from '@/utils/outPaintingError'

const dataList = ref<API.OutPaintingTaskVO[]>([])
const total = ref(0)
const loading = ref(false)
const refreshingId = ref<string>()
const detailModalRef = ref<InstanceType<typeof OutPaintingTaskDetailModal>>()
const quotaInfo = ref<API.OutPaintingQuotaVO>()

const searchParams = reactive<API.OutPaintingTaskQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

const columns = [
  { title: '原图', key: 'originalImage', width: 90, align: 'center' },
  { title: '结果图', key: 'outputImage', width: 90, align: 'center' },
  { title: '原图名称', key: 'pictureName', width: 160 },
  { title: '状态', key: 'taskStatus', width: 100, align: 'center' },
  { title: '错误信息', key: 'errorMessage', width: 180 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 180, fixed: 'right' },
]

const pagination = computed(() => ({
  current: searchParams.current ?? 1,
  pageSize: searchParams.pageSize ?? 10,
  total: total.value,
  showSizeChanger: true,
  showTotal: (count: number) => `共 ${count} 条`,
}))

const fetchQuota = async () => {
  try {
    const res = await getOutPaintingQuotaUsingGet()
    if (res.data.code === 0 && res.data.data) {
      quotaInfo.value = res.data.data
    }
  } catch {
    // ignore
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listOutPaintingTaskByPageUsingPost(searchParams)
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records ?? []
      total.value = res.data.data.total ?? 0
    } else {
      message.error('获取任务列表失败：' + res.data.message)
    }
  } catch {
    message.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const doSearch = () => {
  searchParams.current = 1
  fetchData()
}

const doReset = () => {
  Object.assign(searchParams, {
    current: 1,
    pageSize: 10,
    taskStatus: undefined,
    pictureId: undefined,
    sortField: 'createTime',
    sortOrder: 'descend',
  })
  fetchData()
}

const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

const openDetail = (id?: string) => {
  if (!id) return
  detailModalRef.value?.open(id)
}

const refreshRecord = async (record: API.OutPaintingTaskVO) => {
  if (!record.id) return
  refreshingId.value = record.id
  try {
    const res = await getOutPaintingTaskByIdUsingGet({ id: record.id })
    if (res.data.code === 0 && res.data.data) {
      message.success('状态已刷新')
      fetchData()
    } else {
      message.error('刷新失败：' + res.data.message)
    }
  } catch {
    message.error('刷新失败')
  } finally {
    refreshingId.value = undefined
  }
}

const handleRetry = async (record: API.OutPaintingTaskVO) => {
  if (!record.id) return
  try {
    const res = await retryOutPaintingTaskUsingPost({ id: record.id })
    if (res.data.code === 0) {
      message.success('已创建重试任务')
      fetchData()
    } else {
      message.error('重试失败：' + res.data.message)
    }
  } catch {
    message.error('重试失败')
  }
}

let pollingTimer: ReturnType<typeof setInterval> | null = null

const startPolling = () => {
  pollingTimer = setInterval(() => {
    const hasProcessing = dataList.value.some((item) => !isOutPaintingTaskFinished(item.taskStatus))
    if (hasProcessing) {
      fetchData()
    }
  }, 5000)
}

onMounted(() => {
  fetchQuota()
  fetchData()
  startPolling()
})

onUnmounted(() => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
  }
})
</script>

<style scoped>
.page-header {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-header h2 {
  margin: 0 0 4px;
  font-size: 22px;
  font-weight: 600;
}

.page-desc {
  margin: 0;
  color: #666;
}

.button-col {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}
</style>
