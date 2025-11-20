import HomePage from '@/pages/HomePage.vue'
import { h } from 'vue'
import { HomeOutlined, UploadOutlined } from '@ant-design/icons-vue'
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
  {
    path: '/admin/categoryManage',
    name: '图片分类管理',
    component: () => import('@/pages/admin/CategoryManagePage.vue'),
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/add_picture',
    name: '创建图片',
    component: () => import('@/pages/AddPicturePage.vue'),
    meta: {
      icon: () => h(UploadOutlined)
    }
  },
  {
    path: '/admin/tagManage',
    name: '标签管理',
    component: () => import('@/pages/admin/TagManagePage.vue'),
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/admin/pictureManage',
    name: '图片管理',
    component: () => import('@/pages/admin/PictureManagePage.vue'),
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/picture/:id',
    name: '图片详情',
    component: () => import('@/pages/PictureDetailPage.vue'),
    props: true,
    meta: {
      show: false
    }
  },
  {
    path: '/add_picture/batch',
    name: '批量创建图片',
    component: () => import('@/pages/AddPictureBatchPage.vue'),
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  },
]

export default routes
