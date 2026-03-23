import request from './request'

// 电池管理API

// 电池基本信息
export const batteryApi = {
  // 分页查询
  getBatteryPage: (params?: any) => request.get('/battery/page', { params }),
  // 获取列表
  getBatteryList: (params?: any) => request.get('/battery/list', { params }),
  // 获取正在充电的电池
  getChargingList: () => request.get('/battery/charging'),
  // 获取详情
  getBatteryById: (id: number) => request.get(`/battery/${id}`),
  // 删除
  deleteBattery: (id: number) => request.delete(`/battery/${id}`)
}

// 充电实时数据
export const chargingNowApi = {
  // 获取所有正在充电的记录
  getChargingList: () => request.get('/battery/charging-now/list'),
  // 根据电池ID获取实时充电数据
  getChargingByBatteryId: (batteryId: number) => request.get(`/battery/charging-now/battery/${batteryId}`)
}

// 充电历史记录
export const chargeHistoryApi = {
  // 分页查询
  getHistoryPage: (params?: any) => request.get('/battery/history/page', { params }),
  // 获取列表
  getHistoryList: (params?: any) => request.get('/battery/history/list', { params }),
  // 获取详情
  getHistoryById: (id: number) => request.get(`/battery/history/${id}`),
  // 删除
  deleteHistory: (id: number) => request.delete(`/battery/history/${id}`),
  // 获取电池最近的充电历史
  getLatestByBatteryId: (batteryId: number) => request.get(`/battery/history/battery/${batteryId}/latest`)
}

// 充电故障记录
export const chargeErrorApi = {
  // 分页查询
  getErrorPage: (params?: any) => request.get('/battery/error/page', { params }),
  // 获取列表
  getErrorList: (params?: any) => request.get('/battery/error/list', { params }),
  // 获取未解决的故障
  getUnresolvedList: () => request.get('/battery/error/unresolved'),
  // 获取详情
  getErrorById: (id: number) => request.get(`/battery/error/${id}`),
  // 删除
  deleteError: (id: number) => request.delete(`/battery/error/${id}`)
}
