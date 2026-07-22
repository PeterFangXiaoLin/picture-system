<template>
  <div id="global-header">
    <a-row :wrap="false">
      <a-col flex="200px">
        <RouterLink to="/">
          <h1 class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <span class="title">AI云图库</span>
          </h1>
        </RouterLink>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <a-col flex="none">
        <div class="user-login-status">
          <a-space v-if="loginUserStore.loginUser.id" :size="16">
            <a-badge :count="unreadCount" :overflow-count="99">
              <a-button
                type="text"
                shape="circle"
                class="notification-button"
                aria-label="通知"
                @click="openNotificationDrawer"
              >
                <BellOutlined />
              </a-button>
            </a-badge>
            <a-dropdown placement="bottomRight">
              <a-avatar
                :size="36"
                :src="avatarSrc"
                :style="avatarStyle"
                class="cursor-pointer"
              >
                {{ avatarText }}
              </a-avatar>
              <template #overlay>
                <a-menu @click="handleMenuClick">
                  <a-menu-item key="profile">
                    <UserOutlined />
                    个人中心
                  </a-menu-item>
                  <a-menu-item>
                    <router-link to="/my_space">
                      <UserOutlined />
                      我的空间
                    </router-link>
                  </a-menu-item>
                  <a-menu-item>
                    <router-link to="/out_painting/task">
                      <HistoryOutlined />
                      扩图任务
                    </router-link>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
    <a-drawer v-model:open="notificationDrawerOpen" title="通知中心" width="420">
      <template #extra>
        <a-button type="link" :disabled="unreadCount === 0" @click="markAllRead">
          一键全部已读
        </a-button>
      </template>
      <a-spin :spinning="notificationLoading">
        <a-empty v-if="notifications.length === 0" description="暂无通知" />
        <a-list v-else item-layout="vertical" :data-source="notifications">
          <template #renderItem="{ item }">
            <a-list-item
              class="notification-item"
              :class="{ unread: item.isRead === 0 }"
              @click="readNotification(item)"
            >
              <a-list-item-meta>
                <template #title>
                  <a-space>
                    <span>{{ item.title }}</span>
                    <a-tag v-if="item.isRead === 0" color="blue">未读</a-tag>
                    <a-tag
                      v-if="item.type === 'SPACE_INVITE' && item.actionStatus != null"
                      :color="getActionStatusMeta(item.actionStatus).color"
                    >
                      {{ getActionStatusMeta(item.actionStatus).label }}
                    </a-tag>
                  </a-space>
                </template>
                <template #description>
                  {{ dayjs(item.createTime).format('YYYY-MM-DD HH:mm') }}
                </template>
              </a-list-item-meta>
              <div class="notification-content">{{ item.content }}</div>
              <template v-if="isPendingInviteNotification(item)" #actions>
                <a-button
                  type="primary"
                  size="small"
                  :loading="reviewingNotificationId === item.id"
                  @click.stop="reviewInvite(item, 1)"
                >
                  接受
                </a-button>
                <a-button
                  size="small"
                  danger
                  :loading="reviewingNotificationId === item.id"
                  @click.stop="reviewInvite(item, 2)"
                >
                  拒绝
                </a-button>
              </template>
            </a-list-item>
          </template>
        </a-list>
      </a-spin>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeUnmount, watch } from 'vue'
import { useRouter } from 'vue-router'
import { BellOutlined, UserOutlined, LogoutOutlined, HistoryOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import routes from '@/router/routes.ts'
import checkAccess from '@/access/checkAccess.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLogoutUsingPost } from '@/api/userController'
import {
  getUnreadNotificationCountUsingGet,
  listMyNotificationUsingGet,
  markAllNotificationReadUsingPost,
  markNotificationReadUsingPost,
} from '@/api/userNotificationController'
import { reviewSpaceUserInviteUsingPost } from '@/api/spaceUserController'
import dayjs from 'dayjs'

const loginUserStore = useLoginUserStore()

// 通知中心的展示状态、通知数据和未读角标
const notificationDrawerOpen = ref(false)
const notificationLoading = ref(false)
const notifications = ref<API.UserNotification[]>([])
const unreadCount = ref(0)
const reviewingNotificationId = ref<string>()

// 保存轮询定时器，登录用户变化或组件卸载时需要主动清理
let notificationTimer: number | undefined

// 只请求未读数量，供右上角角标首次加载和定时轮询使用
const fetchUnreadCount = async () => {
  if (!loginUserStore.loginUser.id) return
  const res = await getUnreadNotificationCountUsingGet()
  if (res.data.code === 0) {
    unreadCount.value = Number(res.data.data ?? 0)
  }
}

// 请求最近的通知明细；通知抽屉打开时再加载，避免页面初始化时多查一次列表
const fetchNotifications = async () => {
  notificationLoading.value = true
  try {
    const res = await listMyNotificationUsingGet()
    if (res.data.code === 0) {
      notifications.value = res.data.data ?? []
    } else {
      message.error('加载通知失败，' + res.data.message)
    }
  } finally {
    notificationLoading.value = false
  }
}

const openNotificationDrawer = async () => {
  notificationDrawerOpen.value = true
  // 打开抽屉时并行刷新通知列表和未读数量，确保展示的是最新状态
  await Promise.all([fetchNotifications(), fetchUnreadCount()])
}

const readNotification = async (notification: API.UserNotification) => {
  // 空间邀请需要通过接受或拒绝结束，点击通知本身不直接改变邀请状态
  if (notification.isRead !== 0 || !notification.id || notification.type === 'SPACE_INVITE') return
  const res = await markNotificationReadUsingPost({ id: notification.id })
  if (res.data.code === 0) {
    // 接口成功后直接更新本地状态，无需重新请求整个通知列表
    notification.isRead = 1
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
}

const markAllRead = async () => {
  const res = await markAllNotificationReadUsingPost()
  if (res.data.code === 0) {
    // 一键已读只修改阅读状态，不影响待确认邀请的 actionStatus 和操作按钮
    notifications.value.forEach((item) => (item.isRead = 1))
    unreadCount.value = 0
    message.success('全部通知已读')
  } else {
    message.error('操作失败，' + res.data.message)
  }
}

const isPendingInviteNotification = (notification: API.UserNotification) =>
  notification.type === 'SPACE_INVITE' && notification.actionStatus === 0 && !!notification.relatedId

const getActionStatusMeta = (status: number) => {
  if (status === 1) return { label: '已接受', color: 'success' }
  if (status === 2) return { label: '已拒绝', color: 'error' }
  return { label: '待确认', color: 'processing' }
}

const reviewInvite = async (notification: API.UserNotification, inviteStatus: number) => {
  if (!notification.relatedId) return
  reviewingNotificationId.value = notification.id
  try {
    const res = await reviewSpaceUserInviteUsingPost({
      id: notification.relatedId,
      inviteStatus,
    })
    if (res.data.code === 0) {
      message.success(inviteStatus === 1 ? '已接受空间邀请' : '已拒绝空间邀请')
      // 通知侧边栏重新查询团队空间，接受邀请后可立即看到新加入的空间
      window.dispatchEvent(new Event('space-membership-changed'))
      // 邀请处理会同步修改通知业务状态，并为管理员产生新通知，因此重新查询列表和角标
      await Promise.all([fetchNotifications(), fetchUnreadCount()])
    } else {
      message.error('处理邀请失败，' + res.data.message)
    }
  } finally {
    reviewingNotificationId.value = undefined
  }
}

// 登录成功后立即获取一次未读数，并每 30 秒轮询；切换用户时先销毁旧定时器
watch(
  () => loginUserStore.loginUser.id,
  (userId) => {
    if (notificationTimer) window.clearInterval(notificationTimer)
    if (userId) {
      fetchUnreadCount()
      notificationTimer = window.setInterval(fetchUnreadCount, 30_000)
    } else {
      unreadCount.value = 0
      notifications.value = []
    }
  },
  { immediate: true },
)

onBeforeUnmount(() => {
  // 组件销毁时停止轮询，避免产生无效请求和定时器泄漏
  if (notificationTimer) window.clearInterval(notificationTimer)
})

// 头像颜色列表
const avatarColors = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#87d068', '#ff7875', '#ffc107', '#52c41a']

// 计算头像图片链接
const avatarSrc = computed(() => {
  return loginUserStore.loginUser.userAvatar || undefined
})

// 计算头像文字（用户名首字母）
const avatarText = computed(() => {
  if (loginUserStore.loginUser.userAvatar) {
    return ''
  }
  const userName = loginUserStore.loginUser.userName || '无'
  return userName.charAt(0).toUpperCase()
})

// 计算头像样式（文字头像的背景色）
const avatarStyle = computed(() => {
  if (loginUserStore.loginUser.userAvatar) {
    return {}
  }
  // 根据用户ID或用户名生成固定的颜色索引
  const userId = loginUserStore.loginUser.id || loginUserStore.loginUser.userName || 'default'
  let colorIndex = 0
  if (userId && userId !== 'default') {
    // 简单的哈希函数，确保同一用户总是得到相同的颜色
    colorIndex = userId.split('').reduce((acc, char) => {
      return acc + char.charCodeAt(0)
    }, 0) % avatarColors.length
  }
  return {
    backgroundColor: avatarColors[colorIndex],
    color: '#fff',
    fontSize: '14px',
    fontWeight: 'bold'
  }
})

const items = computed(() => {
  return routes
    .filter((item) => {
      if (item?.meta?.show === false) {
        return false
      }
      return checkAccess(loginUserStore.loginUser, item?.meta?.authCheck)
    })
    .map((item) => {
      return {
        key: item.path,
        label: item.name,
        title: item.name,
        icon: item?.meta?.icon,
      }
    })
})

const router = useRouter()
const current = ref<string[]>(['/'])

router.afterEach((to, from, next) => {
  current.value = [to.path]
})

const doMenuClick = (e: { key: string }) => {
  router.push({
    path: e.key,
  })
}

// 处理用户下拉菜单点击事件
const handleMenuClick = async (e: { key: string }) => {
  if (e.key === 'logout') {
    await doLogout()
  } else if (e.key === 'profile') {
    // 跳转到个人中心（暂时使用首页，后续可以改为实际的个人中心页面）
    router.push('/')
  }
}

// 用户注销
const doLogout = async () => {
  try {
    const res = await userLogoutUsingPost()
    console.log(res)
    if (res.data.code === 0) {
      loginUserStore.setLoginUser({
        userName: '未登录',
      })
      message.success('退出登录成功')
      await router.push('/user/login')
    } else {
      message.error('退出登录失败，' + res.data.message)
    }
  } catch (error: any) {
    console.error('退出登录失败:', error)
    message.error('网络错误，请稍后重试')
  }
}
</script>

<style scoped>
#global-header {
  height: 64px;
  width: 100%;
  padding-left: 24px;
  padding-right: 24px;
}

.title-bar {
  display: flex;
  align-items: center;
  height: 100%;
}

.title {
  color: black;
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
  line-height: 1;
}

.logo {
  height: 48px;
}

.notification-button {
  font-size: 20px;
}

.notification-item {
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item.unread {
  background: #f0f7ff;
}

.notification-item:hover {
  background: #f5f5f5;
}

.notification-content {
  color: #595959;
  line-height: 1.6;
}
</style>
