<template>
  <div id="basic-layout">
    <a-layout style="min-height: 100vh">
      <!-- 只在非身份认证页面显示导航栏 -->
      <a-layout-header v-if="!isAuthPage" class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout>
        <GlobalSider class="sider" />
        <a-layout-content :class="isAuthPage ? '' : 'content'">
          <router-view />
        </a-layout-content>
      </a-layout>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalSider from '@/components/GlobalSider.vue'

const route = useRoute()

// 定义身份认证相关的路由路径
const authRoutes = ['/user/login', '/user/register']

// 检查当前路由是否为身份认证页面
const isAuthPage = computed(() => {
  return authRoutes.includes(route.path)
})
</script>

<style scoped>
#basic-layout .header {
  position: sticky;
  top: 0;
  z-index: 100;
  height: 64px;
  padding-inline: 0;
  line-height: normal;
  background: rgba(255, 255, 255, 0.96);
  border-bottom: 1px solid #edf0f3;
  box-shadow: 0 1px 3px rgb(15 23 42 / 4%);
  backdrop-filter: blur(12px);
  color: unset;
}

#basic-layout .content {
  padding: 3rem 6rem;
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
}

#basic-layout .sider {
  background: #fff;
  padding-top: 20px;
  border-right: 0.5px solid #eee;
}

#basic-layout :deep(.ant-menu-root) {
  border-bottom: none !important;
  border-inline-end: none !important;
}
</style>
