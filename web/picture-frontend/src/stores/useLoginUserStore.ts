import {defineStore} from "pinia";      // 引入
import {ref} from "vue";

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<any>({
    userName: '未登录',
  })

  /**
   * 远程获取登录用户信息
   */
  async function getLoginUser() {

  }

  /**
   * 设置登录用户
   */
  function setLoginUser(user: any) {
    loginUser.value = user
  }

  // 返回
  return {loginUser, getLoginUser, setLoginUser}
})
