<script setup lang="ts">
import TheWelcome from '../components/TheWelcome.vue'
import {computed, reactive, unref} from "vue";

const obj = reactive({
  arr: [1,2]
})

const computedObj = computed(() => {
  return {
    ...obj.arr
  }
})

const handleClick = () => {
  const t = unref(computedObj)
  console.log(t)
}

</script>

<template>
  <main>
    <TheWelcome />
  </main>
  <el-button @click="obj.arr[0]++">增加</el-button>
  {{computedObj}}
  <el-button @click="handleClick">修改</el-button>
</template>
