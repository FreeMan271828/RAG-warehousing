import request from './request'

// 系统管理API

// 部门管理 - 后端路径为 /dept/*
export const deptApi = {
  // 获取部门树
  getDeptTree: () => request.get('/dept/tree'),
  // 分页查询
  getDeptPage: (params?: any) => request.get('/dept/page', { params }),
  // 获取列表
  getDeptList: (params?: any) => request.get('/dept/list', { params }),
  // 获取详情
  getDeptById: (id: number) => request.get(`/dept/${id}`),
  // 创建
  createDept: (data: any) => request.post('/dept', data),
  // 更新
  updateDept: (data: any) => request.put('/dept', data),
  // 删除
  deleteDept: (id: number) => request.delete(`/dept/${id}`)
}

// 认证 - 后端路径为 /auth/*
export const authApi = {
  // 登录
  login: (data: { username: string; password: string }) => request.post('/auth/login', data),
  // 登出
  logout: () => request.post('/auth/logout'),
  // 获取当前用户信息
  getCurrentUser: () => request.get('/auth/info')
}

// 注意：用户管理和角色管理后端未提供相关API
// 如需使用，需要在后端添加相应接口

// 用户管理
export const userApi = {
  // 分页查询
  getUserPage: (params?: any) => request.get('/user/page', { params }),
  // 获取列表
  getUserList: (params?: any) => request.get('/user/list', { params }),
  // 获取详情
  getUserById: (id: number) => request.get(`/user/${id}`),
  // 创建
  createUser: (data: any) => request.post('/user', data),
  // 更新
  updateUser: (data: any) => request.put('/user', data),
  // 删除
  deleteUser: (id: number) => request.delete(`/user/${id}`),
  // 重置密码
  resetPassword: (id: number) => request.put(`/user/${id}/reset-password`),
  // 修改密码
  changePassword: (data: any) => request.put('/user/change-password', data)
}

// 角色管理
export const roleApi = {
  // 分页查询
  getRolePage: (params?: any) => request.get('/role/page', { params }),
  // 获取列表
  getRoleList: (params?: any) => request.get('/role/list', { params }),
  // 获取详情
  getRoleById: (id: number) => request.get(`/role/${id}`),
  // 创建
  createRole: (data: any) => request.post('/role', data),
  // 更新
  updateRole: (data: any) => request.put('/role', data),
  // 删除
  deleteRole: (id: number) => request.delete(`/role/${id}`),
  // 分配权限
  assignPermissions: (roleId: number, permissionIds: number[]) => request.put(`/role/${roleId}/permissions`, permissionIds),
  // 获取权限
  getPermissions: (roleId: number) => request.get(`/role/${roleId}/permissions`)
}
