import HomePage from '@/pages/HomePage.vue'
import { h } from 'vue'
import { HomeOutlined, UploadOutlined, HistoryOutlined } from '@ant-design/icons-vue'
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
    path: '/admin/spaceManage',
    name: '空间管理',
    component: () => import('@/pages/admin/SpaceManagePage.vue'),
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/out_painting/task',
    name: '扩图任务',
    component: () => import('@/pages/OutPaintingTaskPage.vue'),
    meta: {
      authCheck: ACCESS_ENUM.USER,
      icon: () => h(HistoryOutlined),
    },
  },
  {
    path: '/admin/outPaintingTaskManage',
    name: '扩图任务管理',
    component: () => import('@/pages/admin/OutPaintingTaskManagePage.vue'),
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    },
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
  {
    path: '/add_space',
    name: '创建空间',
    component: () => import('@/pages/AddSpacePage.vue'),
  },
  {
    path: '/my_space',
    name: '我的空间',
    component: () => import('@/pages/MySpacePage.vue'),
    meta: {
      show: false
    }
  },
  {
    path: '/space_analyze',
    name: '空间分析',
    component: () => import('@/pages/SpaceAnalyzePage.vue'),
    meta: {
      show: false
    }
  },
  {
    path: '/space/:id',
    name: '空间详情',
    component: () => import('@/pages/SpaceDetailPage.vue'),
    props: true,
    meta: {
      show: false
    }
  },
  {
    path: '/spaceUserManage/:id',
    name: '空间成员管理',
    component: () => import('@/pages/admin/SpaceUserManagePage.vue'),
    props: true,
    meta: {
      show: false
    }
  },
  {
    path: '/search_picture',
    name: '图片搜索',
    component: () => import('@/pages/SearchPicturePage.vue'),
    meta: {
      show: false
    }
  }
]

export default routes
