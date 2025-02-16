<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :span="6" :xs="24">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
            </div>
          </template>
          <div class="text-center">
            <UserAvatar />
          </div>
          <ul class="list-group">
            <li class="list-group-item">
              <el-icon><User /></el-icon>
              用户账号
              <div class="pull-right">{{ loginUserStore.loginUser.userAccount }}</div>
            </li>
            <li class="list-group-item">
              <el-icon><UserFilled /></el-icon>
              用户昵称
              <div class="pull-right">{{ loginUserStore.loginUser.userName }}</div>
            </li>
            <li class="list-group-item">
              <el-icon><Management /></el-icon>
              用户角色
              <div class="pull-right">
                <el-tag :type="loginUserStore.loginUser.userRole === 'admin' ? 'success' : 'info'">
                  {{ loginUserStore.loginUser.userRole === 'admin' ? '管理员' : '普通用户' }}
                </el-tag>
              </div>
            </li>
            <li class="list-group-item">
              <el-icon><Timer /></el-icon>
              创建时间
              <div class="pull-right">
                {{ dayjs(loginUserStore.loginUser.createTime).format('YYYY-MM-DD HH:mm:ss') }}
              </div>
            </li>
          </ul>
        </el-card>
      </el-col>

      <!-- 右侧选项卡 -->
      <el-col :span="18" :xs="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>基本资料</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="mt-4">
                <el-form-item label="用户昵称" prop="userName">
                  <el-input v-model="form.userName" maxlength="30" />
                </el-form-item>
                <el-form-item label="用户简介" prop="userProfile">
                  <el-input
                    v-model="form.userProfile"
                    type="textarea"
                    :rows="4"
                    maxlength="200"
                    show-word-limit
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSubmit">保存</el-button>
                  <el-button @click="resetForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <el-form
                ref="pwdFormRef"
                :model="pwdForm"
                :rules="pwdRules"
                label-width="100px"
                class="mt-4"
              >
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input
                    v-model="pwdForm.oldPassword"
                    type="password"
                    show-password
                    placeholder="请输入旧密码"
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="pwdForm.newPassword"
                    type="password"
                    show-password
                    placeholder="请输入新密码"
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="pwdForm.confirmPassword"
                    type="password"
                    show-password
                    placeholder="请确认新密码"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdatePwd">保存</el-button>
                  <el-button @click="resetPwdForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { User, UserFilled, Management, Timer } from '@element-plus/icons-vue'
import dayjs from '@/utils/dayjs'
import { editUserInfoUsingPost, resetPasswordUsingPost } from '@/api/userController'
import { useRouter } from 'vue-router'
import UserAvatar from '@/components/user/UserAvatar.vue'

const loginUserStore = useLoginUserStore()
const activeTab = ref('userinfo')
const formRef = ref<FormInstance>()
const pwdFormRef = ref<FormInstance>()

// 基本信息表单
const form = reactive<API.UserEditReqVO>({
  id: 0,
  userName: '',
  userProfile: '',
})

// 密码表单
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 基本信息校验规则
const rules = reactive<FormRules>({
  userName: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
    { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' },
  ],
})

// 密码校验规则
const pwdRules = reactive<FormRules>({
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 20, message: '长度在 8 到 20 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
})

// 初始化表单数据
const initForm = () => {
  Object.assign(form, loginUserStore.loginUser)
}

// 提交基本信息
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await editUserInfoUsingPost(form)
        if (res.data.code === 0 && res.data.data) {
          ElMessage.success('修改成功')
          loginUserStore.getLoginUser()
        } else {
          ElMessage.error('修改失败：' + res.data.message)
        }
      } catch (error) {
        ElMessage.error('修改失败：' + error)
      }
    }
  })
}

// 重置基本信息表单
const resetForm = () => {
  formRef.value?.resetFields()
  initForm()
}

const router = useRouter()

// 更新密码
const handleUpdatePwd = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await resetPasswordUsingPost({
          id: loginUserStore.loginUser.id,
          oldPassword: pwdForm.oldPassword,
          newPassword: pwdForm.newPassword,
        })
        if (res.data.code === 0 && res.data.data) {
          ElMessage.success('密码修改成功')
          resetPwdForm()
          // 自动退出登录
          loginUserStore.setLoginUser({
            userName: '未登录',
          })
          await router.push('/user/login')
        } else {
          ElMessage.error('密码修改失败：' + res.data.message)
        }
      } catch (error) {
        ElMessage.error('密码修改失败：' + error)
      }
    }
  })
}

// 重置密码表单
const resetPwdForm = () => {
  pwdFormRef.value?.resetFields()
}

// 头像上传前的处理
const beforeAvatarUpload = (file: File) => {
  // TODO: 实现头像上传逻辑
  return false
}

onMounted(() => {
  initForm()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
}

.list-group {
  padding: 0;
  margin: 0;
  list-style: none;
}

.list-group-item {
  position: relative;
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.list-group-item:last-child {
  border-bottom: none;
}

.pull-right {
  float: right;
}

.avatar-uploader {
  text-align: center;
  margin-bottom: 20px;
}

.upload-tip {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

.cursor-pointer {
  cursor: pointer;
}

:deep(.el-icon) {
  margin-right: 8px;
  vertical-align: middle;
}
</style>
