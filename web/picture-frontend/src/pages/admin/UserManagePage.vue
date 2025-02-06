<template>
  <div class="user-manage-container">
    <!-- 搜索表单 -->
    <el-card shadow="never">
      <div>
        <el-form :inline="true" :model="searchParams" class="search-form">
          <el-form-item label="账号">
            <el-input v-model="searchParams.userAccount" placeholder="输入账号" clearable />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="searchParams.userName" placeholder="输入用户名" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="doSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card shadow="never">
      <!-- 用户表格 -->
      <el-table
        :data="dataList"
        style="width: 100%"
        v-loading="loading"
        :header-cell-style="{ 'background-color': '#ecf8fe', color: '#4986EA' }"
      >
        <el-table-column prop="id" label="id" width="200" align="center"/>
        <el-table-column prop="userAccount" label="账号" width="120" align="center" />
        <el-table-column prop="userName" label="用户名" width="120" align="center"/>
        <el-table-column label="头像" width="120" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.userAvatar"
              :preview-src-list="[row.userAvatar]"
              fit="cover"
              style="width: 80px; height: 80px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="userProfile" label="简介" align="center"/>
        <el-table-column label="用户角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.userRole === 'admin' ? 'success' : 'info'">
              {{ row.userRole === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row.id)"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="searchParams.current"
          v-model:page-size="searchParams.pageSize"
          background
          :total="2"
          :page-sizes="[10, 20, 30]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageUserVoUsingPost, deleteUserUsingPost } from '@/api/userController'
import dayjs from 'dayjs'

const loading = ref(false)
const dataList = ref<API.UserRespVO[]>([])
const total = ref(0)

// 搜索参数
const searchParams = reactive({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'ascend',
  userAccount: '',
  userName: '',
})

// 获取用户列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await pageUserVoUsingPost(searchParams)
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records ?? []
      total.value = res.data.data.total ?? 0
      console.log('total.value -->', total.value)
    } else {
      ElMessage.error(res.data.message || '获取数据失败')
    }
  } catch (error) {
    ElMessage.error('获取数据失败：' + error)
  } finally {
    loading.value = false
  }
}

// 搜索
const doSearch = () => {
  searchParams.current = 1
  fetchData()
}

// 处理页码变化
const handleCurrentChange = (current: number) => {
  searchParams.current = current
  fetchData()
}

// 处理每页条数变化
const handleSizeChange = (size: number) => {
  searchParams.pageSize = size
  searchParams.current = 1
  fetchData()
}

// 删除用户
const handleDelete = async (id: string) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning',
    })
    const res = await deleteUserUsingPost({ id })
    if (res.data.code === 0) {
      ElMessage.success('删除成功')
      fetchData()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error)
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-manage-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
