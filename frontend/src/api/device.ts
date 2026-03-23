import request from './request'

// 设备管理API

// 设备类别
export const deviceCategoryApi = {
  // 分页查询
  getCategoryPage: (params?: any) => request.get('/device-category/page', { params }),
  // 获取列表
  getCategoryList: (params?: any) => request.get('/device-category/list', { params }),
  // 获取顶级类别
  getTopList: () => request.get('/device-category/top'),
  // 获取详情
  getCategoryById: (id: number) => request.get(`/device-category/${id}`),
  // 更新
  updateCategory: (data: any) => request.put('/device-category', data),
  // 删除
  deleteCategory: (id: number) => request.delete(`/device-category/${id}`)
}

// 设备型号
export const deviceModelApi = {
  // 分页查询
  getModelPage: (params?: any) => request.get('/device-model/page', { params }),
  // 获取列表
  getModelList: (params?: any) => request.get('/device-model/list', { params }),
  // 根据类别ID获取设备型号
  getByCategoryId: (categoryId: number) => request.get(`/device-model/by-category/${categoryId}`),
  // 获取详情
  getModelById: (id: number) => request.get(`/device-model/${id}`),
  // 更新
  updateModel: (data: any) => request.put('/device-model', data),
  // 删除
  deleteModel: (id: number) => request.delete(`/device-model/${id}`)
}

// 设备管理
export const equipmentApi = {
  // 分页查询
  getEquipmentPage: (params?: any) => request.get('/equipment/page', { params }),
  // 获取列表
  getEquipmentList: (params?: any) => request.get('/equipment/list', { params }),
  // 获取所有设备（用于3D可视化）
  getAllEquipment: () => request.get('/equipment/all'),
  // 获取详情
  getEquipmentById: (id: number) => request.get(`/equipment/${id}`),
  // 更新
  updateEquipment: (data: any) => request.put('/equipment', data),
  // 删除
  deleteEquipment: (id: number) => request.delete(`/equipment/${id}`)
}

// 零件管理
export const elementApi = {
  // 分页查询
  getElementPage: (params?: any) => request.get('/element/page', { params }),
  // 获取列表
  getElementList: (params?: any) => request.get('/element/list', { params }),
  // 获取库存不足的零件
  getLowStock: () => request.get('/element/low-stock'),
  // 获取详情
  getElementById: (id: number) => request.get(`/element/${id}`),
  // 更新
  updateElement: (data: any) => request.put('/element', data),
  // 删除
  deleteElement: (id: number) => request.delete(`/element/${id}`)
}
