<template>
  <a-config-provider :locale="zhCN">
    <BasicLayout />
  </a-config-provider>
</template>

<script setup lang="ts">
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import BasicLayout from '@/layouts/BasicLayout.vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

dayjs.locale('zh-cn')

const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()

</script>

<style scoped>

</style>
