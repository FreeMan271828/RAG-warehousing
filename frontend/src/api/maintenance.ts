import request from './request'

// 保养管理API

// 保养计划管理
export const maintenancePlanApi = {
  // 分页查询
  getPlanPage: (params?: any) => request.get('/maintenance/plan/page', { params }),
  // 获取列表（获取所有启用的保养计划）
  getPlanList: (params?: any) => request.get('/maintenance/plan/list', { params }),
  // 获取详情
  getPlanById: (id: number) => request.get(`/maintenance/plan/${id}`),
  // 删除
  deletePlan: (id: number) => request.delete(`/maintenance/plan/${id}`)
}

// 保养记录管理
export const maintenanceRecordApi = {
  // 分页查询
  getRecordPage: (params?: any) => request.get('/maintenance/record/page', { params }),
  // 获取列表
  getRecordList: (params?: any) => request.get('/maintenance/record/list', { params }),
  // 获取详情
  getRecordById: (id: number) => request.get(`/maintenance/record/${id}`),
  // 删除
  deleteRecord: (id: number) => request.delete(`/maintenance/record/${id}`)
}

// 保养点管理
export const keepPointApi = {
  // 分页查询
  getKeepPointPage: (params?: any) => request.get('/maintenance/keep-point/page', { params }),
  // 获取列表（获取所有启用的保养点）
  getKeepPointList: (params?: any) => request.get('/maintenance/keep-point/list', { params }),
  // 获取详情
  getKeepPointById: (id: number) => request.get(`/maintenance/keep-point/${id}`),
  // 删除
  deleteKeepPoint: (id: number) => request.delete(`/maintenance/keep-point/${id}`)
}

// 维修记录管理
export const maintainRecordApi = {
  // 分页查询
  getMaintainPage: (params?: any) => request.get('/maintenance/maintain/page', { params }),
  // 获取列表
  getMaintainList: (params?: any) => request.get('/maintenance/maintain/list', { params }),
  // 获取详情
  getMaintainById: (id: number) => request.get(`/maintenance/maintain/${id}`),
  // 删除
  deleteMaintain: (id: number) => request.delete(`/maintenance/maintain/${id}`)
}

// 零件更换记录管理
export const usedElementApi = {
  // 分页查询
  getUsedElementPage: (params?: any) => request.get('/maintenance/used-element/page', { params }),
  // 获取列表
  getUsedElementList: (params?: any) => request.get('/maintenance/used-element/list', { params }),
  // 获取详情
  getUsedElementById: (id: number) => request.get(`/maintenance/used-element/${id}`),
  // 删除
  deleteUsedElement: (id: number) => request.delete(`/maintenance/used-element/${id}`)
}
