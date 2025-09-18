<template>
  <div id="basic-layout">
    <a-layout class="min-h-screen">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="bg-linear-to-r from-[#efefef] to-[#fff]">
        <router-view />
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
</script>

<style scoped>
#basic-layout .header {
  color: unset;
  padding-inline: 20px;
  margin-bottom: 16px;
  background: white;
}
</style>
