<template>
  <div id="global-header" class="">
    <a-row :wrap="false">
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <div class="title">AI云图库</div>
          </div>
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
      <a-col flex="120px">
        <div class="user-login-status">
          <a-button type="primary" href="/user/login">登录</a-button>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

import { useRouter } from 'vue-router'
import routes from '@/router/routes.ts'
import checkAccess from '@/access/checkAccess.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const loginUserStore = useLoginUserStore()

const items = computed(() => {
  return routes
    .filter((item) => {
      if (item?.meta?.show === false) {
        return false
      }
      return checkAccess(loginUserStore.loginUser, item?.meta?.authCheck);
    })
    .map((item) => {
      return {
        key: item.path,
        label: item.name,
        title: item.name,
        icon: item?.meta?.icon
      }
    })
})

const router = useRouter()
const current = ref<string[]>([''])

router.afterEach((to, from, next) => {
  current.value = [to.path]
})

const doMenuClick = (e: { key: string }) => {
  router.push({
    path: e.key,
  })
}

</script>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>
