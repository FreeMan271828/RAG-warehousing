import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import type { Component } from 'vue'

// 布局组件
import MainLayout from '@/layout/MainLayout.vue'

// 页面组件
const Dashboard = () => import('@/views/dashboard/index.vue')
const Warehouse3D = () => import('@/views/warehouse/Warehouse3D.vue')
const WarehouseLocation = () => import('@/views/warehouse/WarehouseLocation.vue')
const WarehousePallet = () => import('@/views/warehouse/WarehousePallet.vue')
const WarehouseBox = () => import('@/views/warehouse/WarehouseBox.vue')
const WarehouseOrder = () => import('@/views/warehouse/WarehouseOrder.vue')
const DeviceCategory = () => import('@/views/device/DeviceCategory.vue')
const DeviceModel = () => import('@/views/device/DeviceModel.vue')
const DeviceEquipment = () => import('@/views/device/DeviceEquipment.vue')
const DeviceElement = () => import('@/views/device/DeviceElement.vue')
const BatteryList = () => import('@/views/battery/BatteryList.vue')
const BatteryCharging = () => import('@/views/battery/BatteryCharging.vue')
const BatteryHistory = () => import('@/views/battery/BatteryHistory.vue')
const BatteryError = () => import('@/views/battery/BatteryError.vue')
const MaintenancePlan = () => import('@/views/maintenance/MaintenancePlan.vue')
const MaintenanceRecord = () => import('@/views/maintenance/MaintenanceRecord.vue')
const MaintainRecord = () => import('@/views/maintenance/MaintainRecord.vue')
const UsedElement = () => import('@/views/maintenance/UsedElement.vue')
const KeepPoint = () => import('@/views/maintenance/KeepPoint.vue')
const KnowledgeDoc = () => import('@/views/knowledge/Doc.vue')
const KnowledgeVideo = () => import('@/views/knowledge/Video.vue')
const SystemUser = () => import('@/views/system/User.vue')
const SystemRole = () => import('@/views/system/Role.vue')
const SystemDept = () => import('@/views/system/Dept.vue')
const Login = () => import('@/views/login/Login.vue')

export interface MenuRouteRecord extends RouteRecordRaw {
  meta?: {
    title?: string
    icon?: string
    roles?: string[]
  }
  children?: MenuRouteRecord[]
}

const routes: MenuRouteRecord[] = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '仪表盘', icon: 'DataAnalysis' }
      },
      {
        path: 'warehouse',
        name: 'Warehouse',
        meta: { title: '仓库管理', icon: 'Box' },
        children: [
          {
            path: '3d',
            name: 'Warehouse3D',
            component: Warehouse3D,
            meta: { title: '3D仓库', icon: 'View' }
          },
          {
            path: 'location',
            name: 'WarehouseLocation',
            component: WarehouseLocation,
            meta: { title: '货位管理', icon: 'Location' }
          },
          {
            path: 'pallet',
            name: 'WarehousePallet',
            component: WarehousePallet,
            meta: { title: '托盘管理', icon: 'Grid' }
          },
          {
            path: 'box',
            name: 'WarehouseBox',
            component: WarehouseBox,
            meta: { title: '箱子管理', icon: 'Box' }
          },
          {
            path: 'order',
            name: 'WarehouseOrder',
            component: WarehouseOrder,
            meta: { title: '出入库工单', icon: 'Document' }
          }
        ]
      },
      {
        path: 'device',
        name: 'Device',
        meta: { title: '设备管理', icon: 'Monitor' },
        children: [
          {
            path: 'category',
            name: 'DeviceCategory',
            component: DeviceCategory,
            meta: { title: '设备类别', icon: 'Folder' }
          },
          {
            path: 'model',
            name: 'DeviceModel',
            component: DeviceModel,
            meta: { title: '设备型号', icon: 'Cpu }
          },
          {
            path: 'equipment',
            name: 'DeviceEquipment',
            component: DeviceEquipment,
            meta: { title: '设备管理', icon: 'Monitor' }
          },
          {
            path: 'element',
            name: 'DeviceElement',
            component: DeviceElement,
            meta: { title: '零件管理', icon: 'Tools' }
          }
        ]
      },
      {
        path: 'battery',
        name: 'Battery',
        meta: { title: '电池管理', icon: 'Battery' },
        children: [
          {
            path: 'list',
            name: 'BatteryList',
            component: BatteryList,
            meta: { title: '电池列表', icon: 'List' }
          },
          {
            path: 'charging',
            name: 'BatteryCharging',
            component: BatteryCharging,
            meta: { title: '实时充电', icon: 'Lightning' }
          },
          {
            path: 'history',
            name: 'BatteryHistory',
            component: BatteryHistory,
            meta: { title: '充电历史', icon: 'Clock' }
          },
          {
            path: 'error',
            name: 'BatteryError',
            component: BatteryError,
            meta: { title: '故障记录', icon: 'Warning' }
          }
        ]
      },
      {
        path: 'maintenance',
        name: 'Maintenance',
        meta: { title: '运维管理', icon: 'Tools' },
        children: [
          {
            path: 'plan',
            name: 'MaintenancePlan',
            component: MaintenancePlan,
            meta: { title: '保养计划', icon: 'Calendar' }
          },
          {
            path: 'record',
            name: 'MaintenanceRecord',
            component: MaintenanceRecord,
            meta: { title: '保养记录', icon: 'Document' }
          },
          {
            path: 'maintain',
            name: 'MaintainRecord',
            component: MaintainRecord,
            meta: { title: '维修记录', icon: 'Wrench' }
          },
          {
            path: 'used-element',
            name: 'UsedElement',
            component: UsedElement,
            meta: { title: '零件更换', icon: 'Refresh' }
          },
          {
            path: 'keep-point',
            name: 'KeepPoint',
            component: KeepPoint,
            meta: { title: '保养点配置', icon: 'Setting' }
          }
        ]
      },
      {
        path: 'knowledge',
        name: 'Knowledge',
        meta: { title: '知识库', icon: 'Reading' },
        children: [
          {
            path: 'doc',
            name: 'KnowledgeDoc',
            component: KnowledgeDoc,
            meta: { title: '文档管理', icon: 'Document' }
          },
          {
            path: 'video',
            name: 'KnowledgeVideo',
            component: KnowledgeVideo,
            meta: { title: '视频管理', icon: 'VideoCamera' }
          }
        ]
      },
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'user',
            name: 'SystemUser',
            component: SystemUser,
            meta: { title: '用户管理', icon: 'User' }
          },
          {
            path: 'role',
            name: 'SystemRole',
            component: SystemRole,
            meta: { title: '角色管理', icon: 'UserFilled' }
          },
          {
            path: 'dept',
            name: 'SystemDept',
            component: SystemDept,
            meta: { title: '部门管理', icon: 'OfficeBuilding' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

// 菜单数据，用于生成侧边栏菜单
export const menuRoutes = routes.find(r => r.path === '/')?.children?.filter(child => child.path !== 'login') || []
