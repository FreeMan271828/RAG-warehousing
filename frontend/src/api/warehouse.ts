import request from './request'

// ==================== 货位管理 ====================

// 货位管理
export const warehouseLocationApi = {
  // 分页查询
  getLocationPage: (params?: any) => request.get('/warehouse/location/page', { params }),
  // 获取列表
  getLocationList: (params?: any) => request.get('/warehouse/location/list', { params }),
  // 获取空货位
  getEmptyLocations: () => request.get('/warehouse/location/empty'),
  // 获取详情
  getLocationById: (id: number) => request.get(`/warehouse/location/${id}`),
  // 创建
  createLocation: (data: any) => request.post('/warehouse/location', data),
  // 更新
  updateLocation: (data: any) => request.put('/warehouse/location', data),
  // 删除
  deleteLocation: (id: number) => request.delete(`/warehouse/location/${id}`)
}

// ==================== 托盘管理 ====================

// 托盘管理
export const palletApi = {
  // 分页查询
  getPalletPage: (params?: any) => request.get('/warehouse/pallet/page', { params }),
  // 获取列表
  getPalletList: (params?: any) => request.get('/warehouse/pallet/list', { params }),
  // 获取详情
  getPalletById: (id: number) => request.get(`/warehouse/pallet/${id}`),
  // 创建
  createPallet: (data: any) => request.post('/warehouse/pallet', data),
  // 更新
  updatePallet: (data: any) => request.put('/warehouse/pallet', data),
  // 删除
  deletePallet: (id: number) => request.delete(`/warehouse/pallet/${id}`)
}

// ==================== 箱子管理 ====================

// 箱子管理
export const boxApi = {
  // 分页查询
  getBoxPage: (params?: any) => request.get('/warehouse/box/page', { params }),
  // 获取列表
  getBoxList: (params?: any) => request.get('/warehouse/box/list', { params }),
  // 获取详情
  getBoxById: (id: number) => request.get(`/warehouse/box/${id}`),
  // 创建
  createBox: (data: any) => request.post('/warehouse/box', data),
  // 更新
  updateBox: (data: any) => request.put('/warehouse/box', data),
  // 删除
  deleteBox: (id: number) => request.delete(`/warehouse/box/${id}`)
}

// ==================== 出入库工单管理 ====================

// 出入库工单
export const inOutOrderApi = {
  // 分页查询
  getInOutPage: (params?: any) => request.get('/warehouse/inout/page', { params }),
  // 获取列表
  getInOutList: (params?: any) => request.get('/warehouse/inout/list', { params }),
  // 获取详情
  getInOutById: (id: number) => request.get(`/warehouse/inout/${id}`),
  // 创建
  createInOut: (data: any) => request.post('/warehouse/inout', data),
  // 更新
  updateInOut: (data: any) => request.put('/warehouse/inout', data),
  // 删除
  deleteInOut: (id: number) => request.delete(`/warehouse/inout/${id}`),
  // 推进工单状态
  transitionStatus: (id: number, targetStatus: string, operatorId: number, operatorName: string, remark?: string) => 
    request.put(`/warehouse/inout/${id}/transition`, null, { params: { targetStatus, operatorId, operatorName, remark } }),
  // 审核工单
  reviewOrder: (id: number, approved: boolean, reviewerId: number, reviewerName: string, remark?: string) =>
    request.put(`/warehouse/inout/${id}/review`, null, { params: { approved, reviewerId, reviewerName, remark } }),
  // 获取可用的操作列表
  getAvailableActions: (id: number) => request.get(`/warehouse/inout/${id}/actions`),
  // 获取所有状态列表
  getAllStatuses: () => request.get('/warehouse/inout/statuses')
}

// ==================== 仓库统计 ====================

// 仓库统计
export const warehouseStatisticsApi = {
  // 获取概览
  getOverview: () => request.get('/warehouse/location/statistics/overview')
}

// ==================== AGV小车管理 ====================

// AGV小车
export const agvApi = {
  // 获取列表
  getAgvList: () => request.get('/warehouse/agv/list'),
  // 获取可用小车
  getAvailableAgvs: () => request.get('/warehouse/agv/available'),
  // 获取详情
  getAgvById: (id: number) => request.get(`/warehouse/agv/${id}`),
  // 更新电量
  updateBattery: (agvId: number, batteryLevel: number) => 
    request.put(`/warehouse/agv/battery/${agvId}`, null, { params: { batteryLevel } }),
  // 更新状态
  updateStatus: (agvId: number, status: string) => 
    request.put(`/warehouse/agv/status/${agvId}`, null, { params: { status } }),
  // 开始充电
  startCharging: (agvId: number, stationId: number) => 
    request.post('/warehouse/agv/charge/start', null, { params: { agvId, stationId } }),
  // 结束充电
  endCharging: (agvId: number, result?: string, interruptReason?: string) => 
    request.post('/warehouse/agv/charge/end', null, { params: { agvId, result, interruptReason } }),
  // 检查是否需要充电
  needsCharging: (agvId: number) => request.get(`/warehouse/agv/needs-charge/${agvId}`)
}

// 充电站
export const chargingStationApi = {
  // 获取列表
  getStationList: () => request.get('/warehouse/charging-station/list'),
  // 分页查询
  getStationPage: (params?: any) => request.get('/warehouse/charging-station/page', { params }),
  // 获取可用充电站
  getAvailableStations: () => request.get('/warehouse/charging-station/available'),
  // 获取详情
  getStationById: (id: number) => request.get(`/warehouse/charging-station/${id}`),
  // 创建
  createStation: (data: any) => request.post('/warehouse/charging-station', data),
  // 更新
  updateStation: (data: any) => request.put('/warehouse/charging-station', data),
  // 删除
  deleteStation: (id: number) => request.delete(`/warehouse/charging-station/${id}`)
}
