<template>
  <div id="category-manage-page">
    <!-- 搜索表单 -->
    <div class="bg-white p-6 rounded-lg shadow-sm mb-4">
      <a-form :model="searchParams" @finish="doSearch">
        <a-row :gutter="16" justify="space-between">
          <a-col :span="6">
            <a-form-item label="分类名称">
              <a-input
                v-model:value="searchParams.name"
                placeholder="请输入分类名称"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="排序字段">
              <a-select v-model:value="searchParams.sortField" placeholder="排序字段" allowClear>
                <a-select-option value="createTime">创建时间</a-select-option>
                <a-select-option value="name">分类名称</a-select-option>
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
          <a-col :span="6" class="button-col">
            <a-form-item>
              <a-space size="middle">
                <a-button type="primary" html-type="submit" :loading="loading">搜索</a-button>
                <a-button @click="doReset">重置</a-button>
                <a-button type="primary" @click="showAddModal" class="add-btn">
                  <PlusOutlined />
                  <span>新增</span>
                </a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 分类表格 -->
    <div class="bg-white p-6 rounded-lg shadow-sm">
      <a-table
        :columns="columns"
        :data-source="dataList"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="doTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'createTime'">
            {{ record.createTime ? dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
          </template>
          <template v-if="column.key === 'updateTime'">
            {{ record.updateTime ? dayjs(record.updateTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="showEditModal(record)" class="action-btn">
                <EditOutlined />
                <span>编辑</span>
              </a-button>
              <a-popconfirm
                title="确定要删除此分类吗？"
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

    <!-- 新增/编辑分类模态框 -->
    <a-modal
      :title="isEdit ? '编辑分类' : '新增分类'"
      :open="modalVisible"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      :confirm-loading="submitLoading"
      width="500px"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
        <a-form-item label="分类名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入分类名称" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import {
  listCategoryVoByPageUsingPost,
  addCategoryUsingPost,
  updateCategoryUsingPost,
  deleteCategoryUsingPost,
} from '@/api/categoryController'
import dayjs from 'dayjs'

// 数据相关
const dataList = ref<API.CategoryVO[]>([])
const total = ref(0)
const loading = ref(false)

// 搜索参数
const searchParams = reactive<API.CategoryQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 模态框相关
const modalVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive<API.CategoryAddRequest & API.CategoryUpdateRequest>({
  name: '',
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, message: '分类名称至少2个字符', trigger: 'blur' },
  ],
}

// 表格列配置
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 200,
    ellipsis: true,
  },
  {
    title: '分类名称',
    dataIndex: 'name',
    key: 'name',
    width: 200,
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
  },
  {
    title: '更新时间',
    key: 'updateTime',
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
    const res = await listCategoryVoByPageUsingPost(searchParams)
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

// 显示新增模态框
const showAddModal = () => {
  isEdit.value = false
  modalVisible.value = true
  resetForm()
}

// 显示编辑模态框
const showEditModal = (record: API.CategoryVO) => {
  isEdit.value = true
  modalVisible.value = true
  Object.assign(formData, {
    id: record.id,
    name: record.name,
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: undefined,
    name: '',
  })
  formRef.value?.resetFields()
}

// 模态框确定
const handleModalOk = async () => {
  try {
    await formRef.value.validateFields()
    submitLoading.value = true

    let res
    if (isEdit.value) {
      // 编辑分类
      res = await updateCategoryUsingPost({
        id: formData.id,
        name: formData.name,
      })
    } else {
      // 新增分类
      res = await addCategoryUsingPost({
        name: formData.name,
      })
    }

    if (res.data.code === 0) {
      message.success(isEdit.value ? '编辑成功' : '新增成功')
      modalVisible.value = false
      fetchData()
    } else {
      message.error((isEdit.value ? '编辑失败：' : '新增失败：') + res.data.message)
    }
  } catch (error) {
    message.error('操作失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

// 模态框取消
const handleModalCancel = () => {
  modalVisible.value = false
  resetForm()
}

// 删除分类
const doDelete = async (id: string) => {
  if (!id) return

  try {
    const res = await deleteCategoryUsingPost({ id })
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

// 页面初始化
onMounted(() => {
  fetchData()
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

.add-btn {
  display: inline-flex !important;
  align-items: center !important;
  gap: 4px;
}

.add-btn span {
  line-height: 1;
}

.button-col {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}
</style>
