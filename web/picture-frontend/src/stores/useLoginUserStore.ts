import { defineStore } from 'pinia' // 引入
import { ref } from 'vue'
import { getLoginUserUsingGet } from '@/api/userController.ts'

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.UserRespVO>({
    userName: '未登录',
  })

  /**
   * 远程获取登录用户信息
   */
  async function getLoginUser() {
    const res = await getLoginUserUsingGet()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
  }

  /**
   * 设置登录用户
   */
  function setLoginUser(user: any) {
    loginUser.value = user
  }

  // 返回
  return { loginUser, getLoginUser, setLoginUser }
})
