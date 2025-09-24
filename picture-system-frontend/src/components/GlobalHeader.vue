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
          <div v-if="loginUserStore.loginUser.id">
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
                  <a-menu-divider />
                  <a-menu-item key="logout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { UserOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import routes from '@/router/routes.ts'
import checkAccess from '@/access/checkAccess.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLogoutUsingPost } from '@/api/userController'

const loginUserStore = useLoginUserStore()

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
</style>
