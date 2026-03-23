<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <el-icon class="logo-icon"><Box /></el-icon>
        <h1>延安烟厂三维数字孪生系统</h1>
      </div>
      
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码 (password)"
            :prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>

        <el-form-item>
          <el-button size="large" class="register-button" @click="showRegisterDialog = true">
            注 册
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <span>© 2024 延安烟厂. All rights reserved.</span>
      </div>
    </div>

    <!-- 注册对话框 -->
    <el-dialog v-model="showRegisterDialog" title="用户注册" width="400px" :close-on-click-modal="false">
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入姓名(可选)" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码(至少4位)" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegisterDialog = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { User, Lock, Box } from '@element-plus/icons-vue'
import request from '@/api/request'
import { setCurrentUserInfo, type RoleType } from '@/utils/permission'

const router = useRouter()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: 'freeman',
  password: 'freeman',
  remember: true
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 注册相关
const showRegisterDialog = ref(false)
const registerLoading = ref(false)
const registerFormRef = ref<FormInstance>()

const registerForm = reactive({
  username: '',
  realName: '',
  password: '',
  confirmPassword: ''
})

// 确认密码验证
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 4, message: '密码长度至少4位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      registerLoading.value = true
      try {
        const res = await request.post('/auth/register', {
          username: registerForm.username,
          password: registerForm.password,
          confirmPassword: registerForm.confirmPassword,
          realName: registerForm.realName
        })
        
        if (res.data && res.data.token) {
          localStorage.setItem('token', res.data.token)
          
          // 存储用户信息
          setCurrentUserInfo({
            id: res.data.userId,
            username: res.data.username,
            realName: registerForm.realName || registerForm.username,
            roles: ['user'] as RoleType[],
            roleCodes: ['user']
          })
          
          ElMessage.success('注册成功')
          showRegisterDialog.value = false
          router.push('/')
        }
      } catch (error: any) {
        const message = error?.response?.data?.message || '注册失败'
        ElMessage.error(message)
      } finally {
        registerLoading.value = false
      }
    }
  })
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 调用登录接口
        const res = await request.post('/auth/login', loginForm)
        if (res.data && res.data.token) {
          localStorage.setItem('token', res.data.token)
          
          // 获取用户信息
          try {
            const userRes = await request.get('/auth/info', {
              headers: { Authorization: `Bearer ${res.data.token}` }
            })
            if (userRes.data) {
              // 存储用户信息（包括权限）
              setCurrentUserInfo({
                id: userRes.data.id,
                username: userRes.data.username,
                realName: userRes.data.realName || userRes.data.username,
                roles: userRes.data.roles || [],
                roleCodes: userRes.data.roleCodes || [],
                permissions: userRes.data.permissions || [],
                permissionCodes: userRes.data.permissionCodes || []
              })
            }
          } catch (e) {
            console.warn('获取用户信息失败', e)
          }
          
          ElMessage.success('登录成功')
          router.push('/')
        }
      } catch (error: any) {
        // 登录失败，不进入演示模式
        loading.value = false
        const message = error?.response?.data?.message || '登录失败，请检查用户名和密码'
        ElMessage.error(message)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
      radial-gradient(circle at 20% 80%, rgba(64, 158, 255, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(103, 194, 58, 0.1) 0%, transparent 50%);
    pointer-events: none;
  }
}

.login-box {
  width: 420px;
  padding: 40px;
  background: var(--card-bg);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  
  .logo-icon {
    font-size: 48px;
    color: var(--primary-color);
    margin-bottom: 16px;
  }
  
  h1 {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-color);
    margin: 0;
  }
}

.login-form {
  .login-button {
    width: 100%;
  }
  .register-button {
    width: 100%;
  }
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  color: var(--text-color-secondary);
  font-size: 12px;
}
</style>
