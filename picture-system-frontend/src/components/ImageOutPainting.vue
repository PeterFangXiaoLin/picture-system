<template>
  <a-modal
    class="image-out-painting"
    v-model:visible="visible"
    title="AI 扩图"
    :footer="false"
    width="960px"
    @cancel="closeModal"
  >
    <a-row :gutter="16">
      <a-col :span="12">
        <h4>原始图片</h4>
        <img :src="picture?.url" :alt="picture?.name" class="preview-image" />
      </a-col>
      <a-col :span="12">
        <h4>扩图结果</h4>
        <img
          v-if="resultImageUrl"
          :src="resultImageUrl"
          :alt="picture?.name"
          class="preview-image"
        />
        <a-empty v-else description="生成后将在此预览" />
      </a-col>
    </a-row>

    <a-divider />

    <a-alert
      v-if="quotaInfo"
      :type="quotaAlertType"
      show-icon
      class="quota-alert"
      :message="quotaMessage"
    />

    <a-form layout="vertical" :model="formData" class="params-form">
      <a-form-item label="扩图方式">
        <a-radio-group v-model:value="formData.mode" button-style="solid">
          <a-radio-button value="scale">按比例扩展</a-radio-button>
          <a-radio-button value="ratio">按宽高比</a-radio-button>
          <a-radio-button value="offset">按方向扩展</a-radio-button>
          <a-radio-button value="rotate">仅旋转</a-radio-button>
        </a-radio-group>
      </a-form-item>

      <template v-if="formData.mode === 'scale'">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="水平扩展比例 (xScale)" extra="取值 1.0~3.0，至少一个方向大于 1.0">
              <a-input-number
                v-model:value="formData.xScale"
                :min="1"
                :max="3"
                :step="0.1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="垂直扩展比例 (yScale)" extra="取值 1.0~3.0，至少一个方向大于 1.0">
              <a-input-number
                v-model:value="formData.yScale"
                :min="1"
                :max="3"
                :step="0.1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </template>

      <a-form-item v-if="formData.mode === 'ratio'" label="输出宽高比 (outputRatio)">
        <a-select
          v-model:value="formData.outputRatio"
          :options="outputRatioOptions"
          placeholder="请选择宽高比"
        />
      </a-form-item>

      <template v-if="formData.mode === 'offset'">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="上方扩展像素 (topOffset)">
              <a-input-number
                v-model:value="formData.topOffset"
                :min="0"
                :step="10"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="下方扩展像素 (bottomOffset)">
              <a-input-number
                v-model:value="formData.bottomOffset"
                :min="0"
                :step="10"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="左侧扩展像素 (leftOffset)">
              <a-input-number
                v-model:value="formData.leftOffset"
                :min="0"
                :step="10"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="右侧扩展像素 (rightOffset)">
              <a-input-number
                v-model:value="formData.rightOffset"
                :min="0"
                :step="10"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </template>

      <a-form-item
        v-if="formData.mode !== 'rotate'"
        label="旋转角度 (angle)"
        extra="逆时针旋转，0 表示不旋转；扩图时会先旋转再扩展"
      >
        <a-input-number
          v-model:value="formData.angle"
          :min="0"
          :max="359"
          :step="1"
          style="width: 100%"
        />
      </a-form-item>

      <a-form-item
        v-else
        label="旋转角度 (angle)"
        extra="仅旋转模式：取值 1~359，不可为 90、180、270"
      >
        <a-input-number
          v-model:value="formData.angle"
          :min="1"
          :max="359"
          :step="1"
          style="width: 100%"
        />
      </a-form-item>

      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="最佳质量 (bestQuality)">
            <a-switch v-model:checked="formData.bestQuality" />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="限制图像大小 (limitImageSize)">
            <a-switch v-model:checked="formData.limitImageSize" />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="添加 AI 水印 (addWatermark)">
            <a-switch v-model:checked="formData.addWatermark" />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

    <a-flex gap="16" justify="center">
      <a-button
        type="primary"
        :loading="!!taskId"
        :disabled="quotaDisabled"
        ghost
        @click="createTask"
      >
        生成图片
      </a-button>
      <a-button v-if="resultImageUrl" type="primary" :loading="uploadLoading" @click="handleUpload">
        应用结果
      </a-button>
    </a-flex>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import {
  createPictureOutPaintingTaskUsingPost,
  getPictureOutPaintingTaskUsingGet,
  getOutPaintingQuotaUsingGet,
  uploadPictureByUrlUsingPost,
} from '@/api/pictureController'
import { message } from 'ant-design-vue'
import { translateOutPaintingError } from '@/utils/outPaintingError'

interface Props {
  picture?: API.PictureVO
  spaceId?: string
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

const outputRatioOptions = [
  { label: '1:1', value: '1:1' },
  { label: '3:4', value: '3:4' },
  { label: '4:3', value: '4:3' },
  { label: '9:16', value: '9:16' },
  { label: '16:9', value: '16:9' },
]

type ExpandMode = 'scale' | 'ratio' | 'offset' | 'rotate'

const defaultFormData = () => ({
  mode: 'scale' as ExpandMode,
  xScale: 2,
  yScale: 2,
  outputRatio: '4:3',
  topOffset: 0,
  bottomOffset: 0,
  leftOffset: 0,
  rightOffset: 0,
  angle: 0,
  bestQuality: false,
  limitImageSize: true,
  addWatermark: true,
})

const formData = reactive(defaultFormData())

const resultImageUrl = ref<string>('')

const taskId = ref<string>()

const quotaInfo = ref<API.OutPaintingQuotaVO>()

const quotaDisabled = computed(() => {
  if (!quotaInfo.value) {
    return false
  }
  if (quotaInfo.value.unlimited) {
    return false
  }
  return (quotaInfo.value.remaining ?? 0) <= 0
})

const quotaMessage = computed(() => {
  if (!quotaInfo.value) {
    return ''
  }
  if (quotaInfo.value.unlimited) {
    return '管理员账号，扩图次数不限'
  }
  const remaining = quotaInfo.value.remaining ?? 0
  if (remaining <= 0) {
    return '扩图次数已用尽，请联系管理员充值'
  }
  return `剩余扩图次数：${remaining} 次`
})

const quotaAlertType = computed(() => {
  if (quotaInfo.value?.unlimited) {
    return 'info'
  }
  return (quotaInfo.value?.remaining ?? 0) <= 0 ? 'error' : 'info'
})

const fetchQuota = async () => {
  try {
    const res = await getOutPaintingQuotaUsingGet()
    if (res.data.code === 0 && res.data.data) {
      quotaInfo.value = res.data.data
    }
  } catch {
    // 忽略额度查询失败，创建任务时后端仍会校验
  }
}

const buildParameters = (): API.Parameters => {
  const params: API.Parameters = {
    bestQuality: formData.bestQuality,
    limitImageSize: formData.limitImageSize,
    addWatermark: formData.addWatermark,
  }

  if (formData.mode === 'rotate') {
    params.angle = formData.angle
    return params
  }

  if (formData.angle > 0) {
    params.angle = formData.angle
  }

  if (formData.mode === 'ratio') {
    params.outputRatio = formData.outputRatio
  } else if (formData.mode === 'scale') {
    params.xScale = formData.xScale
    params.yScale = formData.yScale
  } else if (formData.mode === 'offset') {
    if (formData.topOffset > 0) params.topOffset = formData.topOffset
    if (formData.bottomOffset > 0) params.bottomOffset = formData.bottomOffset
    if (formData.leftOffset > 0) params.leftOffset = formData.leftOffset
    if (formData.rightOffset > 0) params.rightOffset = formData.rightOffset
  }

  return params
}

const validateParameters = (): boolean => {
  if (formData.mode === 'scale') {
    const x = formData.xScale ?? 1
    const y = formData.yScale ?? 1
    if (x <= 1 && y <= 1) {
      message.warning('按比例扩展时，xScale 或 yScale 至少有一个需大于 1.0')
      return false
    }
  }

  if (formData.mode === 'ratio' && !formData.outputRatio) {
    message.warning('请选择输出宽高比')
    return false
  }

  if (formData.mode === 'offset') {
    const total =
      (formData.topOffset ?? 0) +
      (formData.bottomOffset ?? 0) +
      (formData.leftOffset ?? 0) +
      (formData.rightOffset ?? 0)
    if (total <= 0) {
      message.warning('按方向扩展时，至少一个方向扩展像素需大于 0')
      return false
    }
  }

  if (formData.mode === 'rotate') {
    const angle = formData.angle
    if (!angle || angle < 1 || angle > 359) {
      message.warning('仅旋转模式时，角度需在 1~359 之间')
      return false
    }
    if ([90, 180, 270].includes(angle)) {
      message.warning('旋转角度不可为 90、180 或 270')
      return false
    }
  }

  return true
}

const createTask = async () => {
  if (!props.picture?.id) {
    return
  }
  if (quotaDisabled.value) {
    message.error('扩图次数不足，次数已用尽，请联系管理员')
    return
  }
  if (!validateParameters()) {
    return
  }
  try {
    const res = await createPictureOutPaintingTaskUsingPost({
      pictureId: props.picture.id,
      parameters: buildParameters(),
    })
    if (res.data.code === 0 && res.data.data) {
      const taskData = res.data.data
      if (!taskData.taskId) {
        message.error('创建任务失败，未返回任务 ID')
        return
      }
      message.success('创建任务成功，请耐心等待，不要退出界面')
      taskId.value = taskData.taskId
      fetchQuota()
      startPolling()
    } else {
      message.error(translateOutPaintingError(undefined, res.data.message))
    }
  } catch (e: unknown) {
    const err = e as { message?: string }
    console.error('扩图失败: ', err.message)
    message.error(translateOutPaintingError(undefined, err.message))
  }
}

let pollingTimer: ReturnType<typeof setInterval> | null = null

const startPolling = () => {
  if (!taskId.value) {
    return
  }

  pollingTimer = setInterval(async () => {
    try {
      const res = await getPictureOutPaintingTaskUsingGet({
        taskId: taskId.value,
      })
      if (res.data.code === 0 && res.data.data) {
        const taskResult = res.data.data.output
        if (!taskResult) {
          return
        }
        if (taskResult.taskStatus === 'SUCCEEDED') {
          message.success('扩图任务执行成功')
          resultImageUrl.value = taskResult.outputImageUrl ?? ''
          fetchQuota()
          clearPolling()
        } else if (taskResult.taskStatus === 'FAILED') {
          message.error(
            translateOutPaintingError(taskResult.code, taskResult.message),
          )
          fetchQuota()
          clearPolling()
        } else if (taskResult.taskStatus === 'CANCELED') {
          message.warning('扩图任务已取消')
          clearPolling()
        } else if (taskResult.taskStatus === 'UNKNOWN') {
          message.error('扩图任务不存在或状态未知，请重新创建任务')
          clearPolling()
        }
      } else {
        message.error(translateOutPaintingError(undefined, res.data.message))
        clearPolling()
      }
    } catch (e: unknown) {
      const err = e as { message?: string }
      console.error('扩图任务轮询失败', err)
      message.error('扩图任务轮询失败：' + (err.message ?? '未知错误'))
      clearPolling()
    }
  }, 3000)
}

const clearPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
    taskId.value = undefined
  }
}

const uploadLoading = ref<boolean>(false)
const handleUpload = async () => {
  uploadLoading.value = true
  try {
    const params: API.PictureUploadRequest = {
      fileUrl: resultImageUrl.value,
      spaceId: props.spaceId,
    }
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('图片上传成功')
      props.onSuccess?.(res.data.data)
      closeModal()
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error: unknown) {
    const err = error as { message?: string }
    console.error('图片上传失败', err)
    message.error('图片上传失败，' + (err.message ?? '未知错误'))
  }
  uploadLoading.value = false
}

const visible = ref(false)

const resetState = () => {
  Object.assign(formData, defaultFormData())
  resultImageUrl.value = ''
  clearPolling()
}

const openModal = () => {
  resetState()
  fetchQuota()
  visible.value = true
}

const closeModal = () => {
  visible.value = false
  clearPolling()
}

defineExpose({
  openModal,
})
</script>

<style scoped>
.image-out-painting {
  text-align: center;
}

.preview-image {
  max-width: 100%;
  border-radius: 8px;
}

.params-form {
  text-align: left;
  margin-bottom: 16px;
}

.quota-alert {
  margin-bottom: 16px;
  text-align: left;
}

h4 {
  margin-bottom: 8px;
}
</style>
