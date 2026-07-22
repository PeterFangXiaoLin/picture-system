<template>
  <div id="global-header">
    <div class="header-shell">
      <RouterLink to="/" class="brand" aria-label="返回 AI 云图库首页">
        <img class="logo" src="../assets/logo.png" alt="AI 云图库" />
        <span class="brand-title">AI云图库</span>
      </RouterLink>

      <nav class="main-navigation" aria-label="主导航">
        <a-menu
          v-model:selectedKeys="current"
          class="main-menu"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </nav>

      <div class="header-actions">
        <template v-if="loginUserStore.loginUser.id">
          <a-tooltip title="通知">
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
          </a-tooltip>

          <a-divider type="vertical" class="action-divider" />

          <a-dropdown placement="bottomRight" :trigger="['click']">
            <button type="button" class="profile-trigger" aria-label="打开用户菜单">
              <a-avatar
                :size="36"
                :src="avatarSrc"
                :style="avatarStyle"
                class="profile-avatar"
              >
                {{ avatarText }}
              </a-avatar>
              <span class="profile-name">{{ loginUserStore.loginUser.userName || '用户' }}</span>
              <DownOutlined class="profile-arrow" />
            </button>
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
        </template>
        <a-button v-else type="primary" href="/user/login" class="login-button">登录</a-button>
      </div>
    </div>
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
import {
  BellOutlined,
  DownOutlined,
  UserOutlined,
  LogoutOutlined,
  HistoryOutlined,
} from '@ant-design/icons-vue'
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
  background: transparent;
}

.header-shell {
  display: flex;
  align-items: center;
  height: 100%;
  width: 100%;
  padding: 0 28px;
  gap: 0;
}

.brand {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  height: 44px;
  margin-right: 24px;
  color: #111827;
  text-decoration: none;
  white-space: nowrap;
}

.logo {
  width: 42px;
  height: 42px;
  object-fit: contain;
}

.brand-title {
  margin-left: 10px;
  font-size: 19px;
  font-weight: 650;
  line-height: 1;
  letter-spacing: -0.2px;
}

.main-navigation {
  display: flex;
  flex: 1 1 auto;
  align-items: center;
  align-self: stretch;
  min-width: 0;
  overflow: hidden;
}

.main-menu {
  flex: 1 1 auto;
  min-width: 0;
  border-bottom: 0;
  background: transparent;
}

.header-actions {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  align-self: stretch;
  gap: 10px;
  margin-left: 18px;
}

.notification-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  padding: 0;
  color: #374151;
  font-size: 20px;
  line-height: 1;
}

.notification-button:hover {
  color: #1677ff;
  background: #f3f6fa;
}

.action-divider {
  top: 0;
  height: 22px;
  margin: 0 2px;
  border-color: #e5e7eb;
}

.profile-trigger {
  display: inline-flex;
  align-items: center;
  min-height: 44px;
  padding: 4px 8px 4px 4px;
  color: #1f2937;
  font: inherit;
  background: transparent;
  border: 0;
  border-radius: 22px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.profile-trigger:hover,
.profile-trigger:focus-visible {
  background: #f3f6fa;
  outline: none;
}

.profile-avatar {
  flex: 0 0 auto;
  vertical-align: middle;
}

.profile-name {
  max-width: 96px;
  margin-left: 9px;
  overflow: hidden;
  font-size: 14px;
  font-weight: 500;
  line-height: 20px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-arrow {
  margin-left: 6px;
  color: #9ca3af;
  font-size: 11px;
}

.login-button {
  min-width: 72px;
  border-radius: 8px;
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

@media (max-width: 1280px) {
  .header-shell {
    padding: 0 20px;
  }

  .brand {
    margin-right: 12px;
  }

  .profile-name,
  .profile-arrow {
    display: none;
  }

  .profile-trigger {
    padding-right: 4px;
  }
}

@media (max-width: 900px) {
  .header-shell {
    padding: 0 14px;
  }

  .brand-title {
    display: none;
  }

  .brand {
    margin-right: 8px;
  }

  .header-actions {
    margin-left: 8px;
  }
}
</style>
