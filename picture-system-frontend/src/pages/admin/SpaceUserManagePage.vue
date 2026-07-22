<template>
  <div id="spaceManagePage">
    <a-flex justify="space-between">
      <h2>空间管理</h2>
      <a-space>
        <a-button type="primary" href="/add_space" target="_blank">+ 创建空间</a-button>
        <a-button type="primary" ghost href="/space_analyze?queryPublic=1" target="_blank">
          分析公共图库
        </a-button>
        <a-button type="primary" ghost href="/space_analyze?queryAll=1" target="_blank">
          分析全空间
        </a-button>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <!-- 添加成员表单-->
    <a-form layout="inline" :model="formData" @finish="handleSubmit">
      <a-form-item label="用户 id" name="userId">
        <a-input v-model:value="formData.userId" placeholder="请输入用户 id" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">添加用户</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 16px" />
    <a-tabs v-model:active-key="activeStatus">
      <a-tab-pane :key="1" :tab="`正式成员 (${acceptedList.length})`" />
      <a-tab-pane :key="0" :tab="`待确认 (${pendingList.length})`" />
      <a-tab-pane :key="2" :tab="`已拒绝 (${rejectedList.length})`" />
    </a-tabs>
    <a-alert
      v-if="activeStatus === 0"
      message="以下用户尚未接受邀请，不拥有该空间的任何成员权限。"
      type="info"
      show-icon
      class="status-tip"
    />
    <a-alert
      v-else-if="activeStatus === 2"
      message="以下用户已拒绝邀请，可以重新发送邀请。"
      type="warning"
      show-icon
      class="status-tip"
    />
    <!-- 按邀请状态区分展示，待确认用户不会混入正式成员 -->
    <a-table row-key="id" :columns="columns" :data-source="currentList">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userInfo'">
          <a-space>
            <a-avatar :src="record.user?.userAvatar" />
            {{ record.user?.userName }}
          </a-space>
        </template>
        <template v-if="column.dataIndex === 'spaceRole' && record.inviteStatus === 1">
          <a-select
            v-model:value="record.spaceRole"
            :options="SPACE_ROLE_OPTIONS"
            @change="editSpaceRole($event, record)"
          />
        </template>
        <template v-else-if="column.dataIndex === 'spaceRole'">
          {{ getRoleLabel(record.spaceRole) }}
        </template>
        <template v-else-if="column.dataIndex === 'inviteStatus'">
          <a-tag :color="getInviteStatusMeta(record.inviteStatus).color">
            {{ getInviteStatusMeta(record.inviteStatus).label }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button
              v-if="record.inviteStatus === 2"
              type="link"
              @click="resendInvite(record)"
            >
              重新邀请
            </a-button>
            <a-button type="link" danger @click="doDelete(record.id)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { SPACE_ROLE_OPTIONS } from '@/constants/space'
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  addSpaceUserUsingPost,
  deleteSpaceUserUsingPost,
  editSpaceUserUsingPost,
  listSpaceUserUsingPost,
} from '@/api/spaceUserController.ts'

interface Props {
  id: string
}

const props = defineProps<Props>()

const columns = [
  {
    title: '用户',
    dataIndex: 'userInfo',
  },
  {
    title: '角色',
    dataIndex: 'spaceRole',
  },
  {
    title: '邀请状态',
    dataIndex: 'inviteStatus',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 定义数据
const dataList = ref<API.SpaceUserVO[]>([])
const activeStatus = ref(1)
const acceptedList = computed(() => dataList.value.filter((item) => item.inviteStatus === 1))
const pendingList = computed(() => dataList.value.filter((item) => item.inviteStatus === 0))
const rejectedList = computed(() => dataList.value.filter((item) => item.inviteStatus === 2))
const currentList = computed(() => {
  if (activeStatus.value === 0) return pendingList.value
  if (activeStatus.value === 2) return rejectedList.value
  return acceptedList.value
})

const INVITE_STATUS_META = {
  0: { label: '待确认', color: 'processing' },
  1: { label: '已加入', color: 'success' },
  2: { label: '已拒绝', color: 'error' },
}

const getInviteStatusMeta = (status?: number) =>
  INVITE_STATUS_META[(status ?? 0) as keyof typeof INVITE_STATUS_META]

const getRoleLabel = (role?: string) =>
  SPACE_ROLE_OPTIONS.find((option) => option.value === role)?.label ?? role ?? '-'

// 获取数据
const fetchData = async () => {
  const spaceId = props.id
  if (!spaceId) {
    return
  }
  const res = await listSpaceUserUsingPost({
    spaceId,
  })
  if (res.data.code === 0) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 页面加载时获取数据，请求一次
onMounted(() => {
  fetchData()
})

// 添加用户
const formData = reactive<API.SpaceUserAddRequest>({})

const handleSubmit = async () => {
  const spaceId = props.id
  if (!spaceId) {
    return
  }
  const res = await addSpaceUserUsingPost({
    spaceId,
    ...formData,
  })
  if (res.data.code === 0) {
    message.success('邀请已发送')
    activeStatus.value = 0
    // 刷新数据
    await fetchData()
  } else {
    message.error('添加失败，' + res.data.message)
  }
}

const resendInvite = async (record: API.SpaceUserVO) => {
  const res = await addSpaceUserUsingPost({
    spaceId: props.id,
    userId: record.userId,
    spaceRole: record.spaceRole,
  })
  if (res.data.code === 0) {
    message.success('邀请已重新发送')
    activeStatus.value = 0
    await fetchData()
  } else {
    message.error('重新邀请失败，' + res.data.message)
  }
}

// 编辑空间角色
const editSpaceRole = async (value: string, record: API.SpaceUserVO) => {
  const res = await editSpaceUserUsingPost({
    id: record.id,
    spaceRole: value,
  })
  if (res.data.code === 0) {
    message.success('修改成功')
  } else {
    message.error('修改失败，' + res.data.message)
  }
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteSpaceUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    await fetchData()
  } else {
    message.error('删除失败')
  }
}
</script>

<style scoped>
.status-tip {
  margin-bottom: 16px;
}
</style>
