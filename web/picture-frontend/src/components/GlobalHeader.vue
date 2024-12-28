<template>
  <div id="globalHeader">
    <el-row>
      <el-col :span="6">
        <router-link to="/">
          <div class="title-bar">
            <img class="logo-pic" src="../assets/logo.png" alt="logo" />
            <div class="title-name">智能云图库</div>
          </div>
        </router-link>
      </el-col>
      <el-col class="flex-1">

      </el-col>
      <el-col :span="3" class="ml-a">
        <div class="user-login-status">
          <el-button type="primary" tag="a" href="/user/login">登录</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
#globalHeader {
  padding-inline: 20px;
}

#globalHeader .logo-pic {
  height: 48px;
}

#globalHeader .title-bar {
  display: flex;
  align-items: center;
}

#globalHeader .title-name {
  margin-left: 16px;
  font-size: 18px;
  color: black;
}

#globalHeader .user-login-status {
  align-items: center;
}

a {
  text-decoration: none;
}

</style>
