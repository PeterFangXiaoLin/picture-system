<template>
  <div id="user-register-page" class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
    <!-- 背景图片 -->
    <div class="absolute inset-0 bg-cover bg-center bg-no-repeat opacity-40 login-background"></div>
    <!-- 背景遮罩层（临时注释以调试）-->
    <!-- <div class="absolute inset-0 bg-black bg-opacity-10"></div> -->
    <div class="max-w-md w-full space-y-8 relative z-10">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          用户注册
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          创建您的新账号
        </p>
      </div>
 
      <div class="bg-white bg-opacity-95 backdrop-blur-sm py-8 px-6 shadow-xl rounded-lg border border-white border-opacity-20">
        <a-form
          :model="registerForm"
          name="registerForm"
          @finish="handleRegister"
          autocomplete="off"
          layout="vertical"
          :rules="rules"
        >
          <a-form-item
            label="用户账号"
            name="userAccount"
          >
            <a-input
              v-model:value="registerForm.userAccount"
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
              v-model:value="registerForm.userPassword"
              size="large"
              placeholder="请输入用户密码"
              autocomplete="new-password"
            >
              <template #prefix>
                <LockOutlined class="text-gray-400" />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item
            label="确认密码"
            name="checkPassword"
          >
            <a-input-password
              v-model:value="registerForm.checkPassword"
              size="large"
              placeholder="请再次输入密码"
              autocomplete="new-password"
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
              注册
            </a-button>
          </a-form-item>
        </a-form>
        
        <!-- 跳转到登录页面 -->
        <div class="mt-6 text-center">
          <span class="text-gray-600">已经有账号？</span>
          <a @click="goToLogin" class="ml-1 text-indigo-600 hover:text-indigo-500 font-medium cursor-pointer">
            登录账号
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
import { userRegisterUsingPost } from '@/api/userController'

// 注册表单数据
const registerForm = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: ''
})

// 加载状态
const loading = ref<boolean>(false)

// 路由实例
const router = useRouter()

// 表单验证规则
const rules = {
  userAccount: [
    { required: true, message: '请输入用户账号', trigger: 'blur' },
    { whitespace: true, message: '用户账号不能为空', trigger: 'blur' },
    { min: 4, message: '用户账号至少4个字符', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入用户密码', trigger: 'blur' },
    { whitespace: true, message: '用户密码不能为空', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { whitespace: true, message: '确认密码不能为空', trigger: 'blur' }
  ]
}

// 注册处理函数
const handleRegister = async () => {
  // 判断两次输入的密码是否一致
  if (registerForm.userPassword !== registerForm.checkPassword) {
    message.error('两次输入的密码不一致')
    return
  }

  loading.value = true

  try {
    // 调用注册API
    const res = await userRegisterUsingPost({
      userAccount: registerForm.userAccount?.trim(),
      userPassword: registerForm.userPassword?.trim(),
      checkPassword: registerForm.checkPassword?.trim(),
    })

    // 注册成功，跳转到登录页面
    if (res.data.code === 0 && res.data.data) {
      message.success('注册成功')
      router.push({
        path: '/user/login',
        replace: true,
      })
    } else {
      message.error('注册失败，' + res.data.message)
    }
  } catch (error: any) {
    console.error('注册请求失败:', error)
    message.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/user/login')
}
</script>

<style scoped>
/* Ant Design Vue 和 TailwindCSS 结合使用 */
#user-register-page {
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
  #user-register-page {
    padding: 1rem;
  }
}
</style>
