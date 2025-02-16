import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import UserInfoPage from '@/pages/user/UserInfoPage.vue'
import ACCESS_ENUM from '@/access/accessEnum'
import NoAuthPage from '@/error/NoAuthPage.vue'
import AddPicturePage from '@/pages/picture/AddPicturePage.vue'

export const routes = [
  {
    path: '/',
    name: '首页',
    component: HomeView,
  },
  {
    path: '/user/login',
    name: '用户登录',
    component: UserLoginPage,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: '/user/register',
    name: '用户注册',
    component: UserRegisterPage,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: '/user/info',
    name: '用户信息',
    component: UserInfoPage,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: '/admin/userManage',
    name: '用户管理',
    component: UserManagePage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: '/401',
    name: '无权限',
    component: NoAuthPage,
  },
  {
    path: '/add_picture',
    name: '创建图片',
    component: AddPicturePage,
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (About.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('@/pages/AboutPage.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
})

export default router
