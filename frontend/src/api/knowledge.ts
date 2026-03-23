import request from './request'

// 知识库管理API

// 文档管理
export const guideDocApi = {
  // 分页查询
  getDocPage: (params?: any) => request.get('/knowledge/doc/page', { params }),
  // 获取列表（用于下拉选择）
  getDocList: (params?: any) => request.get('/knowledge/doc/list', { params }),
  // 获取详情
  getDocById: (id: number) => request.get(`/knowledge/doc/${id}`),
  // 创建
  createDoc: (data: any) => request.post('/knowledge/doc', data),
  // 更新
  updateDoc: (data: any) => request.put('/knowledge/doc', data),
  // 删除
  deleteDoc: (id: number) => request.delete(`/knowledge/doc/${id}`)
}

// 视频管理
export const guideVideoApi = {
  // 分页查询
  getVideoPage: (params?: any) => request.get('/knowledge/video/page', { params }),
  // 获取列表（用于下拉选择）
  getVideoList: (params?: any) => request.get('/knowledge/video/list', { params }),
  // 获取详情
  getVideoById: (id: number) => request.get(`/knowledge/video/${id}`),
  // 创建
  createVideo: (data: any) => request.post('/knowledge/video', data),
  // 更新
  updateVideo: (data: any) => request.put('/knowledge/video', data),
  // 删除
  deleteVideo: (id: number) => request.delete(`/knowledge/video/${id}`)
}
