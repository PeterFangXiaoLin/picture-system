<template>
  <div class="picture-search-form">
    <div class="search-form-card">
      <a-form :model="searchParams" @finish="doSearch" class="search-form">
        <a-row :gutter="[16, 4]">
          <a-col :xs="24" :sm="12" :lg="6">
            <a-form-item label="关键词">
              <a-input
                v-model:value="searchParams.searchText"
                placeholder="从名称和简介搜索"
                allowClear
                class="search-control"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :lg="6">
            <a-form-item label="类型">
              <a-select
                v-model:value="searchParams.categoryId"
                :options="categoryOptions"
                placeholder="请选择类型"
                allowClear
                show-search
                class="search-control"
                :filter-option="
                  (input, option) => option.label.toLowerCase().includes(input.toLowerCase())
                "
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="24" :lg="12">
            <a-form-item label="标签">
              <a-select
                v-model:value="searchParams.tags"
                :options="tagOptions"
                mode="multiple"
                placeholder="请选择标签"
                allowClear
                show-search
                max-tag-count="responsive"
                class="search-control search-control--tags"
                :filter-option="
                  (input, option) => option.label.toLowerCase().includes(input.toLowerCase())
                "
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="[16, 4]">
          <a-col :xs="24" :sm="24" :lg="12">
            <a-form-item label="日期" name="dateRange">
              <a-range-picker
                class="search-control"
                show-time
                v-model:value="dateRange"
                :placeholder="['编辑开始时间', '编辑结束时间']"
                format="YYYY/MM/DD HH:mm:ss"
                :presets="rangePresets"
                @change="onRangeChange"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :lg="6">
            <a-form-item label="名称" name="name">
              <a-input
                v-model:value="searchParams.name"
                placeholder="请输入名称"
                allow-clear
                class="search-control"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :lg="6">
            <a-form-item label="简介" name="introduction">
              <a-input
                v-model:value="searchParams.introduction"
                placeholder="请输入简介"
                allow-clear
                class="search-control"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="[16, 4]" align="bottom">
          <a-col :xs="12" :sm="8" :lg="4">
            <a-form-item label="宽度" name="picWidth">
              <a-input-number v-model:value="searchParams.picWidth" class="search-control" />
            </a-form-item>
          </a-col>
          <a-col :xs="12" :sm="8" :lg="4">
            <a-form-item label="高度" name="picHeight">
              <a-input-number v-model:value="searchParams.picHeight" class="search-control" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="8" :lg="4">
            <a-form-item label="格式" name="picFormat">
              <a-select
                v-model:value="searchParams.picFormat"
                :options="PIC_FORMAT_OPTIONS"
                placeholder="请选择格式"
                allowClear
                class="search-control"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :lg="12" class="search-form-actions">
            <a-form-item>
              <a-space :size="12">
                <a-button type="primary" html-type="submit" class="search-btn">搜索</a-button>
                <a-button html-type="reset" class="search-btn" @click="doClear">重置</a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { listCategoryVoUsingPost } from '@/api/categoryController'
import { listTagVoUsingPost } from '@/api/tagController'
import dayjs from 'dayjs'
import { PIC_FORMAT_OPTIONS } from '@/constants/picture'

interface Props {
  onSearch?: (searchParams: API.PictureQueryRequest) => void
}

const props = defineProps<Props>()

// 分类和标签选项
const categoryOptions = ref<{ value: string; label: string }[]>([])
const tagOptions = ref<{ value: string; label: string }[]>([])

// 搜索参数
const searchParams = reactive<API.PictureQueryRequest>({})

// 搜索
const doSearch = () => {
  props.onSearch?.(searchParams)
}

// 加载分类和标签列表
const loadCategoryAndTag = async () => {
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

const dateRange = ref<[]>([])

/** 将日期选择器的值同步到 searchParams（用户操作与代码重置共用） */
const syncDateRange = (dates?: any[] | null) => {
  if (dates && dates.length >= 2) {
    searchParams.startEditTime = dates[0].toDate()
    searchParams.endEditTime = dates[1].toDate()
  } else {
    searchParams.startEditTime = undefined
    searchParams.endEditTime = undefined
  }
}

const onRangeChange = (dates: any[]) => {
  syncDateRange(dates)
}

const rangePresets = ref([
  { label: '过去 7 天', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: '过去 14 天', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: '过去 30 天', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: '过去 90 天', value: [dayjs().add(-90, 'd'), dayjs()] },
])

// 重置
const doClear = () => {
  Object.assign(searchParams, {
    searchText: undefined,
    categoryId: undefined,
    tags: undefined,
    name: undefined,
    introduction: undefined,
    picWidth: undefined,
    picHeight: undefined,
    picFormat: undefined,
    startEditTime: undefined,
    endEditTime: undefined,
  })
  dateRange.value = []
  // 程序化清空 v-model 不会触发 RangePicker 的 change 事件，需手动同步
  syncDateRange(null)
  props.onSearch?.(searchParams)
}

onMounted(() => {
  loadCategoryAndTag()
})
</script>

<style scoped>
.search-form-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  padding: 20px 24px 8px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
}

.search-form :deep(.ant-form-item) {
  margin-bottom: 16px;
}

.search-form :deep(.ant-form-item-label > label) {
  color: #334155;
  font-weight: 500;
  font-size: 13px;
}

.search-control {
  width: 100%;
}

.search-control--tags :deep(.ant-select-selector) {
  min-height: 40px;
  height: auto !important;
  padding: 4px 8px;
}

.search-control--tags :deep(.ant-select-selection-overflow) {
  flex-wrap: wrap;
  gap: 4px;
  row-gap: 4px;
}

.search-control--tags :deep(.ant-select-selection-item) {
  margin-inline-end: 0;
  max-width: 100%;
}

.search-form-actions {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}

.search-btn {
  min-width: 88px;
  transition: opacity 0.2s ease, box-shadow 0.2s ease;
}

.search-btn:hover {
  opacity: 0.92;
}

@media (max-width: 991px) {
  .search-form-card {
    padding: 16px 16px 4px;
  }

  .search-form-actions {
    justify-content: flex-start;
    margin-top: 4px;
  }
}
</style>
