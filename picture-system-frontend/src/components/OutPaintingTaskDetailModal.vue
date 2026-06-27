<template>
  <a-modal
    v-model:visible="visible"
    title="扩图任务详情"
    width="900px"
    :footer="null"
    @cancel="handleClose"
  >
    <a-spin :spinning="loading">
      <template v-if="task">
        <a-descriptions bordered :column="2" size="small">
          <a-descriptions-item label="任务 ID" :span="2">{{ task.id }}</a-descriptions-item>
          <a-descriptions-item label="阿里云任务 ID" :span="2">{{ task.taskId }}</a-descriptions-item>
          <a-descriptions-item label="原图名称">{{ task.pictureName || '-' }}</a-descriptions-item>
          <a-descriptions-item label="任务状态">
            <a-tag :color="OUT_PAINTING_TASK_STATUS_COLOR[task.taskStatus ?? '']">
              {{ OUT_PAINTING_TASK_STATUS_MAP[task.taskStatus ?? ''] || task.taskStatus }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="isAdmin" label="用户 ID">{{ task.userId || '-' }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">
            {{ task.createTime ? dayjs(task.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="更新时间">
            {{ task.updateTime ? dayjs(task.updateTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
          </a-descriptions-item>
          <a-descriptions-item v-if="task.parentTaskId" label="关联重试任务">
            {{ task.parentTaskId }}
          </a-descriptions-item>
          <a-descriptions-item v-if="task.errorMessage" label="错误信息" :span="2">
            {{ translateOutPaintingError(task.errorCode, task.errorMessage) }}
          </a-descriptions-item>
        </a-descriptions>

        <a-row :gutter="16" class="image-row">
          <a-col :span="12">
            <h4>原图</h4>
            <a-image
              v-if="task.originalImageUrl"
              :src="task.originalImageUrl"
              :width="'100%'"
              class="preview-image"
            />
            <a-empty v-else description="暂无原图" />
          </a-col>
          <a-col :span="12">
            <h4>扩图结果</h4>
            <a-image
              v-if="task.outputImageUrl"
              :src="task.outputImageUrl"
              :width="'100%'"
              class="preview-image"
            />
            <a-empty v-else description="暂无结果图" />
          </a-col>
        </a-row>

        <div class="modal-actions">
          <a-space>
            <a-button :loading="refreshLoading" @click="refreshTask">刷新状态</a-button>
            <a-button
              v-if="task.outputImageUrl"
              type="primary"
              :href="task.outputImageUrl"
              target="_blank"
            >
              下载结果
            </a-button>
            <a-button type="primary" ghost @click="handleRetry" :loading="retryLoading">
              重试任务
            </a-button>
          </a-space>
        </div>
      </template>
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  getOutPaintingTaskByIdUsingGet,
  retryOutPaintingTaskUsingPost,
} from '@/api/pictureController'
import {
  OUT_PAINTING_TASK_STATUS_COLOR,
  OUT_PAINTING_TASK_STATUS_MAP,
} from '@/constants/outPainting'
import { translateOutPaintingError } from '@/utils/outPaintingError'

interface Props {
  isAdmin?: boolean
}

defineProps<Props>()

const emit = defineEmits<{
  refreshed: [task: API.OutPaintingTaskVO]
  retried: [task: API.OutPaintingTaskVO]
}>()

const visible = ref(false)
const loading = ref(false)
const refreshLoading = ref(false)
const retryLoading = ref(false)
const task = ref<API.OutPaintingTaskVO>()

const open = async (taskId: string) => {
  visible.value = true
  loading.value = true
  try {
    const res = await getOutPaintingTaskByIdUsingGet({ id: taskId })
    if (res.data.code === 0 && res.data.data) {
      task.value = res.data.data
    } else {
      message.error('获取任务详情失败：' + res.data.message)
      visible.value = false
    }
  } catch {
    message.error('获取任务详情失败')
    visible.value = false
  } finally {
    loading.value = false
  }
}

const refreshTask = async () => {
  if (!task.value?.id) return
  refreshLoading.value = true
  try {
    const res = await getOutPaintingTaskByIdUsingGet({ id: task.value.id })
    if (res.data.code === 0 && res.data.data) {
      task.value = res.data.data
      emit('refreshed', res.data.data)
      message.success('状态已刷新')
    } else {
      message.error('刷新失败：' + res.data.message)
    }
  } catch {
    message.error('刷新失败')
  } finally {
    refreshLoading.value = false
  }
}

const handleRetry = async () => {
  if (!task.value?.id) return
  retryLoading.value = true
  try {
    const res = await retryOutPaintingTaskUsingPost({ id: task.value.id })
    if (res.data.code === 0 && res.data.data) {
      message.success('已创建重试任务')
      emit('retried', res.data.data)
      visible.value = false
    } else {
      message.error('重试失败：' + res.data.message)
    }
  } catch {
    message.error('重试失败')
  } finally {
    retryLoading.value = false
  }
}

const handleClose = () => {
  visible.value = false
  task.value = undefined
}

defineExpose({ open })
</script>

<style scoped>
.image-row {
  margin-top: 16px;
}

.preview-image {
  border-radius: 8px;
}

.modal-actions {
  margin-top: 16px;
  text-align: center;
}

h4 {
  margin-bottom: 8px;
}
</style>
