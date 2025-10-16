<template>
  <div id="picture-manage-page">
    <!-- 搜索表单 -->
    <div class="bg-white p-6 rounded-lg shadow-sm mb-4">
      <a-form :model="searchParams" @finish="doSearch">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item label="图片名称">
              <a-input
                v-model:value="searchParams.name"
                placeholder="请输入图片名称"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="简介">
              <a-input
                v-model:value="searchParams.introduction"
                placeholder="请输入图片简介"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="分类">
              <a-select
                v-model:value="searchParams.categoryId"
                :options="categoryOptions"
                placeholder="请选择分类"
                allowClear
                show-search
                :filter-option="(input, option) => option.label.toLowerCase().includes(input.toLowerCase())"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="标签">
              <a-select
                v-model:value="searchParams.tags"
                :options="tagOptions"
                mode="multiple"
                placeholder="请选择标签"
                allowClear
                show-search
                :filter-option="(input, option) => option.label.toLowerCase().includes(input.toLowerCase())"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16" justify="space-between">
          <a-col :span="6">
            <a-form-item label="排序字段">
              <a-select v-model:value="searchParams.sortField" placeholder="排序字段" allowClear>
                <a-select-option value="createTime">创建时间</a-select-option>
                <a-select-option value="editTime">编辑时间</a-select-option>
                <a-select-option value="name">图片名称</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="排序顺序">
              <a-select v-model:value="searchParams.sortOrder" placeholder="排序顺序" allowClear>
                <a-select-option value="desc">降序</a-select-option>
                <a-select-option value="asc">升序</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12" class="button-col">
            <a-form-item>
              <a-space size="middle">
                <a-button type="primary" html-type="submit" :loading="loading">搜索</a-button>
                <a-button @click="doReset">重置</a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 图片表格 -->
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
          <template v-if="column.key === 'picUrl'">
            <a-image
              :src="record.url"
              :width="80"
              :height="80"
              :preview="{ src: record.url }"
              class="rounded-lg object-cover cursor-pointer"
              fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mN8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="
            />
          </template>
          <template v-if="column.key === 'name'">
            <div class="max-w-xs truncate" :title="record.name">
              {{ record.name }}
            </div>
          </template>
          <template v-if="column.key === 'introduction'">
            <div class="max-w-xs truncate" :title="record.introduction">
              {{ record.introduction || '-' }}
            </div>
          </template>
          <template v-if="column.key === 'category'">
            <a-tag v-if="record.category" color="blue">{{ record.category.name }}</a-tag>
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'tags'">
            <a-space wrap>
              <a-tag v-for="tag in record.tagVOList" :key="tag" color="green">{{ tag.name }}</a-tag>
              <span v-if="!record.tags || record.tags.length === 0">-</span>
            </a-space>
          </template>
          <template v-if="column.key === 'user'">
            <div class="flex items-center gap-2">
              <a-avatar :src="record.user?.userAvatar" size="small">
                <template #icon>
                  <UserOutlined />
                </template>
              </a-avatar>
              <span>{{ record.user?.userName || '-' }}</span>
            </div>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ record.createTime ? dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
          </template>
          <template v-if="column.key === 'editTime'">
            {{ record.editTime ? dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" :href="`/add_picture?id=${record.id}`" class="action-btn">
                <EditOutlined />
                <span>编辑</span>
              </a-button>
              <a-popconfirm
                title="确定要删除此图片吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="doDelete(record.id)"
              >
                <a-button type="link" size="small" danger class="action-btn">
                  <DeleteOutlined />
                  <span>删除</span>
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { EditOutlined, DeleteOutlined, UserOutlined } from '@ant-design/icons-vue'
import {
  listPictureAdminVoByPageUsingPost,
  editPictureUsingPost,
  deletePictureUsingPost,
} from '@/api/pictureController'
import { listCategoryVoUsingPost } from '@/api/categoryController'
import { listTagVoUsingPost } from '@/api/tagController'
import dayjs from 'dayjs'

// 数据相关
const dataList = ref<API.PictureAdminVO[]>([])
const total = ref(0)
const loading = ref(false)

// 分类和标签选项
const categoryOptions = ref<{ value: string; label: string }[]>([])
const tagOptions = ref<{ value: string; label: string }[]>([])

// 搜索参数
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 模态框相关
const modalVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive<API.PictureEditRequest>({
  id: '',
  name: '',
  introduction: '',
  url: '',
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入图片名称', trigger: 'blur' }],
}

// 表格列配置
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 180,
    ellipsis: true,
  },
  {
    title: '图片',
    key: 'picUrl',
    width: 100,
    align: 'center',
  },
  {
    title: '名称',
    key: 'name',
    width: 150,
  },
  {
    title: '简介',
    key: 'introduction',
    width: 180,
  },
  {
    title: '分类',
    key: 'category',
    width: 100,
    align: 'center',
  },
  {
    title: '标签',
    key: 'tags',
    width: 180,
  },
  {
    title: '上传用户',
    key: 'user',
    width: 150,
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
  },
  {
    title: '编辑时间',
    key: 'editTime',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    fixed: 'right',
  },
]

// 分页配置
const pagination = computed(() => ({
  current: searchParams.current ?? 1,
  pageSize: searchParams.pageSize ?? 10,
  total: total.value,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`,
}))

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await listPictureAdminVoByPageUsingPost(searchParams)
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records ?? []
      total.value = res.data.data.total ?? 0
    } else {
      message.error('获取数据失败：' + res.data.message)
    }
  } catch (error) {
    message.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const doSearch = () => {
  searchParams.current = 1
  fetchData()
}

// 重置搜索
const doReset = () => {
  Object.assign(searchParams, {
    current: 1,
    pageSize: 10,
    name: undefined,
    introduction: undefined,
    categoryId: undefined,
    tags: undefined,
    sortField: undefined,
    sortOrder: undefined,
  })
  fetchData()
}

// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 删除图片
const doDelete = async (id: string) => {
  if (!id) return

  try {
    const res = await deletePictureUsingPost({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    message.error('删除失败，请稍后重试')
  }
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

// 页面初始化
onMounted(() => {
  fetchData()
  loadCategoryAndTag()
})
</script>

<style scoped>
:deep(.ant-table) {
  font-size: 14px;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  text-align: center;
}

:deep(.ant-form-item-label > label) {
  font-weight: 500;
}

:deep(.ant-modal-header) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.ant-modal-footer) {
  border-top: 1px solid #f0f0f0;
}

.action-btn {
  display: inline-flex !important;
  align-items: center !important;
  gap: 4px;
  padding: 4px 8px !important;
}

.action-btn span {
  line-height: 1;
}

.button-col {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}
</style>
