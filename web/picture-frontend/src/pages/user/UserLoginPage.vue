<template>
  <div class="login-container">
    <div class="login-box">
      <h2>智能云图库-用户登录</h2>
      <div>企业级智能协同云图库</div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="0"
        size="large"
      >
        <el-form-item prop="userAccount">
          <el-input
            v-model="loginForm.userAccount"
            placeholder="请输入账号"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="userPassword">
          <el-input
            v-model="loginForm.userPassword"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
        <div class="login-extra">
          <router-link to="/user/register">注册账号</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { userLoginUsingPost } from '@/api/userController'
import { useLoginUserStore } from '@/stores/useLoginUserStore'

const router = useRouter()
const loading = ref(false)
const loginFormRef = ref()

const loginUserStore = useLoginUserStore()

const loginForm = reactive({
  userAccount: '',
  userPassword: ''
})

const loginRules = {
  userAccount: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  userPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const res = await userLoginUsingPost(loginForm)
        if (res.data.code === 0) {
          await loginUserStore.getLoginUser()
          ElMessage.success('登录成功')
          router.push({
            path: '/',
            replace: true,
          })
        } else {
          ElMessage.error(res.data.message || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败，' + error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-extra {
  text-align: right;
  margin-top: 10px;
}

.login-extra a {
  color: #409EFF;
  text-decoration: none;
  font-size: 14px;
}

.login-extra a:hover {
  color: #66b1ff;
}
</style>