<template>
  <div id="basicLayout">
    <el-container class="min-h-100vh">
      <el-header class="header">
        <GlobalHeader />
      </el-header>
      <el-main class="content">
        <router-view />
      </el-main>
      <el-footer class="footer">Footer</el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
</script>

<style scoped>
#basicLayout .header {
  padding-inline: 20px;
  background: white;
  color: unset;
  margin-bottom: 16px;
  margin-left: 24px;
  margin-right: 24px;
}

#basicLayout .content {
  padding: 20px;
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
}

#basicLayout .footer {
  background: #efefef;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>
