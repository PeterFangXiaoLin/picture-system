import router from '@/router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { ElMessage } from 'element-plus'

// 是否为首次获取登录用户
let firstFetchLoginUser = true
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  // 确保页面刷新时，且为首次加载，能等待后端返回用户信息再校验
  if (firstFetchLoginUser) {
    await loginUserStore.getLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false
  }

  const toUrl = to.fullPath
  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.userRole !== 'admin') {
      ElMessage.error('无权限访问')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})
