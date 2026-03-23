<template>
  <div class="warehouse-3d-container">
    <!-- 3D视图区域 -->
    <div class="view-panel">
      <div ref="canvasContainer" class="canvas-container"></div>
      
      <!-- 视图控制工具栏 -->
      <div class="view-controls">
        <el-tooltip content="重置视角" placement="top">
          <el-button circle @click="resetCamera">
            <el-icon><Refresh /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="俯视图" placement="top">
          <el-button circle @click="setTopView">
            <el-icon><Top /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="前视图" placement="top">
          <el-button circle @click="setFrontView">
            <el-icon><View /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="侧视图" placement="top">
          <el-button circle @click="setSideView">
            <el-icon><View /></el-icon>
          </el-button>
        </el-tooltip>
        <el-divider direction="vertical" />
        <el-tooltip content="自动旋转" placement="top">
          <el-button circle :type="autoRotate ? 'primary' : ''" @click="toggleAutoRotate">
            <el-icon><RefreshRight /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
      
      <!-- 图例 -->
      <div class="legend">
        <div class="legend-item">
          <span class="legend-color empty"></span>
          <span>空闲 ({{ stats.empty }})</span>
        </div>
        <div class="legend-item">
          <span class="legend-color occupied"></span>
          <span>占用 ({{ stats.occupied }})</span>
        </div>
        <div class="legend-item">
          <span class="legend-color reserved"></span>
          <span>预留 ({{ stats.reserved }})</span>
        </div>
      </div>
      
      <!-- 选中货位信息 -->
      <transition name="slide-fade">
        <div v-if="selectedLocation" class="selected-info">
          <div class="info-header">
            <span class="info-title">{{ selectedLocation.locationCode }}</span>
            <el-button text @click="selectedLocation = null">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="info-content">
            <div class="info-row">
              <span class="label">区域:</span>
              <span>{{ selectedLocation.areaName }}</span>
            </div>
            <div class="info-row">
              <span class="label">位置:</span>
              <span>行{{ selectedLocation.rowNum }} 列{{ selectedLocation.colNum }} 层{{ selectedLocation.levelNum }}</span>
            </div>
            <div class="info-row">
              <span class="label">状态:</span>
              <el-tag :type="getStatusType(selectedLocation.currentStatus)">
                {{ getStatusText(selectedLocation.currentStatus) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">类型:</span>
              <span>{{ getLocationTypeText(selectedLocation.locationType) }}</span>
            </div>
          </div>
        </div>
      </transition>
      
      <!-- 选中AGV信息 -->
      <transition name="slide-fade">
        <div v-if="selectedAgv" class="selected-info agv-info">
          <div class="info-header">
            <span class="info-title">{{ selectedAgv.equipmentCode }}</span>
            <el-button text @click="selectedAgv = null">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="info-content">
            <div class="info-row">
              <span class="label">名称:</span>
              <span>{{ selectedAgv.equipmentName }}</span>
            </div>
            <div class="info-row">
              <span class="label">状态:</span>
              <el-tag :type="getAgvStatusType(selectedAgv.agvStatus)">
                {{ getAgvStatusText(selectedAgv.agvStatus) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">电量:</span>
              <el-progress 
                :percentage="selectedAgv.batteryLevel" 
                :color="getBatteryColor(selectedAgv.batteryLevel)"
                :stroke-width="10"
                style="width: 120px"
              />
              <span style="margin-left: 8px">{{ selectedAgv.batteryLevel }}%</span>
            </div>
            <div class="info-row">
              <span class="label">当前位置:</span>
              <span>{{ selectedAgv.currentLocation }}</span>
            </div>
            <div class="info-row">
              <span class="label">目标位置:</span>
              <span>{{ selectedAgv.targetLocation || '无' }}</span>
            </div>
            <div class="info-row">
              <span class="label">累计距离:</span>
              <span>{{ selectedAgv.totalDistance || 0 }} 米</span>
            </div>
          </div>
        </div>
      </transition>
      
      <!-- 选中充电站信息 -->
      <transition name="slide-fade">
        <div v-if="selectedStation" class="selected-info station-info">
          <div class="info-header">
            <span class="info-title">{{ selectedStation.stationCode }}</span>
            <el-button text @click="selectedStation = null">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="info-content">
            <div class="info-row">
              <span class="label">名称:</span>
              <span>{{ selectedStation.stationName }}</span>
            </div>
            <div class="info-row">
              <span class="label">位置:</span>
              <span>{{ selectedStation.location }}</span>
            </div>
            <div class="info-row">
              <span class="label">状态:</span>
              <el-tag :type="selectedStation.status === 1 ? 'success' : selectedStation.status === 2 ? 'warning' : 'info'">
                {{ selectedStation.status === 0 ? '禁用' : selectedStation.status === 1 ? '启用' : '使用中' }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">功率:</span>
              <span>{{ selectedStation.power }}W</span>
            </div>
            <div class="info-row">
              <span class="label">电压:</span>
              <span>{{ selectedStation.voltage }}V</span>
            </div>
            <div class="info-row">
              <span class="label">描述:</span>
              <span>{{ selectedStation.description || '无' }}</span>
            </div>
          </div>
        </div>
      </transition>
    </div>
    
    <!-- 右侧统计面板 -->
    <div class="stats-panel">
      <div class="panel-header">
        <h3>仓库概览</h3>
        <el-button type="primary" link @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon total">
            <el-icon><Box /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">货位总数</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon empty">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.empty }}</div>
            <div class="stat-label">空闲货位</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon occupied">
            <el-icon><Box /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.occupied }}</div>
            <div class="stat-label">占用货位</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon reserved">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.reserved }}</div>
            <div class="stat-label">预留货位</div>
          </div>
        </div>
      </div>
      
      <!-- 使用率图表 -->
      <div class="usage-chart">
        <h4>库位使用率</h4>
        <div class="progress-wrapper">
          <el-progress
            :percentage="usageRate"
            :color="getProgressColor(usageRate)"
            :stroke-width="20"
            :text-inside="true"
          />
        </div>
      </div>
      
      <!-- 区域分布 -->
      <div class="area-distribution">
        <h4>区域分布</h4>
        <div class="area-list">
          <div v-for="area in areaStats" :key="area.areaCode" class="area-item">
            <div class="area-header">
              <span class="area-name">{{ area.areaName || area.areaCode }}</span>
              <span class="area-count">{{ area.count }}</span>
            </div>
            <el-progress
              :percentage="stats.total > 0 ? (area.count / stats.total) * 100 : 0"
              :show-text="false"
              :stroke-width="8"
              color="#409eff"
            />
          </div>
        </div>
      </div>
      
      <!-- 最近操作 -->
      <div class="recent-operations">
        <h4>最近操作</h4>
        <el-timeline v-if="recentOperations.length > 0">
          <el-timeline-item
            v-for="(item, index) in recentOperations"
            :key="index"
            :timestamp="item.time"
            placement="top"
            :type="item.type"
          >
            {{ item.content }}
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无最近操作" :image-size="60" />
      </div>
      
      <!-- AGV小车统计 -->
      <div class="agv-stats">
        <h4>AGV小车</h4>
        <div class="agv-stats-grid">
          <div class="agv-stat-item">
            <div class="agv-stat-icon total">
              <el-icon><Van /></el-icon>
            </div>
            <div class="agv-stat-info">
              <div class="agv-stat-value">{{ agvStats.total }}</div>
              <div class="agv-stat-label">总数</div>
            </div>
          </div>
          <div class="agv-stat-item">
            <div class="agv-stat-icon idle">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="agv-stat-info">
              <div class="agv-stat-value">{{ agvStats.idle }}</div>
              <div class="agv-stat-label">空闲</div>
            </div>
          </div>
          <div class="agv-stat-item">
            <div class="agv-stat-icon working">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="agv-stat-info">
              <div class="agv-stat-value">{{ agvStats.working }}</div>
              <div class="agv-stat-label">工作中</div>
            </div>
          </div>
          <div class="agv-stat-item">
            <div class="agv-stat-icon charging">
              <el-icon><Lightning /></el-icon>
            </div>
            <div class="agv-stat-info">
              <div class="agv-stat-value">{{ agvStats.charging }}</div>
              <div class="agv-stat-label">充电中</div>
            </div>
          </div>
        </div>
        
        <!-- AGV列表 -->
        <div class="agv-list">
          <div 
            v-for="agv in agvList" 
            :key="agv.agvId" 
            class="agv-item"
          >
            <div class="agv-info">
              <span class="agv-code">{{ agv.equipmentCode }}</span>
              <el-tag :type="getAgvStatusType(agv.agvStatus)" size="small">
                {{ getAgvStatusText(agv.agvStatus) }}
              </el-tag>
            </div>
            <div class="agv-battery">
              <el-progress 
                :percentage="agv.batteryLevel" 
                :color="getBatteryColor(agv.batteryLevel)"
                :stroke-width="6"
                :show-text="false"
              />
              <span class="battery-text">{{ agv.batteryLevel }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, reactive } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import { EffectComposer } from 'three/examples/jsm/postprocessing/EffectComposer'
import { RenderPass } from 'three/examples/jsm/postprocessing/RenderPass'
import { UnrealBloomPass } from 'three/examples/jsm/postprocessing/UnrealBloomPass'
import { OutputPass } from 'three/examples/jsm/postprocessing/OutputPass'
import { ElMessage } from 'element-plus'
import { Van, CircleCheck, Loading, Lightning } from '@element-plus/icons-vue'
import request, { Result } from '@/api/request'
import { agvApi, chargingStationApi } from '@/api/warehouse'

// 类型定义
interface WarehouseLocation {
  id: number
  locationCode: string
  locationName: string
  areaCode: string
  areaName: string
  rowNum: number
  colNum: number
  levelNum: number
  xPosition: number
  yPosition: number
  zPosition: number
  locationType: string
  status: number
  currentStatus: string
  capacity: number
  maxWeight: number
  description: string
}

interface LocationStats {
  total: number
  empty: number
  occupied: number
  reserved: number
}

interface AreaStat {
  areaCode: string
  areaName: string
  count: number
}

interface RecentOperation {
  time: string
  content: string
  type: string
}

// 响应式数据
const canvasContainer = ref<HTMLDivElement>()
const locations = ref<WarehouseLocation[]>([])
const selectedLocation = ref<WarehouseLocation | null>(null)

// 选中的AGV和充电站
const selectedAgv = ref<any | null>(null)
const selectedStation = ref<any | null>(null)
const autoRotate = ref(false)
const loading = ref(false)

const stats = reactive<LocationStats>({
  total: 0,
  empty: 0,
  occupied: 0,
  reserved: 0
})

const areaStats = ref<AreaStat[]>([])

const recentOperations = ref<RecentOperation[]>([])

// AGV相关数据
const agvList = ref<any[]>([])
const stationList = ref<any[]>([])

const agvStats = reactive({
  total: 0,
  idle: 0,
  working: 0,
  charging: 0,
  fault: 0
})

// 加载AGV数据
const loadAgvData = async () => {
  try {
    const [agvRes, stationRes] = await Promise.all([
      agvApi.getAgvList(),
      chargingStationApi.getStationList()
    ])
    agvList.value = agvRes.data || []
    stationList.value = stationRes.data || []
    
    // 更新AGV统计
    agvStats.total = agvList.value.length
    agvStats.idle = agvList.value.filter((a: any) => a.agvStatus === 'idle').length
    agvStats.working = agvList.value.filter((a: any) => a.agvStatus === 'working').length
    agvStats.charging = agvList.value.filter((a: any) => a.agvStatus === 'charging').length
    agvStats.fault = agvList.value.filter((a: any) => a.agvStatus === 'fault').length
    
    // 初始化货物模型
    if (scene && !cargoMesh) {
      initCargoModel()
    }
    
    // 创建3D模型
    if (scene) {
      createAgvModelsFromData()
    }
  } catch (error) {
    console.error('获取AGV数据失败:', error)
  }
}

// Three.js 相关
let scene: THREE.Scene
let camera: THREE.PerspectiveCamera
let renderer: THREE.WebGLRenderer
let controls: OrbitControls
let composer: EffectComposer
let animationId: number
const raycaster = new THREE.Raycaster()
const mouse = new THREE.Vector2()
const locationMeshes: Map<number, THREE.Mesh> = new Map()
const locationEdges: Map<number, THREE.LineSegments> = new Map()

// AGV模型
const agvMeshes: Map<number, THREE.Group> = new Map()
const stationMeshes: Map<number, THREE.Mesh> = new Map()

// AGV移动动画相关状态
interface AgvAnimation {
  agvId: number
  fromX: number
  fromZ: number
  toX: number
  toZ: number
  progress: number
  speed: number
  phase: 'moving' | 'loading' | 'unloading' | 'complete'
  cargoMesh?: THREE.Group
}

const agvAnimations: Map<number, AgvAnimation> = new Map()

// 货物模型
let cargoMesh: THREE.Group | null = null

// 创建货物模型
const createCargoModel = (): THREE.Group => {
  const group = new THREE.Group()
  
  // 托盘
  const palletGeometry = new THREE.BoxGeometry(1.2, 0.1, 1.2)
  const palletMaterial = new THREE.MeshStandardMaterial({ color: 0x8b4513, roughness: 0.8 })
  const pallet = new THREE.Mesh(palletGeometry, palletMaterial)
  pallet.position.y = 0.05
  group.add(pallet)
  
  // 箱子
  const boxGeometry = new THREE.BoxGeometry(1, 0.8, 1)
  const boxMaterial = new THREE.MeshStandardMaterial({ 
    color: 0xdeb887, 
    roughness: 0.5,
    metalness: 0.1
  })
  const box = new THREE.Mesh(boxGeometry, boxMaterial)
  box.position.y = 0.5
  group.add(box)
  
  return group
}

// 初始化货物模型
const initCargoModel = () => {
  cargoMesh = createCargoModel()
  cargoMesh.visible = false
  scene.add(cargoMesh)
}

// 开始AGV移动动画
const startAgvAnimation = (agvId: number, fromX: number, fromZ: number, toX: number, toZ: number) => {
  const animation: AgvAnimation = {
    agvId,
    fromX,
    fromZ,
    toX,
    toZ,
    progress: 0,
    speed: 0.02, // 移动速度
    phase: 'moving'
  }
  
  agvAnimations.set(agvId, animation)
  
  // 显示货物（如果是工作任务）
  if (cargoMesh) {
    cargoMesh.visible = true
    cargoMesh.position.set(fromX, 0, fromZ)
  }
}

// 更新AGV动画
const updateAgvAnimations = (delta: number) => {
  agvAnimations.forEach((animation, agvId) => {
    const agvMesh = agvMeshes.get(agvId)
    if (!agvMesh) return
    
    if (animation.phase === 'moving') {
      // 更新移动进度
      animation.progress += animation.speed * delta
      
      // 计算当前位置
      const currentX = animation.fromX + (animation.toX - animation.fromX) * animation.progress
      const currentZ = animation.fromZ + (animation.toZ - animation.fromZ) * animation.progress
      
      // 更新AGV位置
      agvMesh.position.set(currentX, 0, currentZ)
      
      // 更新货物位置
      if (cargoMesh) {
        cargoMesh.position.set(currentX, 0, currentZ)
      }
      
      // 计算朝向
      const dx = animation.toX - animation.fromX
      const dz = animation.toZ - animation.fromZ
      if (dx !== 0 || dz !== 0) {
        const targetRotation = Math.atan2(dx, dz)
        agvMesh.rotation.y = targetRotation
      }
      
      // 到达目标位置
      if (animation.progress >= 1) {
        animation.progress = 1
        animation.phase = 'loading' // 开始装货/卸货动画
        setTimeout(() => {
          animation.phase = 'complete'
          agvAnimations.delete(agvId)
        }, 1000) // 1秒装卸货时间
      }
    }
  })
}

// AGV状态颜色
const AGV_COLORS: Record<string, number> = {
  idle: 0x67c23a,      // 绿色 - 空闲
  working: 0x409eff,   // 蓝色 - 工作中
  charging: 0xe6a23c,  // 黄色 - 充电中
  returning: 0xf39c12, // 橙色 - 返回中
  fault: 0xf56c6c      // 红色 - 故障
}

// 计算使用率
const usageRate = computed(() => {
  if (stats.total === 0) return 0
  return Math.round(((stats.total - stats.empty) / stats.total) * 100)
})

// 从后端获取货位数据
const loadLocationData = async () => {
  try {
    const res = await request.get<Result<WarehouseLocation[]>>('/warehouse/location/list')
    if (res.data) {
      locations.value = res.data
      // 根据真实数据更新统计
      updateStatsFromData()
      // 根据真实数据创建3D模型
      createWarehouseModelFromData()
    }
  } catch (error) {
    console.error('获取货位数据失败:', error)
    ElMessage.error('获取货位数据失败')
  }
}

// 根据真实数据更新统计
const updateStatsFromData = () => {
  const data = locations.value
  stats.total = data.length
  stats.empty = data.filter(l => l.currentStatus === 'empty').length
  stats.occupied = data.filter(l => l.currentStatus === 'occupied').length
  stats.reserved = data.filter(l => l.currentStatus === 'reserved').length
  
  // 按区域统计
  const areaMap = new Map<string, AreaStat>()
  data.forEach(loc => {
    const areaCode = loc.areaCode || 'unknown'
    const existing = areaMap.get(areaCode)
    if (existing) {
      existing.count++
    } else {
      areaMap.set(areaCode, {
        areaCode,
        areaName: loc.areaName || `${areaCode}区`,
        count: 1
      })
    }
  })
  areaStats.value = Array.from(areaMap.values())
}

// 创建金属质感材质
const createMetalMaterial = (color: number, emissive: number = 0x000000) => {
  return new THREE.MeshStandardMaterial({
    color: color,
    metalness: 0.7,
    roughness: 0.3,
    emissive: emissive,
    emissiveIntensity: emissive ? 0.5 : 0
  })
}

// 从真实数据创建3D仓库模型
const createWarehouseModelFromData = () => {
  // 清除之前的模型
  clearScene()
  
  const locationSize = 2
  const gap = 0.15
  const frameWidth = 0.04
  
  // 按区域分组计算偏移 - 扩大间距
  const areas = [...new Set(locations.value.map(l => l.areaCode))]
  const areaOffsets: Record<string, number> = {}
  const areaColors: Record<string, number> = {
    'A': 0x3498db,  // 蓝色
    'B': 0x2ecc71,  // 绿色
    'C': 0xe74c3c,  // 红色
    'D': 0xf39c12,  // 橙色
    'E': 0x9b59b6,  // 紫色
    'F': 0x1abc9c,  // 青色
    'G': 0xe91e63,  // 粉色
    'H': 0x00bcd4   // 浅蓝
  }
  
  areas.forEach((area, index) => {
    areaOffsets[area] = (index - (areas.length - 1) / 2) * 14
  })
  
  // 状态颜色
  const statusColors: Record<string, number> = {
    'empty': 0x2ecc71,     // 绿色 - 空闲
    'occupied': 0x3498db,  // 蓝色 - 占用
    'reserved': 0xf39c12  // 橙色 - 预留
  }
  
  // 创建货位
  locations.value.forEach(loc => {
    const areaOffset = areaOffsets[loc.areaCode] || 0
    const baseColor = areaColors[loc.areaCode] || 0x3498db
    const statusColor = statusColors[loc.currentStatus] || statusColors['empty']
    
    // 创建货位主体
    const geometry = new THREE.BoxGeometry(
      locationSize - gap * 2,
      locationSize - gap * 2,
      locationSize - gap * 2
    )
    
    const material = new THREE.MeshStandardMaterial({
      color: statusColor,
      metalness: 0.5,
      roughness: 0.4,
      transparent: true,
      opacity: 0.85,
      emissive: statusColor,
      emissiveIntensity: 0.1
    })
    
    const cube = new THREE.Mesh(geometry, material)
    
    // 计算位置
    const x = ((loc.colNum || 1) - 2.5) * locationSize + areaOffset
    const y = ((loc.levelNum || 1) - 0.5) * locationSize
    const z = ((loc.rowNum || 1) - 2) * locationSize
    
    cube.position.set(x, y, z)
    cube.castShadow = true
    cube.receiveShadow = true
    
    // 存储货位信息
    cube.userData = {
      id: loc.id,
      locationCode: loc.locationCode,
      areaCode: loc.areaCode,
      areaName: loc.areaName,
      rowNum: loc.rowNum,
      colNum: loc.colNum,
      levelNum: loc.levelNum,
      currentStatus: loc.currentStatus || 'empty',
      locationType: loc.locationType || 'storage'
    }
    
    scene.add(cube)
    locationMeshes.set(loc.id, cube)
    
    // 添加边缘轮廓线
    const edgesGeometry = new THREE.EdgesGeometry(geometry)
    const edgesMaterial = new THREE.LineBasicMaterial({ 
      color: 0xffffff, 
      transparent: true, 
      opacity: 0.6 
    })
    const edges = new THREE.LineSegments(edgesGeometry, edgesMaterial)
    edges.position.copy(cube.position)
    scene.add(edges)
    locationEdges.set(loc.id, edges)
  })
  
  // 创建货架框架
  areas.forEach(area => {
    const areaOffset = areaOffsets[area]
    const areaLocations = locations.value.filter(l => l.areaCode === area)
    if (areaLocations.length === 0) return
    
    const maxRow = Math.max(...areaLocations.map(l => l.rowNum || 1))
    const maxCol = Math.max(...areaLocations.map(l => l.colNum || 1))
    const maxLevel = Math.max(...areaLocations.map(l => l.levelNum || 1))
    
    const shelfColor = 0x4a5568
    
    // 垂直支柱
    const pillarGeometry = new THREE.BoxGeometry(frameWidth, maxLevel * locationSize + 0.5, frameWidth)
    const pillarMaterial = createMetalMaterial(shelfColor)
    
    const positions = [
      [-maxCol * locationSize / 2 - 0.2, (maxLevel * locationSize) / 2, -maxRow * locationSize / 2 - 0.2],
      [-maxCol * locationSize / 2 - 0.2, (maxLevel * locationSize) / 2, maxRow * locationSize / 2 + 0.2],
      [maxCol * locationSize / 2 + 0.2, (maxLevel * locationSize) / 2, -maxRow * locationSize / 2 - 0.2],
      [maxCol * locationSize / 2 + 0.2, (maxLevel * locationSize) / 2, maxRow * locationSize / 2 + 0.2]
    ]
    
    positions.forEach(pos => {
      const pillar = new THREE.Mesh(pillarGeometry, pillarMaterial)
      pillar.position.set(pos[0] + areaOffset, pos[1], pos[2])
      pillar.castShadow = true
      pillar.receiveShadow = true
      scene.add(pillar)
      
      // 支柱边缘线
      const pillarEdges = new THREE.EdgesGeometry(pillarGeometry)
      const pillarLineMaterial = new THREE.LineBasicMaterial({ color: 0x718096, opacity: 0.5, transparent: true })
      const pillarLines = new THREE.LineSegments(pillarEdges, pillarLineMaterial)
      pillarLines.position.copy(pillar.position)
      scene.add(pillarLines)
    })
    
    // 横梁
    const beamGeometry = new THREE.BoxGeometry(maxCol * locationSize + 0.5, frameWidth, frameWidth)
    const beamMaterial = createMetalMaterial(shelfColor)
    
    for (let level = 1; level <= maxLevel; level++) {
      for (let row = 0; row <= maxRow; row++) {
        // 前横梁
        const frontBeam = new THREE.Mesh(beamGeometry, beamMaterial)
        frontBeam.position.set(areaOffset, (level - 0.5) * locationSize, (row - maxRow / 2 + 0.5) * locationSize + locationSize / 2 + 0.2)
        scene.add(frontBeam)
        
        // 后横梁
        const backBeam = new THREE.Mesh(beamGeometry, beamMaterial)
        backBeam.position.set(areaOffset, (level - 0.5) * locationSize, (row - maxRow / 2 + 0.5) * locationSize - locationSize / 2 - 0.2)
        scene.add(backBeam)
      }
    }
    
    // 区域标签
    createAreaLabel(area, areaOffset, maxRow, maxLevel, locationSize)
  })
}

// 创建区域标签
const createAreaLabel = (areaCode: string, xOffset: number, maxRow: number, maxLevel: number, locationSize: number) => {
  // 创建发光的地板区域
  const planeGeometry = new THREE.PlaneGeometry(maxRow * locationSize + 2, 4)
  const planeMaterial = new THREE.MeshBasicMaterial({
    color: 0x1a365d,
    transparent: true,
    opacity: 0.3,
    side: THREE.DoubleSide
  })
  const plane = new THREE.Mesh(planeGeometry, planeMaterial)
  plane.rotation.x = -Math.PI / 2
  plane.position.set(xOffset, 0.01, 0)
  scene.add(plane)
}

// 清除场景中的货位模型
const clearScene = () => {
  // 清除货位主体
  locationMeshes.forEach((mesh) => {
    scene.remove(mesh)
    mesh.geometry.dispose()
    ;(mesh.material as THREE.Material).dispose()
  })
  locationMeshes.clear()
  
  // 清除边缘线
  locationEdges.forEach((line) => {
    scene.remove(line)
    line.geometry.dispose()
    ;(line.material as THREE.Material).dispose()
  })
  locationEdges.clear()
  
  // 清除货架和标签
  const toRemove: THREE.Object3D[] = []
  scene.traverse((obj) => {
    if (obj instanceof THREE.Mesh && obj !== camera) {
      const material = obj.material as THREE.MeshStandardMaterial
      if (material.color && 
          (material.color.getHex() === 0x4a5568 || 
           material.color.getHex() === 0x1a365d ||
           obj.userData.isLabel)) {
        toRemove.push(obj)
      }
    }
    if (obj instanceof THREE.LineSegments) {
      toRemove.push(obj)
    }
  })
  toRemove.forEach(obj => {
    scene.remove(obj)
    if (obj instanceof THREE.Mesh) {
      obj.geometry.dispose()
      ;(obj.material as THREE.Material).dispose()
    }
  })
}

// 初始化Three.js场景
const initScene = () => {
  if (!canvasContainer.value) return
  
  // 创建场景
  scene = new THREE.Scene()
  
  // 添加雾效 - 营造深度感
  scene.fog = new THREE.FogExp2(0x0a0a1a, 0.005)
  
  // 渐变背景
  const canvas = document.createElement('canvas')
  canvas.width = 2
  canvas.height = 512
  const ctx = canvas.getContext('2d')!
  const gradient = ctx.createLinearGradient(0, 0, 0, 512)
  gradient.addColorStop(0, '#0f172a')   // 深蓝顶部
  gradient.addColorStop(0.5, '#1e293b') // 中间
  gradient.addColorStop(1, '#0a0a1a')   // 底部
  ctx.fillStyle = gradient
  ctx.fillRect(0, 0, 2, 512)
  const backgroundTexture = new THREE.CanvasTexture(canvas)
  scene.background = backgroundTexture
  
  // 创建相机
  const width = canvasContainer.value.clientWidth
  const height = canvasContainer.value.clientHeight
  camera = new THREE.PerspectiveCamera(50, width / height, 0.1, 1000)
  camera.position.set(50, 40, 50)
  
  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ 
    antialias: true,
    powerPreference: 'high-performance'
  })
  renderer.setSize(width, height)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  renderer.shadowMap.enabled = true
  renderer.shadowMap.type = THREE.PCFSoftShadowMap
  renderer.toneMapping = THREE.ACESFilmicToneMapping
  renderer.toneMappingExposure = 1.2
  canvasContainer.value.appendChild(renderer.domElement)
  
  // 控制器设置
  controls = new OrbitControls(camera, renderer.domElement)
  controls.enableDamping = true
  controls.dampingFactor = 0.05
  controls.minDistance = 15
  controls.maxDistance = 200
  controls.maxPolarAngle = Math.PI / 2 - 0.05
  controls.target.set(0, 8, 0)
  
  // 灯光设置
  setupLighting()
  
  // 地面
  createFloor()
  
  // 初始化后期处理
  initPostProcessing()
  
  // 开始渲染循环
  animate()
  
  // 绑定事件
  window.addEventListener('resize', onWindowResize)
  renderer.domElement.addEventListener('click', onMouseClick)
}

// 灯光设置
const setupLighting = () => {
  // 环境光 - 柔和的全局照明
  const ambientLight = new THREE.AmbientLight(0x404060, 0.5)
  scene.add(ambientLight)
  
  // 半球光 - 模拟天空和地面反射
  const hemisphereLight = new THREE.HemisphereLight(0x606080, 0x404040, 0.6)
  scene.add(hemisphereLight)
  
  // 主方向光 - 模拟阳光
  const mainLight = new THREE.DirectionalLight(0xffffff, 1.2)
  mainLight.position.set(30, 50, 30)
  mainLight.castShadow = true
  mainLight.shadow.mapSize.width = 2048
  mainLight.shadow.mapSize.height = 2048
  mainLight.shadow.camera.near = 0.5
  mainLight.shadow.camera.far = 150
  mainLight.shadow.camera.left = -50
  mainLight.shadow.camera.right = 50
  mainLight.shadow.camera.top = 50
  mainLight.shadow.camera.bottom = -50
  mainLight.shadow.bias = -0.0001
  scene.add(mainLight)
  
  // 补光 - 照亮暗部
  const fillLight = new THREE.DirectionalLight(0x4080ff, 0.4)
  fillLight.position.set(-20, 20, -20)
  scene.add(fillLight)
  
  // 区域指示点光源
  const areaPositions = [
    { pos: [-15, 8, 0], color: 0x3498db },
    { pos: [0, 8, 0], color: 0x2ecc71 },
    { pos: [15, 8, 0], color: 0xe74c3c }
  ]
  
  areaPositions.forEach(area => {
    const pointLight = new THREE.PointLight(area.color, 0.5, 30)
    pointLight.position.set(area.pos[0], area.pos[1], area.pos[2])
    scene.add(pointLight)
  })
}

// 创建地面
const createFloor = () => {
  // 金属质感地面
  const floorGeometry = new THREE.PlaneGeometry(300, 300)
  const floorMaterial = new THREE.MeshStandardMaterial({
    color: 0x1a1a2e,
    metalness: 0.8,
    roughness: 0.6
  })
  const floor = new THREE.Mesh(floorGeometry, floorMaterial)
  floor.rotation.x = -Math.PI / 2
  floor.position.y = -0.01
  floor.receiveShadow = true
  scene.add(floor)
  
  // 地面网格
  const gridHelper = new THREE.GridHelper(150, 75, 0x2d3748, 0x1a202c)
  gridHelper.position.y = 0
  scene.add(gridHelper)
  
  // 发光边框
  const borderGeometry = new THREE.EdgesGeometry(new THREE.PlaneGeometry(120, 80))
  const borderMaterial = new THREE.LineBasicMaterial({ 
    color: 0x3182ce, 
    transparent: true, 
    opacity: 0.4 
  })
  const border = new THREE.LineSegments(borderGeometry, borderMaterial)
  border.rotation.x = -Math.PI / 2
  border.position.y = 0.02
  scene.add(border)
  
  // 创建入库口
  createEntryPorts()
}

// 创建入库口模型
const createEntryPorts = () => {
  // 入库口位置配置（上下左右各一个）
  const entryPorts = [
    { position: 'top', x: 0, y: -25, z: 0, rotation: 0 },
    { position: 'bottom', x: 0, y: 25, z: 0, rotation: Math.PI },
    { position: 'left', x: -45, y: 0, z: 0, rotation: Math.PI / 2 },
    { position: 'right', x: 45, y: 0, z: 0, rotation: -Math.PI / 2 }
  ]
  
  const portColor = 0x00ff88  // 绿色发光
  
  entryPorts.forEach(port => {
    // 创建入库口框架
    const frameGeometry = new THREE.BoxGeometry(6, 4, 0.3)
    const frameMaterial = new THREE.MeshStandardMaterial({
      color: 0x444444,
      metalness: 0.8,
      roughness: 0.3
    })
    const frame = new THREE.Mesh(frameGeometry, frameMaterial)
    frame.position.set(port.x, 2, port.y)
    frame.rotation.y = port.rotation
    scene.add(frame)
    
    // 创建发光门框
    const gateGeometry = new THREE.BoxGeometry(5.5, 3.5, 0.1)
    const gateMaterial = new THREE.MeshStandardMaterial({
      color: portColor,
      emissive: portColor,
      emissiveIntensity: 0.5,
      transparent: true,
      opacity: 0.7
    })
    const gate = new THREE.Mesh(gateGeometry, gateMaterial)
    gate.position.set(port.x, 2, port.y)
    gate.rotation.y = port.rotation
    scene.add(gate)
    
    // 创建指示灯
    const lightGeometry = new THREE.SphereGeometry(0.3, 16, 16)
    const lightMaterial = new THREE.MeshStandardMaterial({
      color: portColor,
      emissive: portColor,
      emissiveIntensity: 1
    })
    const indicatorLight = new THREE.Mesh(lightGeometry, lightMaterial)
    indicatorLight.position.set(port.x, 4, port.y)
    scene.add(indicatorLight)
    
    // 添加标签
    const labelText = `${port.position.toUpperCase()}入口`
    createTextSprite(labelText, port.x, 4.5, port.y)
  })
}

// 创建文字标签
const createTextSprite = (text: string, x: number, y: number, z: number) => {
  const canvas = document.createElement('canvas')
  const context = canvas.getContext('2d')!
  canvas.width = 256
  canvas.height = 64
  
  context.fillStyle = 'rgba(0, 0, 0, 0)'
  context.fillRect(0, 0, canvas.width, canvas.height)
  
  context.font = 'bold 24px Arial'
  context.fillStyle = '#ffffff'
  context.textAlign = 'center'
  context.fillText(text, canvas.width / 2, canvas.height / 2 + 8)
  
  const texture = new THREE.CanvasTexture(canvas)
  const spriteMaterial = new THREE.SpriteMaterial({ map: texture, transparent: true })
  const sprite = new THREE.Sprite(spriteMaterial)
  sprite.position.set(x, y, z)
  sprite.scale.set(4, 1, 1)
  scene.add(sprite)
}

// 创建AGV模型
const createAgvModel = (agv: any): THREE.Group => {
  const group = new THREE.Group()
  
  // 车身 - 扁平的盒子
  const bodyGeometry = new THREE.BoxGeometry(1.5, 0.5, 1.0)
  const bodyMaterial = new THREE.MeshStandardMaterial({ 
    color: AGV_COLORS[agv.agvStatus] || 0x67c23a,
    metalness: 0.6,
    roughness: 0.4
  })
  const body = new THREE.Mesh(bodyGeometry, bodyMaterial)
  body.position.y = 0.35
  body.castShadow = true
  group.add(body)

  // 顶部指示灯 - 发光球体
  const lightGeometry = new THREE.SphereGeometry(0.12, 16, 16)
  const lightMaterial = new THREE.MeshStandardMaterial({ 
    color: AGV_COLORS[agv.agvStatus] || 0x67c23a,
    emissive: AGV_COLORS[agv.agvStatus] || 0x67c23a,
    emissiveIntensity: 0.8
  })
  const light = new THREE.Mesh(lightGeometry, lightMaterial)
  light.position.y = 0.72
  group.add(light)

  // 前传感器
  const sensorGeometry = new THREE.BoxGeometry(0.3, 0.15, 0.1)
  const sensorMaterial = new THREE.MeshStandardMaterial({ color: 0x333333 })
  const frontSensor = new THREE.Mesh(sensorGeometry, sensorMaterial)
  frontSensor.position.set(0, 0.3, 0.55)
  group.add(frontSensor)

  // 车轮 - 4个
  const wheelGeometry = new THREE.CylinderGeometry(0.15, 0.15, 0.12, 16)
  const wheelMaterial = new THREE.MeshStandardMaterial({ color: 0x222222, roughness: 0.9 })
  
  const wheelPositions = [
    [-0.55, 0.15, 0.45],
    [0.55, 0.15, 0.45],
    [-0.55, 0.15, -0.45],
    [0.55, 0.15, -0.45]
  ]
  
  wheelPositions.forEach(pos => {
    const wheel = new THREE.Mesh(wheelGeometry, wheelMaterial)
    wheel.rotation.x = Math.PI / 2
    wheel.position.set(pos[0], pos[1], pos[2])
    group.add(wheel)
  })

  // 设置用户数据，用于点击识别
  group.userData = { type: 'agv', ...agv }
  
  // 遍历子元素也添加userData，确保点击任何部位都能识别
  group.traverse((child) => {
    if (child instanceof THREE.Mesh) {
      child.userData = { type: 'agv', ...agv }
    }
  })

  return group
}

// 创建充电站模型
const createStationModel = (station: any): THREE.Mesh => {
  // 充电站底座
  const baseGeometry = new THREE.BoxGeometry(2.5, 0.15, 2)
  const baseMaterial = new THREE.MeshStandardMaterial({ 
    color: station.status === 1 ? 0x409eff : station.status === 2 ? 0x67c23a : 0x666666,
    metalness: 0.4,
    roughness: 0.6
  })
  const base = new THREE.Mesh(baseGeometry, baseMaterial)
  base.position.y = 0.075
  base.castShadow = true

  // 充电指示灯柱
  const poleGeometry = new THREE.CylinderGeometry(0.08, 0.1, 1.5, 8)
  const poleMaterial = new THREE.MeshStandardMaterial({ 
    color: 0x888888,
    metalness: 0.8,
    roughness: 0.3
  })
  
  // 两根柱子
  const pole1 = new THREE.Mesh(poleGeometry, poleMaterial)
  pole1.position.set(-0.9, 0.9, -0.7)
  base.add(pole1)
  
  const pole2 = new THREE.Mesh(poleGeometry, poleMaterial)
  pole2.position.set(0.9, 0.9, -0.7)
  base.add(pole2)

  // 顶部充电指示灯
  const lightGeometry = new THREE.BoxGeometry(0.4, 0.2, 0.4)
  const lightMaterial = new THREE.MeshStandardMaterial({ 
    color: station.status === 2 ? 0x67c23a : 0x666666,
    emissive: station.status === 2 ? 0x67c23a : 0x000000,
    emissiveIntensity: station.status === 2 ? 1 : 0
  })
  const indicatorLight = new THREE.Mesh(lightGeometry, lightMaterial)
  indicatorLight.position.set(0, 1.65, -0.7)
  base.add(indicatorLight)

  // 设置位置
  base.position.set(
    (station.xPosition || station.id * 5) - 25,
    0,
    (station.yPosition || 0) + 15
  )

  // 设置用户数据，用于点击识别
  base.userData = { type: 'station', ...station }
  
  // 遍历子元素也添加userData
  base.traverse((child) => {
    if (child instanceof THREE.Mesh) {
      child.userData = { type: 'station', ...station }
    }
  })

  return base
}

// 创建AGV和充电站模型
const createAgvModelsFromData = () => {
  // 清除旧的AGV模型
  agvMeshes.forEach(mesh => {
    scene.remove(mesh)
  })
  agvMeshes.clear()
  
  // 清除旧的充电站模型
  stationMeshes.forEach(mesh => {
    scene.remove(mesh)
  })
  stationMeshes.clear()

  // 创建充电站模型
  stationList.value.forEach(station => {
    const model = createStationModel(station)
    scene.add(model)
    stationMeshes.set(station.id, model)
  })

  // 创建AGV模型
  agvList.value.forEach(agv => {
    const model = createAgvModel(agv)
    // 根据当前位置放置AGV，如果没有位置则按ID排列
    const xPos = agv.xPosition !== undefined ? agv.xPosition - 25 : (agv.agvId - 1) * 5 - 10
    const zPos = agv.yPosition !== undefined ? agv.yPosition + 15 : 10
    model.position.set(xPos, 0, zPos)
    
    // 保存目标位置用于动画
    const targetX = agv.targetLocation ? getLocationX(agv.targetLocation) : xPos
    const targetZ = agv.targetLocation ? getLocationY(agv.targetLocation) : zPos
    
    // 检查是否需要触发动画（当AGV有目标位置且正在工作时）
    if (agv.agvStatus === 'working' && agv.targetLocation && !agvAnimations.has(agv.agvId)) {
      startAgvAnimation(agv.agvId, xPos, zPos, targetX, targetZ)
    }
    
    scene.add(model)
    agvMeshes.set(agv.agvId, model)
  })
}

// 根据位置编码获取X坐标
const getLocationX = (location: string): number => {
  if (!location) return 0
  
  // 入库口位置
  if (location.startsWith('ENTRY-')) {
    if (location.includes('TOP')) return 0
    if (location.includes('BOTTOM')) return 0
    if (location.includes('LEFT')) return -45
    if (location.includes('RIGHT')) return 45
  }
  
  // 充电站位置
  if (location.startsWith('CS-')) {
    return 0
  }
  
  // 货位解析（格式：A-01-01）
  try {
    const parts = location.split('-')
    if (parts.length >= 2) {
      const area = parts[0].charCodeAt(0) - 65 // A=0, B=1, ...
      const col = parseInt(parts[1])
      return (col - 2.5) * 2 + area * 14
    }
  } catch (e) {
    console.warn('解析位置X失败:', location)
  }
  
  return 0
}

// 根据位置编码获取Z坐标
const getLocationY = (location: string): number => {
  if (!location) return 0
  
  // 入库口位置
  if (location.startsWith('ENTRY-')) {
    if (location.includes('TOP')) return -25
    if (location.includes('BOTTOM')) return 25
    if (location.includes('LEFT')) return 0
    if (location.includes('RIGHT')) return 0
  }
  
  // 充电站位置
  if (location.startsWith('CS-')) {
    return 15
  }
  
  // 货位解析
  try {
    const parts = location.split('-')
    if (parts.length >= 3) {
      const row = parseInt(parts[2])
      return (row - 2) * 2
    }
  } catch (e) {
    console.warn('解析位置Y失败:', location)
  }
  
  return 0
}

// 初始化后期处理
const initPostProcessing = () => {
  composer = new EffectComposer(renderer)
  
  // 渲染通道
  const renderPass = new RenderPass(scene, camera)
  composer.addPass(renderPass)
  
  // 辉光效果 - 营造科技感
  const bloomPass = new UnrealBloomPass(
    new THREE.Vector2(window.innerWidth, window.innerHeight),
    0.8,    // strength
    0.4,    // radius
    0.85    // threshold
  )
  composer.addPass(bloomPass)
  
  // 输出通道
  const outputPass = new OutputPass()
  composer.addPass(outputPass)
}

// 动画循环
let lastTime = 0
const animate = (time: number = 0) => {
  animationId = requestAnimationFrame(animate)
  
  // 计算delta时间
  const delta = lastTime ? (time - lastTime) / 16.67 : 1
  lastTime = time
  
  if (autoRotate.value) {
    scene.rotation.y += 0.002
  }
  
  // 更新AGV动画
  updateAgvAnimations(delta)
  
  controls.update()
  composer.render()
}

// 窗口大小变化
const onWindowResize = () => {
  if (!canvasContainer.value) return
  const width = canvasContainer.value.clientWidth
  const height = canvasContainer.value.clientHeight
  
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  
  renderer.setSize(width, height)
  composer.setSize(width, height)
}

// 鼠标点击
const onMouseClick = (event: MouseEvent) => {
  if (!canvasContainer.value) return
  
  const rect = canvasContainer.value.getBoundingClientRect()
  mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
  mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
  
  raycaster.setFromCamera(mouse, camera)
  const intersects = raycaster.intersectObjects(scene.children)
  
  // 先清除之前的选中状态
  selectedLocation.value = null
  selectedAgv.value = null
  selectedStation.value = null
  
  for (const intersect of intersects) {
    const userData = intersect.object.userData
    
    if (userData && userData.type === 'agv') {
      // 选中了AGV
      selectedAgv.value = userData
      highlightAgv(intersect.object as THREE.Group)
      return
    } else if (userData && userData.type === 'station') {
      // 选中了充电站
      selectedStation.value = userData
      return
    } else if (userData && userData.id && userData.locationCode) {
      // 选中了货位
      selectedLocation.value = userData
      highlightLocation(intersect.object as THREE.Mesh)
      return
    }
  }
}

// 高亮选中的AGV
const highlightAgv = (group: THREE.Group) => {
  group.traverse((child) => {
    if (child instanceof THREE.Mesh && child.material instanceof THREE.MeshStandardMaterial) {
      child.material.emissive.setHex(0xffffff)
      child.material.emissiveIntensity = 0.3
    }
  })
}

// 高亮选中货位
const highlightLocation = (mesh: THREE.Mesh) => {
  // 重置所有货位颜色
  locationMeshes.forEach((m) => {
    const material = m.material as THREE.MeshStandardMaterial
    const status = m.userData.currentStatus
    const statusColors: Record<string, number> = {
      'empty': 0x2ecc71,
      'occupied': 0x3498db,
      'reserved': 0xf39c12
    }
    material.color.setHex(statusColors[status] || 0x2ecc71)
    material.emissive.setHex(statusColors[status] || 0x2ecc71)
    material.emissiveIntensity = 0.1
    material.opacity = 0.85
  })
  
  // 高亮选中货位
  const material = mesh.material as THREE.MeshStandardMaterial
  material.color.setHex(0xf56c6c)
  material.emissive.setHex(0xf56c6c)
  material.emissiveIntensity = 0.5
  material.opacity = 1
  
  // 更新边缘线
  const selectedId = mesh.userData.id
  locationEdges.forEach((line, id) => {
    const lineMaterial = line.material as THREE.LineBasicMaterial
    if (id === selectedId) {
      lineMaterial.color.setHex(0xff6b6b)
      lineMaterial.opacity = 1
    } else {
      lineMaterial.color.setHex(0xffffff)
      lineMaterial.opacity = 0.6
    }
  })
}

// 视图控制
const resetCamera = () => {
  camera.position.set(50, 40, 50)
  controls.reset()
  controls.target.set(0, 8, 0)
  scene.rotation.y = 0
}

const setTopView = () => {
  camera.position.set(0, 80, 0.1)
  camera.lookAt(0, 0, 0)
  controls.reset()
}

const setFrontView = () => {
  camera.position.set(0, 20, 80)
  camera.lookAt(0, 10, 0)
  controls.reset()
}

const setSideView = () => {
  camera.position.set(80, 20, 0)
  camera.lookAt(0, 10, 0)
  controls.reset()
}

const toggleAutoRotate = () => {
  autoRotate.value = !autoRotate.value
}

// 刷新数据
const refreshData = async () => {
  loading.value = true
  try {
    // 获取概览统计
    const overviewRes = await request.get<Result<any>>('/warehouse/location/statistics/overview')
    if (overviewRes.data) {
      stats.total = overviewRes.data.totalLocations || 0
      stats.empty = overviewRes.data.emptyLocations || 0
      stats.occupied = overviewRes.data.occupiedLocations || 0
      stats.reserved = stats.total - stats.empty - stats.occupied
    }
    
    // 重新加载货位数据
    await loadLocationData()
    
    // 加载AGV数据
    await loadAgvData()
    
    ElMessage.success('数据刷新成功')
  } catch (error) {
    console.error('刷新数据失败:', error)
    ElMessage.error('刷新数据失败')
  } finally {
    loading.value = false
  }
}

// 辅助函数
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'empty': 'success',
    'occupied': 'primary',
    'reserved': 'warning'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'empty': '空闲',
    'occupied': '占用',
    'reserved': '预留'
  }
  return map[status] || '未知'
}

const getLocationTypeText = (type: string) => {
  const map: Record<string, string> = {
    'storage': '存储位',
    'picking': '拣选位',
    'buffer': '缓存位'
  }
  return map[type] || '未知'
}

const getProgressColor = (percentage: number) => {
  if (percentage < 50) return '#67c23a'
  if (percentage < 80) return '#409eff'
  return '#f56c6c'
}

// AGV状态相关辅助函数
const getAgvStatusType = (status: string) => {
  const map: Record<string, string> = {
    idle: 'success',
    working: 'primary',
    charging: 'warning',
    returning: 'warning',
    fault: 'danger'
  }
  return map[status] || 'info'
}

const getAgvStatusText = (status: string) => {
  const map: Record<string, string> = {
    idle: '空闲',
    working: '工作中',
    charging: '充电中',
    returning: '返回中',
    fault: '故障'
  }
  return map[status] || status
}

const getBatteryColor = (level: number) => {
  if (level >= 60) return '#67c23a'
  if (level >= 20) return '#e6a23c'
  return '#f56c6c'
}

// 生命周期
onMounted(() => {
  initScene()
  loadLocationData()
  loadAgvData()
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('resize', onWindowResize)
  if (renderer) {
    renderer.domElement.removeEventListener('click', onMouseClick)
    renderer.dispose()
    composer?.dispose()
  }
})
</script>

<style lang="scss" scoped>
.warehouse-3d-container {
  display: flex;
  height: calc(100vh - 100px);
  gap: 20px;
}

.view-panel {
  flex: 1;
  position: relative;
  background-color: var(--card-bg);
  border-radius: 8px;
  overflow: hidden;
  
  .canvas-container {
    width: 100%;
    height: 100%;
  }
  
  .view-controls {
    position: absolute;
    top: 20px;
    right: 20px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 41, 59, 0.9));
    padding: 12px;
    border-radius: 12px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
  }
  
  .legend {
    position: absolute;
    bottom: 20px;
    left: 20px;
    background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 41, 59, 0.9));
    padding: 14px 18px;
    border-radius: 12px;
    display: flex;
    gap: 20px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    
    .legend-item {
      display: flex;
      align-items: center;
      gap: 10px;
      color: #e2e8f0;
      font-size: 13px;
      font-weight: 500;
      
      .legend-color {
        width: 18px;
        height: 18px;
        border-radius: 4px;
        box-shadow: 0 0 10px currentColor;
        
        &.empty {
          background-color: #2ecc71;
          box-shadow: 0 0 10px #2ecc71;
        }
        
        &.occupied {
          background-color: #3498db;
          box-shadow: 0 0 10px #3498db;
        }
        
        &.reserved {
          background-color: #f39c12;
          box-shadow: 0 0 10px #f39c12;
        }
      }
    }
  }
  
  .selected-info {
    position: absolute;
    top: 20px;
    left: 20px;
    width: 300px;
    background: linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95));
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 12px;
    overflow: hidden;
    backdrop-filter: blur(10px);
    
    .info-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 14px 18px;
      background: linear-gradient(90deg, #3182ce, #4299e1);
      
      .info-title {
        font-weight: 600;
        font-size: 15px;
        color: #fff;
      }
    }
    
    .info-content {
      padding: 16px;
      
      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px 0;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        
        &:last-child {
          border-bottom: none;
        }
        
        .label {
          color: #a0aec0;
          font-size: 13px;
        }
        
        span:last-child {
          color: #e2e8f0;
          font-weight: 500;
        }
      }
    }
  }
}

.stats-panel {
  width: 360px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
  
  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h3 {
      margin: 0;
      font-size: 18px;
      color: var(--text-color);
      font-weight: 600;
    }
  }
  
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    
    .stat-card {
      display: flex;
      align-items: center;
      gap: 14px;
      padding: 18px;
      background: linear-gradient(135deg, rgba(30, 41, 59, 0.8), rgba(15, 23, 42, 0.9));
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 12px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        border-color: rgba(255, 255, 255, 0.2);
      }
      
      .stat-icon {
        width: 52px;
        height: 52px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: #fff;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
        
        &.total {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        &.empty {
          background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%);
        }
        
        &.occupied {
          background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
        }
        
        &.reserved {
          background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
        }
      }
      
      .stat-info {
        .stat-value {
          font-size: 26px;
          font-weight: 700;
          color: var(--text-color);
          line-height: 1.2;
        }
        
        .stat-label {
          font-size: 12px;
          color: #a0aec0;
          margin-top: 2px;
        }
      }
    }
  }
  
  .usage-chart, .area-distribution, .recent-operations {
    background: linear-gradient(135deg, rgba(30, 41, 59, 0.6), rgba(15, 23, 42, 0.8));
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 18px;
    
    h4 {
      margin: 0 0 16px;
      font-size: 14px;
      color: #a0aec0;
      font-weight: 500;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
  }
  
  .usage-chart {
    .progress-wrapper {
      margin-top: 8px;
    }
  }
  
  .area-distribution {
    .area-list {
      display: flex;
      flex-direction: column;
      gap: 14px;
      
      .area-item {
        .area-header {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;
          
          .area-name {
            font-size: 14px;
            color: #e2e8f0;
            font-weight: 500;
          }
          
          .area-count {
            font-size: 14px;
            color: #718096;
            font-weight: 600;
          }
        }
      }
    }
  }
  
  .recent-operations {
    .el-timeline {
      padding: 0;
    }
  }
  
  // AGV统计样式
  .agv-stats {
    background: linear-gradient(135deg, rgba(30, 41, 59, 0.6), rgba(15, 23, 42, 0.8));
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 18px;
    margin-top: 16px;
    
    h4 {
      margin: 0 0 16px;
      font-size: 14px;
      color: #a0aec0;
      font-weight: 500;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    
    .agv-stats-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      margin-bottom: 16px;
    }
    
    .agv-stat-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 8px;
      
      .agv-stat-icon {
        width: 32px;
        height: 32px;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        
        &.total { background: rgba(64, 158, 255, 0.2); color: #409eff; }
        &.idle { background: rgba(103, 194, 58, 0.2); color: #67c23a; }
        &.working { background: rgba(64, 158, 255, 0.2); color: #409eff; }
        &.charging { background: rgba(230, 162, 60, 0.2); color: #e6a23c; }
        &.fault { background: rgba(245, 108, 108, 0.2); color: #f56c6c; }
      }
      
      .agv-stat-info {
        .agv-stat-value {
          font-size: 20px;
          font-weight: bold;
          color: #fff;
        }
        .agv-stat-label {
          font-size: 11px;
          color: #718096;
        }
      }
    }
    
    .agv-list {
      display: flex;
      flex-direction: column;
      gap: 10px;
      max-height: 200px;
      overflow-y: auto;
      
      .agv-item {
        padding: 10px;
        background: rgba(255, 255, 255, 0.03);
        border-radius: 8px;
        
        .agv-info {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          
          .agv-code {
            color: #e2e8f0;
            font-size: 13px;
            font-weight: 500;
          }
        }
        
        .agv-battery {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .el-progress {
            flex: 1;
          }
          
          .battery-text {
            font-size: 12px;
            color: #a0aec0;
            min-width: 35px;
          }
        }
      }
    }
  }
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(-20px);
  opacity: 0;
}
</style>
