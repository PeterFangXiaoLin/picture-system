<template>
  <a-modal
    v-model:visible="visible"
    title="批量编辑图片"
    :footer="false"
    class="batch-edit-modal"
    @cancel="closeModal"
  >
    <a-alert type="warning" show-icon class="scope-alert">
      <template #message>
        <span class="scope-alert-title">编辑范围说明</span>
      </template>
      <template #description>只对当前页面的图片生效</template>
    </a-alert>

    <!-- 批量创建表单-->
    <a-form layout="vertical" :model="formData" @finish="handleSubmit" class="form-section">
      <a-form-item label="分类" name="categoryId" class="form-item">
        <a-select
          v-model:value="formData.categoryId"
          :options="categoryOptions"
          placeholder="请选择分类"
          allowClear
          size="large"
          class="rounded-lg"
          show-search
          :filter-option="
            (input, option) => option.label.toLowerCase().includes(input.toLowerCase())
          "
        />
      </a-form-item>

      <a-form-item label="标签" name="tags" class="form-item">
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

      <a-form-item label="命名规则" name="nameRule" class="form-item">
        <a-input
          v-model:value="formData.nameRule"
          placeholder="请输入命名规则，输入 {序号} 可动态生成"
          size="large"
          class="rounded-lg"
        />
        <template #extra>
          <div class="name-rule-hint">
            <span class="name-rule-hint-label">命名提示</span>
            <p class="name-rule-hint-text">
              使用
              <code class="name-rule-code">{序号}</code>
              作为占位符，系统会自动替换为递增编号。
            </p>
            <p class="name-rule-example">
              示例：<span>风景_{序号}</span> → 风景_1、风景_2、风景_3
            </p>
          </div>
        </template>
      </a-form-item>

      <a-form-item class="submit-section">
        <a-button
          type="primary"
          html-type="submit"
          block
          size="large"
          class="submit-button"
        >
          提交
        </a-button>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { listCategoryVoUsingPost } from '@/api/categoryController.ts'
import { listTagVoUsingPost } from '@/api/tagController.ts'
import { message } from 'ant-design-vue'
import { editPictureByBatchUsingPost } from '@/api/pictureController.ts'

/**
 * 定义组件属性
 */
interface Props {
  pictureList: API.PictureVO[]
  spaceId: string
  onSuccess: () => void
}

/**
 * 定义属性默认值
 */
const props = withDefaults(defineProps<Props>(), {})

// 是否可见
const visible = ref<boolean>(false)

// 打开窗口
const openModal = () => {
  visible.value = true
}

// 关闭窗口
const closeModal = () => {
  visible.value = false
}

// 暴露打开窗口给父组件
defineExpose({
  openModal,
})

const categoryOptions = ref<{ value: string | number; label: string }[]>([])
const tagOptions = ref<{ value: string; label: string }[]>([])

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

// 表单信息
const formData = reactive<API.PictureEditByBatchRequest>({})

// 提交表单
const handleSubmit = async (values: any) => {
  if (!props.pictureList) {
    return;
  }

  try {
    const res = await editPictureByBatchUsingPost({
      pictureIdList: props.pictureList.map((item: API.PictureVO) => item.id),
      spaceId: props.spaceId,
      ...values,
    })
    if (res.data.code === 0 && res.data.data) {
      message.success('操作成功')
      closeModal()
      props.onSuccess?.()
    } else {
      message.error('操作失败，' + res.data.message)
    }
  } catch (error) {
    message.error('操作失败，请稍后重试')
  }
}

</script>

<style scoped>
.scope-alert {
  margin-bottom: 20px;
  border: 1px solid #ffe58f;
  border-left: 4px solid #faad14;
  border-radius: 8px;
  background: #fffbe6;
}

.scope-alert :deep(.ant-alert-icon) {
  color: #faad14;
  margin-top: 2px;
}

.scope-alert-title {
  font-weight: 600;
  color: #874d00;
}

.scope-alert :deep(.ant-alert-description) {
  color: #614700;
  line-height: 1.6;
}

.form-section {
  margin-top: 4px;
}

.form-item :deep(.ant-form-item-label > label) {
  font-weight: 600;
  font-size: 15px;
  color: #374151;
}

.form-item :deep(.ant-input) {
  border-radius: 8px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-item :deep(.ant-select-selector) {
  border-radius: 8px !important;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.name-rule-hint {
  margin-top: 8px;
  padding: 12px 14px;
  border: 1px solid #91caff;
  border-left: 4px solid #1677ff;
  border-radius: 8px;
  background: #e6f4ff;
}

.name-rule-hint-label {
  display: inline-block;
  margin-bottom: 6px;
  padding: 2px 8px;
  border-radius: 4px;
  background: #bae0ff;
  color: #0958d9;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.5;
}

.name-rule-hint-text {
  margin: 0;
  color: #434343;
  font-size: 13px;
  line-height: 1.6;
}

.name-rule-code {
  padding: 1px 6px;
  border: 1px solid #91caff;
  border-radius: 4px;
  background: #fff;
  color: #0958d9;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  font-weight: 500;
}

.name-rule-example {
  margin: 8px 0 0;
  padding-top: 8px;
  border-top: 1px solid #91caff;
  color: #434343;
  font-size: 13px;
  line-height: 1.6;
}

.name-rule-example span {
  color: #0958d9;
  font-weight: 600;
}

.submit-section {
  margin-top: 28px;
  margin-bottom: 0;
}

.submit-button {
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
}
</style>
