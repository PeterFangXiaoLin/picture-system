<template>
  <div id="global-sider">
    <a-layout-sider
      v-if="loginUserStore.loginUser.id"
      width="200"
      breakpoint="lg"
      collapsed-width="0"
      :style="{ overflow: 'auto'  }"
    >
      <a-menu
        v-model:selectedKeys="current"
        :items="menuItems"
        mode="inline"
        @click="doMenuClick"
      />
    </a-layout-sider>
  </div>
</template>

<script setup lang="ts">
// 菜单列表
import { useRouter, useRoute } from 'vue-router'
import { UserOutlined, PictureOutlined, HistoryOutlined } from '@ant-design/icons-vue'
import { h, ref, watch } from 'vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const loginUserStore = useLoginUserStore()

const menuItems = [
  {
    key: '/',
    label: '公共图库',
    icon: () => h(PictureOutlined),
  },
  {
    key: '/my_space',
    label: '我的空间',
    icon: () => h(UserOutlined),
  },
  {
    key: '/out_painting/task',
    label: '扩图任务',
    icon: () => h(HistoryOutlined),
  },
]

const router = useRouter()
const route = useRoute()

/** 将实际路由映射到侧边栏菜单 key */
const getActiveMenuKey = (path: string) => {
  if (path.startsWith('/space/') || path === '/add_space') {
    return '/my_space'
  }
  if (path.startsWith('/out_painting')) {
    return '/out_painting/task'
  }
  const matched = menuItems.find((item) => item.key === path)
  return matched ? matched.key : path
}

// 当前选中菜单
const current = ref<string[]>([getActiveMenuKey(route.path)])

watch(
  () => route.path,
  (path) => {
    current.value = [getActiveMenuKey(path)]
  },
  { immediate: true },
)

// 路由跳转事件
const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}
</script>

<style scoped>
#global-sider .ant-layout-sider {
  background: none;
}
</style>
