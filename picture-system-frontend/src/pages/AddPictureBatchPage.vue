<template>
  <div id="addPictureBatchPage">
    <h2 style="margin-bottom: 16px">批量创建</h2>
    <!-- 图片信息表单 -->
    <a-form name="formData" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item name="searchText" label="关键词">
        <a-input v-model:value="formData.searchText" placeholder="请输入关键词" allow-clear />
      </a-form-item>
      <a-form-item name="count" label="抓取数量">
        <a-input-number
          v-model:value="formData.count"
          placeholder="请输入数量"
          style="min-width: 180px"
          :min="1"
          :max="30"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="namePrefix" label="名称前缀">
        <a-input
          v-model:value="formData.namePrefix"
          placeholder="请输入名称前缀，会自动补充序号"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="first" label="起始偏移量">
        <a-input-number
          v-model:value="formData.first"
          placeholder="请输入起始偏移量"
          class="min-w-180px"
          :min="1"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="categoryId" label="分类">
        <a-select
          v-model:value="formData.categoryId"
          :options="categoryOptions"
          placeholder="请选择分类"
          allowClear
          size="large"
          class="rounded-lg"
          show-search
          :filter-option="(input, option) => option.label.toLowerCase().includes(input.toLowerCase())"
        />
      </a-form-item>
      <a-form-item name="tags" label="标签">
        <a-select
          v-model:value="formData.tags"
          mode="multiple"
          placeholder="请选择标签（可多选）"
          :options="tagOptions"
          allowClear
          size="large"
          class="rounded-lg"
          :maxTagCount="5"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          执行任务
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  uploadPictureByBatchUsingPost,
} from '@/api/pictureController.ts'
import { useRouter } from 'vue-router'
import { listCategoryVoUsingPost } from '@/api/categoryController.ts'
import { listTagVoUsingPost } from '@/api/tagController.ts'

const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})

const categoryOptions = ref<{ value: string | number; label: string }[]>([])
const tagOptions = ref<{ value: string; label: string }[]>([])

// 提交任务状态
const loading = ref(false)

const router = useRouter()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  loading.value = true
  const res = await uploadPictureByBatchUsingPost({
    ...formData,
  })
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success(`创建成功，共 ${res.data.data} 条`)
    // 跳转到主页
    router.push({
      path: `/`,
    })
  } else {
    message.error('创建失败，' + res.data.message)
  }
  loading.value = false
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
</script>

<style scoped>
#addPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
