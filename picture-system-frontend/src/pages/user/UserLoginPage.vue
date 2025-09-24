<template>
  <div id="user-login-page" class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
    <!-- 背景图片 -->
    <div class="absolute inset-0 bg-cover bg-center bg-no-repeat opacity-40 login-background"></div>
    <!-- 背景遮罩层（临时注释以调试）-->
    <!-- <div class="absolute inset-0 bg-black bg-opacity-10"></div> -->
    <div class="max-w-md w-full space-y-8 relative z-10">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          用户登录
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          欢迎回来，请输入您的账号信息
        </p>
      </div>

      <div class="bg-white bg-opacity-95 backdrop-blur-sm py-8 px-6 shadow-xl rounded-lg border border-white border-opacity-20">
        <a-form
          :model="loginForm"
          name="loginForm"
          @finish="handleLogin"
          autocomplete="off"
          layout="vertical"
          :rules="rules"
        >
          <a-form-item
            label="用户账号"
            name="userAccount"
          >
            <a-input
              v-model:value="loginForm.userAccount"
              size="large"
              placeholder="请输入用户账号"
              autocomplete="username"
            >
              <template #prefix>
                <UserOutlined class="text-gray-400" />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            label="用户密码"
            name="userPassword"
          >
            <a-input-password
              v-model:value="loginForm.userPassword"
              size="large"
              placeholder="请输入用户密码"
              autocomplete="current-password"
            >
              <template #prefix>
                <LockOutlined class="text-gray-400" />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              :loading="loading"
              class="w-full text-base font-medium"
            >
              登录
            </a-button>
          </a-form-item>
        </a-form>
        
        <!-- 跳转到注册页面 -->
        <div class="mt-6 text-center">
          <span class="text-gray-600">还没有账号？</span>
          <a @click="goToRegister" class="ml-1 text-indigo-600 hover:text-indigo-500 font-medium cursor-pointer">
            注册账号
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { userLoginUsingPost } from '@/api/userController'
import { useLoginUserStore } from '@/stores/useLoginUserStore'

// 登录表单数据
const loginForm = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: ''
})

// 加载状态
const loading = ref<boolean>(false)

// 路由实例
const router = useRouter()

// 用户状态管理
const loginUserStore = useLoginUserStore()

// 表单验证规则
const rules = {
  userAccount: [
    { required: true, message: '请输入用户账号', trigger: 'blur' },
    { whitespace: true, message: '用户账号不能为空', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入用户密码', trigger: 'blur' },
    { whitespace: true, message: '用户密码不能为空', trigger: 'blur' }
  ]
}

// 登录处理函数
const handleLogin = async () => {
  loading.value = true

  try {
    // 调用登录API
    const res = await userLoginUsingPost({
      userAccount: loginForm.userAccount?.trim(),
      userPassword: loginForm.userPassword?.trim(),
    })

    // 登录成功，把登录态保存到全局状态中
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登录成功')
      router.push({
        path: '/',
        replace: true,
      })
    } else {
      message.error('登录失败，' + res.data.message)
    }
  } catch (error: any) {
    console.error('登录请求失败:', error)
    message.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/user/register')
}
</script>

<style scoped>
/* Ant Design Vue 和 TailwindCSS 结合使用 */
#user-login-page {
  min-height: 100vh;
  background-attachment: fixed;
}

/* 背景图片样式 */
.login-background {
  background-image: url('@/assets/background.webp');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

/* 标题样式增强 */
h2 {
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 表单容器样式 */
:deep(.ant-form) {
  position: relative;
}

:deep(.ant-form-item-label) {
  font-weight: 600;
  color: #374151;
}

:deep(.ant-input-affix-wrapper),
:deep(.ant-input) {
  border-radius: 10px;
  border: 2px solid #e5e7eb;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

:deep(.ant-input-affix-wrapper:focus),
:deep(.ant-input-affix-wrapper-focused),
:deep(.ant-input:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

:deep(.ant-btn-primary) {
  height: 48px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px 0 rgba(116, 75, 162, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 600;
  font-size: 16px;
}

:deep(.ant-btn-primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px 0 rgba(116, 75, 162, 0.4);
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
}

:deep(.ant-btn-primary:active) {
  transform: translateY(0);
}

:deep(.ant-form-item) {
  margin-bottom: 24px !important;
}

:deep(.ant-form-item:last-child) {
  margin-bottom: 0 !important;
}

/* 输入框图标样式 */
:deep(.anticon) {
  color: #9ca3af;
}

/* 响应式调整 */
@media (max-width: 640px) {
  #user-login-page {
    padding: 1rem;
  }
}
</style>
