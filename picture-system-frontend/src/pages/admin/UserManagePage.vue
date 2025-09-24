<template>
  <div id="user-manage-page" class="p-6 max-w-7xl mx-auto">
    <!-- 搜索表单 -->
    <div class="bg-white p-6 rounded-lg shadow-sm mb-4">
      <a-form :model="searchParams" @finish="doSearch">
        <!-- 第一行：四个搜索条件 -->
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item label="用户账号">
              <a-input
                v-model:value="searchParams.userAccount"
                placeholder="请输入用户账号"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="用户昵称">
              <a-input
                v-model:value="searchParams.userName"
                placeholder="请输入用户昵称"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="用户角色">
              <a-select
                v-model:value="searchParams.userRole"
                placeholder="请选择用户角色"
                allowClear
              >
                <a-select-option value="user">用户</a-select-option>
                <a-select-option value="admin">管理员</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="排序字段">
              <a-select v-model:value="searchParams.sortField" placeholder="排序字段" allowClear>
                <a-select-option value="createTime">创建时间</a-select-option>
                <a-select-option value="userAccount">账号</a-select-option>
                <a-select-option value="userName">昵称</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 第二行：排序顺序和操作按钮 -->
        <a-row :gutter="16" justify="space-between">
          <a-col :span="6">
            <a-form-item label="排序顺序">
              <a-select v-model:value="searchParams.sortOrder" placeholder="排序顺序" allowClear>
                <a-select-option value="desc">降序</a-select-option>
                <a-select-option value="asc">升序</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="18" class="button-col">
            <a-form-item>
              <a-space size="middle">
                <a-button type="primary" html-type="submit" :loading="loading"> 搜索 </a-button>
                <a-button @click="doReset">重置</a-button>
                <a-button type="primary" @click="showAddModal" class="add-user-btn">
                  <PlusOutlined />
                  <span>新增用户</span>
                </a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 用户表格 -->
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
          <template v-if="column.key === 'userAvatar'">
            <a-avatar :src="record.userAvatar" size="small">
              <template #icon>
                <UserOutlined />
              </template>
            </a-avatar>
          </template>
          <template v-if="column.key === 'userRole'">
            <a-tag :color="record.userRole === 'admin' ? 'red' : 'blue'">
              {{ record.userRole === 'admin' ? '管理员' : '用户' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ record.createTime ? dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="showEditModal(record)" class="action-btn">
                <EditOutlined />
                <span>编辑</span>
              </a-button>
              <a-popconfirm
                title="确定要删除此用户吗？"
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

    <!-- 新增/编辑用户模态框 -->
    <a-modal
      :title="isEdit ? '编辑用户' : '新增用户'"
      :open="modalVisible"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      :confirm-loading="submitLoading"
      width="600px"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
        <a-form-item label="用户账号" name="userAccount" v-if="!isEdit">
          <a-input v-model:value="formData.userAccount" placeholder="请输入用户账号" />
        </a-form-item>
        <a-form-item label="用户昵称" name="userName">
          <a-input v-model:value="formData.userName" placeholder="请输入用户昵称" />
        </a-form-item>
        <a-form-item label="用户头像" name="userAvatar">
          <a-input v-model:value="formData.userAvatar" placeholder="请输入头像URL" />
        </a-form-item>
        <a-form-item label="用户简介" name="userProfile">
          <a-textarea v-model:value="formData.userProfile" placeholder="请输入用户简介" :rows="3" />
        </a-form-item>
        <a-form-item label="用户角色" name="userRole">
          <a-select v-model:value="formData.userRole" placeholder="请选择用户角色">
            <a-select-option value="user">用户</a-select-option>
            <a-select-option value="admin">管理员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, EditOutlined, DeleteOutlined, UserOutlined } from '@ant-design/icons-vue'
import {
  listUserVoByPageUsingPost,
  addUserUsingPost,
  updateUserUsingPost,
  deleteUserUsingPost,
} from '@/api/userController'
import dayjs from 'dayjs'

// 数据相关
const dataList = ref<API.UserVO[]>([])
const total = ref(0)
const loading = ref(false)

// 搜索参数
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 模态框相关
const modalVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive<API.UserAddRequest & API.UserUpdateRequest>({
  userAccount: '',
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: 'user',
})

// 表单验证规则
const formRules = {
  userAccount: [
    { required: true, message: '请输入用户账号', trigger: 'blur' },
    { min: 4, message: '用户账号至少4个字符', trigger: 'blur' },
  ],
  userName: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
  userRole: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
}

// 表格列配置
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 100,
    ellipsis: true,
  },
  {
    title: '头像',
    key: 'userAvatar',
    width: 80,
    align: 'center',
  },
  {
    title: '用户账号',
    dataIndex: 'userAccount',
    key: 'userAccount',
    width: 120,
  },
  {
    title: '用户昵称',
    dataIndex: 'userName',
    key: 'userName',
    width: 120,
  },
  {
    title: '用户简介',
    dataIndex: 'userProfile',
    key: 'userProfile',
    ellipsis: true,
  },
  {
    title: '用户角色',
    key: 'userRole',
    width: 100,
    align: 'center',
  },
  {
    title: '创建时间',
    key: 'createTime',
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
    const res = await listUserVoByPageUsingPost(searchParams)
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
    userAccount: undefined,
    userName: undefined,
    userProfile: undefined,
    userRole: undefined,
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
const showEditModal = (record: API.UserVO) => {
  isEdit.value = true
  modalVisible.value = true
  Object.assign(formData, {
    id: record.id,
    userName: record.userName,
    userAvatar: record.userAvatar,
    userProfile: record.userProfile,
    userRole: record.userRole,
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: undefined,
    userAccount: '',
    userName: '',
    userAvatar: '',
    userProfile: '',
    userRole: 'user',
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
      // 编辑用户
      res = await updateUserUsingPost({
        id: formData.id,
        userName: formData.userName,
        userAvatar: formData.userAvatar,
        userProfile: formData.userProfile,
        userRole: formData.userRole,
      })
    } else {
      // 新增用户
      res = await addUserUsingPost({
        userAccount: formData.userAccount,
        userName: formData.userName,
        userAvatar: formData.userAvatar,
        userProfile: formData.userProfile,
        userRole: formData.userRole,
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

// 删除用户
const doDelete = async (id: string) => {
  if (!id) return

  try {
    const res = await deleteUserUsingPost({ id })
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
/* TailwindCSS 已提供大部分样式 */
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

/* 操作按钮样式 */
.action-btn {
  display: inline-flex !important;
  align-items: center !important;
  gap: 4px;
  padding: 4px 8px !important;
}

.action-btn span {
  line-height: 1;
}

/* 新增用户按钮图标和文字对齐 */
.add-user-btn {
  display: inline-flex !important;
  align-items: center !important;
  gap: 4px;
}

.add-user-btn span {
  line-height: 1;
}

/* 第二行按钮列对齐 */
.button-col {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}
</style>
