<template>
  <div id="out-painting-task-manage-page">
    <div class="page-header">
      <h2>扩图任务监控</h2>
      <p class="page-desc">查看系统全部扩图任务执行情况与统计数据</p>
    </div>

    <a-row :gutter="16" class="statistics-row">
      <a-col :span="4">
        <a-card>
          <a-statistic title="任务总数" :value="statistics.totalCount ?? 0" />
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card>
          <a-statistic
            title="成功"
            :value="statistics.successCount ?? 0"
            :value-style="{ color: '#3f8600' }"
          />
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card>
          <a-statistic
            title="失败"
            :value="statistics.failedCount ?? 0"
            :value-style="{ color: '#cf1322' }"
          />
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card>
          <a-statistic title="处理中" :value="statistics.processingCount ?? 0" />
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card>
          <a-statistic title="成功率" :value="statistics.successRate ?? 0" suffix="%" />
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card>
          <a-statistic title="失败率" :value="statistics.failureRate ?? 0" suffix="%" />
        </a-card>
      </a-col>
    </a-row>

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
            <a-form-item label="用户 ID">
              <a-input v-model:value="searchParams.userId" placeholder="请输入用户 ID" allowClear />
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
          <a-col :span="6" class="button-col">
            <a-form-item>
              <a-space>
                <a-button type="primary" html-type="submit" :loading="loading">搜索</a-button>
                <a-button @click="doReset">重置</a-button>
                <a-button @click="refreshAll">刷新</a-button>
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
        :scroll="{ x: 1400 }"
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
            <a-button type="link" size="small" @click="openDetail(record.id)">详情</a-button>
          </template>
        </template>
      </a-table>
    </div>

    <OutPaintingTaskDetailModal
      ref="detailModalRef"
      is-admin
      @refreshed="refreshAll"
      @retried="refreshAll"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  listOutPaintingTaskByPageAdminUsingPost,
  getOutPaintingTaskStatisticsUsingGet,
} from '@/api/pictureController'
import OutPaintingTaskDetailModal from '@/components/OutPaintingTaskDetailModal.vue'
import {
  OUT_PAINTING_TASK_STATUS_COLOR,
  OUT_PAINTING_TASK_STATUS_MAP,
  OUT_PAINTING_TASK_STATUS_OPTIONS,
} from '@/constants/outPainting'
import { translateOutPaintingError } from '@/utils/outPaintingError'

const dataList = ref<API.OutPaintingTaskVO[]>([])
const total = ref(0)
const loading = ref(false)
const detailModalRef = ref<InstanceType<typeof OutPaintingTaskDetailModal>>()
const statistics = ref<API.OutPaintingTaskStatisticsVO>({})

const searchParams = reactive<API.OutPaintingTaskQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 180, ellipsis: true },
  { title: '用户 ID', dataIndex: 'userId', key: 'userId', width: 180, ellipsis: true },
  { title: '原图', key: 'originalImage', width: 90, align: 'center' },
  { title: '结果图', key: 'outputImage', width: 90, align: 'center' },
  { title: '原图名称', dataIndex: 'pictureName', key: 'pictureName', width: 140, ellipsis: true },
  { title: '状态', key: 'taskStatus', width: 100, align: 'center' },
  { title: '错误信息', key: 'errorMessage', width: 180 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 100, fixed: 'right' },
]

const pagination = computed(() => ({
  current: searchParams.current ?? 1,
  pageSize: searchParams.pageSize ?? 10,
  total: total.value,
  showSizeChanger: true,
  showTotal: (count: number) => `共 ${count} 条`,
}))

const fetchStatistics = async () => {
  try {
    const res = await getOutPaintingTaskStatisticsUsingGet()
    if (res.data.code === 0 && res.data.data) {
      statistics.value = res.data.data
    }
  } catch {
    message.error('获取统计数据失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listOutPaintingTaskByPageAdminUsingPost(searchParams)
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

const refreshAll = async () => {
  await Promise.all([fetchStatistics(), fetchData()])
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
    userId: undefined,
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

onMounted(() => {
  refreshAll()
})
</script>

<style scoped>
.page-header {
  margin-bottom: 16px;
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

.statistics-row {
  margin-bottom: 16px;
}

.button-col {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}
</style>
