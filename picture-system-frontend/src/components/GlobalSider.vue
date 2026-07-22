<template>
  <div id="global-sider">
    <a-layout-sider
      v-if="loginUserStore.loginUser.id"
      width="200"
      breakpoint="lg"
      collapsed-width="0"
      :style="{ overflow: 'auto' }"
    >
      <a-menu
        :selectedKeys="current"
        :items="menuItems"
        mode="inline"
        @click="doMenuClick"
      />
    </a-layout-sider>
  </div>
</template>

<script setup lang="ts">
// 菜单列表
import { useRouter, useRoute } from 'vue-router'
import { UserOutlined, PictureOutlined, HistoryOutlined, TeamOutlined } from '@ant-design/icons-vue'
import { computed, h, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'
import { listMyTeamSpaceUsingPost } from '@/api/spaceUserController.ts'
import { message } from 'ant-design-vue'

const loginUserStore = useLoginUserStore()
const CREATE_TEAM_MENU_KEY = `/add_space?type=${SPACE_TYPE_ENUM.TEAM}`
const router = useRouter()
const route = useRoute()

// 固定的菜单列表
const fixedMenuItems = [
  {
    key: '/',
    label: '公共图库',
    icon: () => h(PictureOutlined),
  },
  {
    key: '/my_space',
    label: '我的空间',
    icon: () => h(UserOutlined),
  },
  {
    key: '/out_painting/task',
    label: '扩图任务',
    icon: () => h(HistoryOutlined),
  },
  {
    key: CREATE_TEAM_MENU_KEY,
    label: '创建团队',
    icon: () => h(TeamOutlined),
  },
]

const teamSpaceList = ref<API.SpaceUserVO[]>([])
const menuItems = computed(() => {
  // 如果用户没有团队空间，返回固定菜单列表
  if (teamSpaceList.value.length < 1) {
    return fixedMenuItems
  }
  const teamSpaceSubMenus = teamSpaceList.value.map((spaceUser: API.SpaceUserVO) => {
    const space = spaceUser.space
    return {
      key: '/space/' + space?.id,
      label: space?.spaceName,
    }
  })
  const teamSpaceMenuGroup = {
    type: 'group',
    label: '我的团队',
    children: teamSpaceSubMenus,
  }
  return [...fixedMenuItems, teamSpaceMenuGroup]
})

// 加载团队空间列表
const fetchTeamSpaceList = async () => {
  const res = await listMyTeamSpaceUsingPost()
  if (res.data.code === 0 && res.data.data) {
    teamSpaceList.value = res.data.data
  } else {
    message.error('加载我的团队空间失败, ' + res.data.message)
  }
}

onMounted(() => window.addEventListener('space-membership-changed', fetchTeamSpaceList))
onBeforeUnmount(() => window.removeEventListener('space-membership-changed', fetchTeamSpaceList))

// 登录后加载；创建团队跳转到空间详情页时重新加载菜单
watch(
  [() => loginUserStore.loginUser.id, () => route.path],
  ([userId]) => {
    if (userId) {
      fetchTeamSpaceList()
    } else {
      teamSpaceList.value = []
    }
  },
  { immediate: true },
)

/** 将实际路由映射到侧边栏菜单 key */
const getActiveMenuKey = (path: string) => {
  if (path.startsWith('/space/')) {
    const isTeamSpace = teamSpaceList.value.some(
      (spaceUser) => `/space/${spaceUser.space?.id}` === path,
    )
    return isTeamSpace ? path : '/my_space'
  }
  if (path === '/add_space') {
    return Number(route.query.type) === SPACE_TYPE_ENUM.TEAM
      ? CREATE_TEAM_MENU_KEY
      : '/my_space'
  }
  if (path.startsWith('/out_painting')) {
    return '/out_painting/task'
  }
  return path
}

// 当前选中菜单会随路由和异步加载的团队空间列表自动更新
const current = computed<string[]>(() => [getActiveMenuKey(route.path)])

// 路由跳转事件
const doMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}
</script>

<style scoped>
#global-sider .ant-layout-sider {
  background: none;
}
</style>
