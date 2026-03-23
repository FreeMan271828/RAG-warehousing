<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <el-icon class="logo-icon"><Box /></el-icon>
        <span v-if="!isCollapsed" class="logo-text">烟厂仓储</span>
      </div>
      
      <el-scrollbar class="menu-scrollbar">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed"
          :background-color="menuBgColor"
          :text-color="menuTextColor"
          :active-text-color="menuActiveTextColor"
          router
        >
          <template v-for="route in menuData" :key="route.path">
            <!-- 有子菜单的 -->
            <el-sub-menu v-if="route.children && route.children.length > 0" :index="'/' + route.path">
              <template #title>
                <el-icon v-if="route.meta?.icon"><component :is="route.meta.icon" /></el-icon>
                <span>{{ route.meta?.title }}</span>
              </template>
              <el-menu-item
                v-for="child in route.children"
                :key="child.path"
                :index="'/' + route.path + '/' + child.path"
              >
                <el-icon v-if="child.meta?.icon"><component :is="child.meta.icon" /></el-icon>
                <span>{{ child.meta?.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            
            <!-- 无子菜单的 -->
            <el-menu-item v-else :index="'/' + route.path">
              <el-icon v-if="route.meta?.icon"><component :is="route.meta.icon" /></el-icon>
              <span>{{ route.meta?.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </aside>
    
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 头部 -->
      <header class="header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleCollapse">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute?.parent">
              {{ currentRoute.parent.meta?.title }}
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute?.meta?.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <!-- 主题切换 -->
          <el-tooltip :content="isDark ? '切换亮色模式' : '切换暗色模式'" placement="bottom">
            <el-icon class="header-icon" @click="toggleTheme">
              <Sunny v-if="isDark" />
              <Moon v-else />
            </el-icon>
          </el-tooltip>
          
          <!-- 刷新 -->
          <el-tooltip content="刷新" placement="bottom">
            <el-icon class="header-icon" @click="refresh">
              <Refresh />
            </el-icon>
          </el-tooltip>
          
          <!-- 全屏 -->
          <el-tooltip content="全屏" placement="bottom">
            <el-icon class="header-icon" @click="toggleFullscreen">
              <FullScreen v-if="!isFullscreen" />
              <Aim v-else />
            </el-icon>
          </el-tooltip>
          
          <!-- 用户 -->
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :icon="User" />
              <span class="username">{{ username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <!-- 内容区 -->
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { menuRoutes } from '@/router'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { getCurrentUserInfo } from '@/utils/permission'

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()

// 获取用户信息
const userInfo = computed(() => getCurrentUserInfo())

// 用户名
const username = computed(() => userInfo.value?.realName || userInfo.value?.username || '管理员')

// 折叠状态
const isCollapsed = ref(false)

// 全屏状态
const isFullscreen = ref(false)

// 主题
const isDark = computed(() => themeStore.isDark)

// 菜单背景色
const menuBgColor = computed(() => isDark.value ? '#16213e' : '#ffffff')
const menuTextColor = computed(() => isDark.value ? '#e4e7ed' : '#303133')
const menuActiveTextColor = computed(() => isDark.value ? '#409eff' : '#409eff')

// 菜单数据
const menuData = menuRoutes

// 当前激活菜单
const activeMenu = computed(() => {
  const path = route.path
  // 精确匹配或前缀匹配
  if (path === '/dashboard') return '/dashboard'
  // 对于子路由，处理 /warehouse/location 这类路由
  for (const item of menuData) {
    if (item.children) {
      for (const child of item.children) {
        const fullPath = '/' + item.path + '/' + child.path
        if (path === fullPath || path.startsWith(fullPath + '/')) {
          return fullPath
        }
      }
    }
  }
  return path
})

// 当前路由信息
const currentRoute = computed(() => {
  const path = route.path
  for (const item of menuData) {
    if (item.children) {
      for (const child of item.children) {
        const fullPath = '/' + item.path + '/' + child.path
        if (path === fullPath || path.startsWith(fullPath + '/')) {
          return { ...child, parent: item }
        }
      }
    }
  }
  // 顶级路由
  if (path === '/dashboard') {
    return { meta: { title: '仪表盘' } }
  }
  return route
})

// 切换折叠
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

// 切换主题
const toggleTheme = () => {
  themeStore.toggleTheme()
}

// 刷新
const refresh = () => {
  router.go(0)
}

// 全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

// 用户菜单
const handleCommand = (command: string) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/system/profile')
  } else if (command === 'settings') {
    ElMessage.info('设置开发中')
  }
}
</script>

<style lang="scss" scoped>
.main-layout {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 220px;
  height: 100vh;
  background-color: var(--sidebar-bg);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  
  &.collapsed {
    width: 64px;
  }
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    border-bottom: 1px solid var(--border-color);
    
    .logo-icon {
      font-size: 28px;
      color: var(--primary-color);
    }
    
    .logo-text {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-color);
      white-space: nowrap;
    }
  }
  
  .menu-scrollbar {
    flex: 1;
    overflow: hidden;
  }
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: var(--bg-color);
}

.header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: var(--header-bg);
  border-bottom: 1px solid var(--header-border-color);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .collapse-icon {
      font-size: 20px;
      cursor: pointer;
      color: var(--text-color);
      
      &:hover {
        color: var(--primary-color);
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .header-icon {
      font-size: 20px;
      cursor: pointer;
      color: var(--text-color);
      
      &:hover {
        color: var(--primary-color);
      }
    }
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      
      .username {
        color: var(--text-color);
      }
    }
  }
}

.content {
  flex: 1;
  overflow: auto;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
