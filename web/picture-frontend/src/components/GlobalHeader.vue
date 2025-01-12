<template>
  <div
    id="globalHeader"
    class="w-full h-[50px] px-6 flex m-auto w-full items-center md:flex max-w-[1200px]"
  >
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
      <el-button href="/user/login" link class="!hover:text-black">登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const items = ref([
  {
    key: '/',
    label: '首页',
    icon: 'HomeFilled',
  },
  {
    key: '/about',
    label: '关于',
  },
])

const activeIndex = ref('/')
const router = useRouter()

const handleSelect = (key: string, keyPath: string[]) => {
  activeIndex.value = key
  router.push({
    path: key,
  })
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
</style>
