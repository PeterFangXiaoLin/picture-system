<template>
  <div id="basic-layout">
    <a-layout class="min-h-screen">
      <!-- 只在非身份认证页面显示导航栏 -->
      <a-layout-header v-if="!isAuthPage" class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content :class="isAuthPage ? '' : 'bg-linear-to-r from-[#efefef] to-[#fff]'">
        <router-view />
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'

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
  color: unset;
  margin-bottom: 16px;
  background: white;
  display: flex;
  align-items: center;
}
</style>
