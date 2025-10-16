<template>
  <div id="addPicturePage">
    <h2 class="text-3xl font-bold text-center mb-8 text-gray-800">
      {{ route.query?.id ? '修改图片' : '创建图片' }}
    </h2>

    <div class="upload-section">
      <PictureUpload :picture="picture" :onSuccess="onSuccess" />
    </div>

    <a-form
      v-if="picture"
      layout="vertical"
      :model="pictureForm"
      @finish="handleSubmit"
      class="mt-8 form-section"
    >
      <a-form-item label="名称" name="name" class="form-item">
        <a-input
          v-model:value="pictureForm.name"
          placeholder="请输入图片名称"
          size="large"
          class="rounded-lg"
        />
      </a-form-item>

      <a-form-item label="简介" name="introduction" class="form-item">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="请输入图片简介"
          :rows="3"
          :auto-size="{ minRows: 3, maxRows: 6 }"
          allowClear
          size="large"
          class="rounded-lg"
        />
      </a-form-item>

      <a-form-item label="分类" name="categoryId" class="form-item">
        <a-select
          v-model:value="pictureForm.categoryId"
          :options="categoryOptions"
          placeholder="请选择分类"
          allowClear
          size="large"
          class="rounded-lg"
          show-search
          :filter-option="(input, option) => option.label.toLowerCase().includes(input.toLowerCase())"
        />
      </a-form-item>

      <a-form-item label="标签" name="tags" class="form-item">
        <a-select
          v-model:value="pictureForm.tags"
          mode="multiple"
          placeholder="请选择标签（可多选）"
          :options="tagOptions"
          allowClear
          size="large"
          class="rounded-lg"
          :maxTagCount="5"
        />
      </a-form-item>

      <a-form-item class="submit-section">
        <a-button
          type="primary"
          html-type="submit"
          block
          size="large"
          :loading="submitting"
          class="submit-button"
        >
          <template v-if="!submitting">创建图片</template>
          <template v-else>创建中...</template>
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import PictureUpload from '@/components/PictureUpload.vue'
import { editPictureUsingPost, getPictureVoByIdUsingGet } from '@/api/pictureController'
import { listCategoryVoUsingPost } from '@/api/categoryController'
import { listTagVoUsingPost } from '@/api/tagController'

const router = useRouter()

const picture = ref<API.PictureVO>()
const pictureForm = reactive<API.PictureEditRequest>({})
const submitting = ref(false)

const categoryOptions = ref<{ value: string | number; label: string }[]>([])
const tagOptions = ref<{ value: string; label: string }[]>([])

// 上传成功后回调
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

// 提交表单
const handleSubmit = async (values: any) => {
  const pictureId = picture.value?.id
  if (!pictureId) {
    message.error('图片信息丢失，请重新上传')
    return
  }

  submitting.value = true

  try {
    const payload = {
      id: pictureId,
      ...values,
    }

    const res = await editPictureUsingPost(payload)
    if (res.data.code === 0 && res.data.data) {
      message.success('创建成功')
      router.push({ path: `/picture/${pictureId}` })
    } else {
      message.error('创建失败，' + res.data.message)
    }
  } catch (error) {
    message.error('创建失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 加载分类和标签列表
const getCategoryAndTag = async () => {
  try {
    const [categoryRes, tagRes] = await Promise.all([
      listCategoryVoUsingPost(),
      listTagVoUsingPost(),
    ])

    categoryOptions.value =
      categoryRes.data.data?.map((c: any) => ({ value: c.id, label: c.name })) || []
    tagOptions.value = tagRes.data.data?.map((t: any) => ({ value: t.id, label: t.name })) || []
  } catch (error) {
    message.error('加载分类和标签失败')
  }
}

onMounted(() => {
  getCategoryAndTag()
})

const route = useRoute()

// 获取老数据
const getOldPicture = async () => {
  // 获取到 id
  const id = route.query?.id as string
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.categoryId = data.categoryId
      pictureForm.tags = data.tagVOList?.map((tag) => tag.id) || []
    }
  }
}

onMounted(() => {
  getOldPicture()
})
</script>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
  padding: 32px 24px;
  min-height: calc(100vh - 64px);
}

.upload-section {
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.upload-section:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
}

.form-section {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  animation: fadeInUp 0.5s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-item :deep(.ant-form-item-label > label) {
  font-weight: 600;
  font-size: 15px;
  color: #374151;
}

.form-item :deep(.ant-input),
.form-item :deep(.ant-input-textarea textarea) {
  border-radius: 10px;
  border: 2px solid #e5e7eb;
  transition: all 0.3s ease;
}

.form-item :deep(.ant-input:hover),
.form-item :deep(.ant-input-textarea textarea:hover) {
  border-color: #93c5fd;
}

.form-item :deep(.ant-input:focus),
.form-item :deep(.ant-input-textarea textarea:focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-item :deep(.ant-select-selector) {
  border-radius: 10px !important;
  border: 2px solid #e5e7eb !important;
  transition: all 0.3s ease;
}

.form-item :deep(.ant-select:hover .ant-select-selector) {
  border-color: #93c5fd !important;
}

.form-item :deep(.ant-select-focused .ant-select-selector) {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1) !important;
}

.form-item :deep(.ant-auto-complete .ant-input) {
  border-radius: 10px;
}

.submit-section {
  margin-top: 32px;
  margin-bottom: 0;
}

.submit-button {
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
  background: linear-gradient(135deg, #7c8ff0 0%, #8a5ab8 100%);
}

.submit-button:active {
  transform: translateY(0);
}

/* 响应式设计 */
@media (max-width: 640px) {
  #addPicturePage {
    padding: 20px 16px;
  }

  .upload-section,
  .form-section {
    padding: 20px;
    border-radius: 16px;
  }

  h2 {
    font-size: 24px;
    margin-bottom: 24px;
  }
}
</style>
