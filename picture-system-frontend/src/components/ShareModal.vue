<template>
  <a-modal v-model:visible="visible" title="分享图片" :footer="false" @cancel="closeModal">
    <h4>复制分享链接</h4>
    <a-typography-link copyable>
      {{ link }}
    </a-typography-link>
    <div style="margin-bottom: 16px"></div>
    <h4>手机扫码查看</h4>
    <a-qrcode :value="link" />
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue'

/**
 * 定义组件属性
 */
interface Props {
  link: string
}

/**
 * 定义属性默认值
 */
const props = withDefaults(defineProps<Props>(), {
  link: () => '',
})

// 是否可见
const visible = ref<boolean>(false)

// 打开窗口
const openModal = () => {
  visible.value = true
}

// 关闭窗口
const closeModal = () => {
  visible.value = false
}

// 暴露打开窗口给父组件
defineExpose({
  openModal,
})
</script>

<style scoped></style>
