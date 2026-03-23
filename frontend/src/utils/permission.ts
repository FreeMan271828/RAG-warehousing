/**
 * 权限工具类
 * 用于判断当前用户是否有权限进行某些操作
 * 基于数据库中的 permission_code 进行权限判断
 */

import { getUserInfo } from './permission'

// 用户信息接口
export interface UserInfo {
  id: number
  username: string
  realName: string
  roles: string[]
  roleCodes: string[]
  permissions: string[]
  permissionCodes: string[]
}

/**
 * 获取当前用户信息
 */
export const getCurrentUserInfo = (): UserInfo | null => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (!userInfoStr) return null
  try {
    return JSON.parse(userInfoStr)
  } catch {
    return null
  }
}

/**
 * 设置用户信息
 */
export const setCurrentUserInfo = (userInfo: UserInfo) => {
  localStorage.setItem('userInfo', JSON.stringify(userInfo))
}

/**
 * 判断用户是否拥有指定权限
 * @param permission 权限编码，如 'warehouse:location:add'
 */
export const hasPermission = (permission: string): boolean => {
  const userInfo = getCurrentUserInfo()
  if (!userInfo) return false
  
  // 系统管理员拥有所有权限
  if (userInfo.roleCodes.includes('admin')) return true
  
  // 检查用户是否有该权限
  return userInfo.permissionCodes?.includes(permission) || false
}

/**
 * 判断用户是否拥有指定模块的任何权限
 * @param module 模块名称，如 'warehouse'
 */
export const hasModulePermission = (module: string): boolean => {
  const userInfo = getCurrentUserInfo()
  if (!userInfo) return false
  
  // 系统管理员拥有所有模块权限
  if (userInfo.roleCodes.includes('admin')) return true
  
  // 检查用户是否有该模块的任意权限
  const permissions = userInfo.permissionCodes || []
  return permissions.some(p => p.startsWith(module + ':'))
}

/**
 * 判断用户是否可以执行新增操作
 * @param module 模块名称
 */
export const canAdd = (module: string): boolean => {
  return hasPermission(module + ':add')
}

/**
 * 判断用户是否可以执行编辑操作
 * @param module 模块名称
 */
export const canEdit = (module: string): boolean => {
  return hasPermission(module + ':edit')
}

/**
 * 判断用户是否可以执行删除操作
 * @param module 模块名称
 */
export const canDelete = (module: string): boolean => {
  return hasPermission(module + ':delete')
}

/**
 * 判断用户是否可以查看
 * 所有登录用户都可以查看
 */
export const canView = (): boolean => {
  return !!localStorage.getItem('token')
}

/**
 * 获取用户所有权限编码
 */
export const getAllPermissions = (): string[] => {
  const userInfo = getCurrentUserInfo()
  if (!userInfo) return []
  
  // 系统管理员返回所有权限
  if (userInfo.roleCodes.includes('admin')) {
    return ['*'] // 返回 * 表示所有权限
  }
  
  return userInfo.permissionCodes || []
}

/**
 * 判断是否是系统管理员
 */
export const isAdmin = (): boolean => {
  const userInfo = getCurrentUserInfo()
  if (!userInfo) return false
  return userInfo.roleCodes.includes('admin')
}
