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
import { useRouter } from 'vue-router'
import { UserOutlined, PictureOutlined } from '@ant-design/icons-vue'
import { h, ref } from 'vue'
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
]

const router = useRouter()

// 当前选中菜单
const current = ref<string[]>([])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, failure) => {
  current.value = [to.path]
})

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
