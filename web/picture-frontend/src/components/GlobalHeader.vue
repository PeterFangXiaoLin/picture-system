<template>
  <div id="globalHeader" class="w-full h-[50px] flex items-center">
    <div>
      <router-link to="/">
        <div class="flex items-center mr-8">
          <img class="logo-pic" src="../assets/logo.png" alt="logo" />
          <div class="title-name">智能云图库</div>
        </div>
      </router-link>
    </div>
    <el-menu
      :default-active="activeIndex"
      mode="horizontal"
      :ellipsis="false"
      v-for="item in items"
      :key="item.key"
      @select="handleSelect"
    >
      <el-menu-item :index="item.key">
        <el-icon v-if="item.icon">
          <component :is="item.icon" />
        </el-icon>
        <span>{{ item.label }}</span>
      </el-menu-item>
    </el-menu>
    <div class="ml-auto h-full flex items-center">
      <div v-if="loginUserStore.loginUser.id">
        <el-dropdown trigger="click">
          <el-avatar :size="40" :src="loginUserStore.loginUser.userAvatar ?? defaultAvatar" />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="User" @click="openUserInfo"> 个人信息</el-dropdown-item>
              <el-dropdown-item :icon="SwitchButton" divided @click="doLogout">
                退出登录</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div v-else class="flex items-center whitespace-nowrap">
        <a href="/user/register" class="!hover:text-black c-coolGray">注册</a>
        <span class="mx-3">或</span>
        <a href="/user/login" class="!hover:text-black c-coolGray">登录</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLogoutUsingPost } from '@/api/userController.ts'
import { ElMessage } from 'element-plus'
import { SwitchButton, User } from '@element-plus/icons-vue'
import { routes } from '@/router'
import checkAccess from '@/access/checkAccess'

const loginUserStore = useLoginUserStore()

// 把路由转化成menu
const routeToMenu = (item: any) => {
  return {
    key: item.path,
    label: item.name,
    icon: item.path === '/' ? 'HomeFilled' : undefined,
  }
}

const items = computed(() => {
  return routes
    .filter((item) => {
      if (item.meta?.hideInMenu) {
        return false
      }
      // 根据权限过滤菜单
      return checkAccess(loginUserStore.loginUser, item.meta?.access as string)
    })
    .map(routeToMenu)
})

const activeIndex = ref('/')
const router = useRouter()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const handleSelect = (key: string, keyPath: string[]) => {
  activeIndex.value = key
  router.push({
    path: key,
  })
}

// 打开用户信息弹窗
const openUserInfo = () => {
  router.push('/user/info')
}

// 用户注销
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    ElMessage.success('退出登录成功')
    await router.push('/user/login')
  } else {
    ElMessage.error('退出登录失败，' + res.data.message)
  }
}

router.afterEach((to, from) => {
  activeIndex.value = to.path
})
</script>

<style scoped>
#globalHeader .title-name {
  margin-left: 16px;
  font-size: 18px;
  color: black;
}

a {
  text-decoration: none;
}

.el-menu--horizontal.el-menu {
  border-bottom: none;
}
</style>
