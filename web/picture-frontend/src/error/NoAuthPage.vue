<template>
  <div class="errPage-container">
    <el-button :icon="ArrowLeft" class="pan-back-btn" @click="back">
      返回
    </el-button>
    <el-row>
      <el-col :span="12">
        <h1 class="text-jumbo">
          401错误!
        </h1>
        <h2>您没有访问权限！</h2>
        <h6>对不起，您没有访问权限，请不要进行非法操作！您可以返回主页面</h6>
        <ul class="list-unstyled">
          <li class="link-type">
            <router-link to="/">
              回首页
            </router-link>
          </li>
        </ul>
      </el-col>
      <el-col :span="12">
        <img :src="errGifUrl" width="313" height="428" alt="401 Unauthorized">
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import errGif from '@/assets/401.gif'

const router = useRouter()
const route = useRoute()

// 添加时间戳防止图片缓存
const errGifUrl = ref(errGif + '?' + Date.now())

// 返回上一页或首页
const back = () => {
  if (route.query.noGoBack) {
    router.push('/')
  } else {
    router.go(-1)
  }
}
</script>

<style scoped>
.errPage-container {
  width: 800px;
  max-width: 100%;
  margin: 100px auto;
  padding: 0 20px;
  box-sizing: border-box;
}

.pan-back-btn {
  background: #008489;
  color: #fff;
  border: none !important;
  margin-bottom: 40px;
}

.pan-back-btn:hover {
  opacity: 0.8;
}

.text-jumbo {
  font-size: 60px;
  font-weight: 700;
  color: #484848;
  margin: 20px 0;
}

h2 {
  font-size: 24px;
  color: #606266;
  margin: 20px 0;
}

h6 {
  font-size: 14px;
  color: #909399;
  margin: 10px 0 30px;
}

.list-unstyled {
  padding: 0;
  margin: 0;
  list-style: none;
  font-size: 14px;
}

.list-unstyled li {
  padding-bottom: 5px;
}

.list-unstyled a {
  color: #008489;
  text-decoration: none;
  transition: all 0.3s;
}

.list-unstyled a:hover {
  text-decoration: underline;
  opacity: 0.8;
}

@media screen and (max-width: 768px) {
  .el-col {
    width: 100% !important;
    text-align: center;
  }

  img {
    max-width: 100%;
    height: auto;
    margin-top: 30px;
  }

  .text-jumbo {
    font-size: 40px;
  }
}
</style> 