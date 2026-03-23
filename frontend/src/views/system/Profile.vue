<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">个人中心</h2>
    </div>

    <div class="profile-content">
      <!-- 基本信息 -->
      <el-card class="profile-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">基本信息</span>
          </div>
        </template>
        
        <div class="user-info">
          <div class="avatar-section">
            <el-avatar :size="100" :icon="User" />
            <div class="user-name">{{ userInfo.realName || userInfo.username }}</div>
            <div class="user-status">
              <el-tag :type="userInfo.status === 1 ? 'success' : 'info'">
                {{ userInfo.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </div>
          </div>
          
          <el-descriptions :column="2" border class="info-descriptions">
            <el-descriptions-item label="用户名">{{ userInfo.username || '-' }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ userInfo.realName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userInfo.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="岗位">{{ userInfo.post || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最后登录IP">{{ userInfo.loginIp || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最后登录时间">
              {{ userInfo.loginTime ? formatDateTime(userInfo.loginTime) : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ userInfo.createTime ? formatDateTime(userInfo.createTime) : '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>

      <!-- 角色信息 -->
      <el-card class="profile-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">角色信息</span>
          </div>
        </template>
        
        <div class="roles-section">
          <el-tag
            v-for="role in userInfo.roles"
            :key="role.roleId"
            :type="getRoleType(role.roleType)"
            class="role-tag"
            effect="light"
          >
            {{ role.roleName }}
          </el-tag>
          <el-empty v-if="!userInfo.roles || userInfo.roles.length === 0" description="暂无角色" :image-size="60" />
        </div>
      </el-card>

      <!-- 权限信息 -->
      <el-card class="profile-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">权限信息</span>
            <span class="permission-count">共 {{ userInfo.permissions?.length || 0 }} 项权限</span>
          </div>
        </template>
        
        <div class="permissions-section">
          <el-tabs v-model="activeTab" type="border-card">
            <el-tab-pane label="菜单权限" name="menu">
              <div class="permission-list">
                <template v-if="menuPermissions.length > 0">
                  <el-tag
                    v-for="perm in menuPermissions"
                    :key="perm.permissionId"
                    type="primary"
                    class="permission-tag"
                  >
                    {{ perm.permissionName }}
                  </el-tag>
                </template>
                <el-empty v-else description="暂无菜单权限" :image-size="60" />
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="按钮权限" name="button">
              <div class="permission-list">
                <template v-if="buttonPermissions.length > 0">
                  <el-tag
                    v-for="perm in buttonPermissions"
                    :key="perm.permissionId"
                    type="success"
                    class="permission-tag"
                  >
                    {{ perm.permissionName }}
                  </el-tag>
                </template>
                <el-empty v-else description="暂无按钮权限" :image-size="60" />
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="接口权限" name="api">
              <div class="permission-list">
                <template v-if="apiPermissions.length > 0">
                  <el-tag
                    v-for="perm in apiPermissions"
                    :key="perm.permissionId"
                    type="warning"
                    class="permission-tag"
                  >
                    {{ perm.permissionName }}
                  </el-tag>
                </template>
                <el-empty v-else description="暂无接口权限" :image-size="60" />
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="全部权限" name="all">
              <div class="permission-list">
                <template v-if="userInfo.permissions && userInfo.permissions.length > 0">
                  <el-tag
                    v-for="perm in userInfo.permissions"
                    :key="perm.permissionId"
                    class="permission-tag"
                    :type="getPermissionType(perm.permissionType)"
                  >
                    {{ perm.permissionName }} ({{ perm.permissionCode }})
                  </el-tag>
                </template>
                <el-empty v-else description="暂无权限" :image-size="60" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { authApi } from '@/api'
import { getCurrentUserInfo, setCurrentUserInfo, type UserInfo } from '@/utils/permission'

// 用户信息
const userInfo = ref<any>({
  id: 0,
  username: '',
  realName: '',
  email: '',
  phone: '',
  post: '',
  deptId: null,
  status: 1,
  isAdmin: 0,
  loginIp: '',
  loginTime: '',
  createTime: '',
  roles: [],
  permissions: []
})

// 当前激活的标签页
const activeTab = ref('menu')

// 按类型筛选权限
const menuPermissions = computed(() => {
  return userInfo.value.permissions?.filter((p: any) => p.permissionType === 'menu') || []
})

const buttonPermissions = computed(() => {
  return userInfo.value.permissions?.filter((p: any) => p.permissionType === 'button') || []
})

const apiPermissions = computed(() => {
  return userInfo.value.permissions?.filter((p: any) => p.permissionType === 'api') || []
})

// 获取用户信息
const loadUserInfo = async () => {
  try {
    const res = await authApi.getCurrentUser()
    if (res.data) {
      userInfo.value = res.data
      // 同时更新本地存储的用户信息
      setCurrentUserInfo({
        id: res.data.id,
        username: res.data.username,
        realName: res.data.realName || res.data.username,
        roles: res.data.roleCodes || [],
        roleCodes: res.data.roleCodes || [],
        permissions: res.data.permissions || [],
        permissionCodes: res.data.permissionCodes || []
      })
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 如果接口失败，尝试从本地存储获取
    const localUserInfo = getCurrentUserInfo()
    if (localUserInfo) {
      userInfo.value = {
        ...localUserInfo,
        roles: localUserInfo.roles?.map((code: string) => ({
          roleCode: code,
          roleName: code
        })) || []
      }
    }
  }
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  return dateTime.replace('T', ' ').substring(0, 19)
}

// 获取角色类型对应的标签类型
const getRoleType = (roleType: string) => {
  const typeMap: Record<string, any> = {
    'admin': 'danger',
    'system': 'warning',
    'user': 'primary'
  }
  return typeMap[roleType] || 'info'
}

// 获取权限类型对应的标签类型
const getPermissionType = (permissionType: string) => {
  const typeMap: Record<string, any> = {
    'menu': 'primary',
    'button': 'success',
    'api': 'warning'
  }
  return typeMap[permissionType] || 'info'
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  
  .page-title {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: var(--text-color);
  }
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-card {
  :deep(.el-card__header) {
    padding: 14px 20px;
    background-color: var(--bg-color);
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-color);
    }
    
    .permission-count {
      font-size: 12px;
      color: var(--text-color-secondary);
    }
  }
}

.user-info {
  .avatar-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px 0;
    
    .user-name {
      margin-top: 16px;
      font-size: 20px;
      font-weight: 600;
      color: var(--text-color);
    }
    
    .user-status {
      margin-top: 8px;
    }
  }
  
  .info-descriptions {
    margin-top: 20px;
  }
}

.roles-section {
  .role-tag {
    margin: 4px;
  }
}

.permissions-section {
  .permission-list {
    min-height: 100px;
    max-height: 400px;
    overflow-y: auto;
    
    .permission-tag {
      margin: 4px;
    }
  }
}
</style>
