import HomePage from '@/pages/HomePage.vue'
import { h } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import ACCESS_ENUM from '@/access/accessEnum.ts'

const routes = [
  {
    path: '/',
    name: '主页',
    component: HomePage,
    meta: {
      icon: () => h(HomeOutlined),
    }
  },
  {
    path: '/user/login',
    name: '用户登录',
    component: UserLoginPage,
    meta: {
      show: false
    }
  },
  {
    path: '/user/register',
    name: '用户注册',
    component: UserRegisterPage,
    meta: {
      show: false
    }
  },
  {
    path: '/admin/userManage',
    name: '用户管理',
    component: UserManagePage,
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  },
]

export default routes
