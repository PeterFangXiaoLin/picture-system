import HomePage from '@/pages/HomePage.vue'
import { h } from 'vue'

const routes = [
  {
    path: '/',
    name: '主页',
    component: HomePage,
    meta: {
      icon: () => h('HomeOutlined'),
    }
  },
  {
    path: '/about',
    name: '关于',
    // route level code-splitting
    // this generates a separate chunk (About.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('../pages/AboutView.vue'),
  },
]

export default routes
