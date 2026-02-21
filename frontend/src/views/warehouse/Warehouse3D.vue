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
      
      <!-- 选中信息 -->
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
              :percentage="(area.count / stats.total) * 100"
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
        <el-timeline>
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
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, reactive } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import { ElMessage } from 'element-plus'
import request, { Result } from '@/api/request'

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
const autoRotate = ref(false)
const loading = ref(false)

const stats = reactive<LocationStats>({
  total: 0,
  empty: 0,
  occupied: 0,
  reserved: 0
})

const areaStats = ref<AreaStat[]>([])

const recentOperations = ref<RecentOperation[]>([
  { time: '2024-01-15 14:30', content: '入库: A01-01-01 烟草原料', type: 'success' },
  { time: '2024-01-15 13:20', content: '出库: A01-02-03 成品烟', type: 'primary' },
  { time: '2024-01-15 11:45', content: '盘点: A02区域完成', type: 'info' },
  { time: '2024-01-15 10:30', content: '入库: A02-01-02 辅助材料', type: 'success' },
  { time: '2024-01-15 09:15', content: '调度: 托盘T001完成任务', type: 'primary' }
])

// Three.js 相关
let scene: THREE.Scene
let camera: THREE.PerspectiveCamera
let renderer: THREE.WebGLRenderer
let controls: OrbitControls
let animationId: number
const raycaster = new THREE.Raycaster()
const mouse = new THREE.Vector2()
const locationMeshes: Map<number, THREE.Mesh> = new Map()

// 计算使用率
const usageRate = computed(() => {
  if (stats.total === 0) return 0
  return Math.round(((stats.total - stats.empty) / stats.total) * 100)
})

// 初始化Three.js场景
const initScene = () => {
  if (!canvasContainer.value) return
  
  // 创建场景
  scene = new THREE.Scene()
  scene.background = new THREE.Color(0x1a1a2e)
  
  // 创建相机
  const width = canvasContainer.value.clientWidth
  const height = canvasContainer.value.clientHeight
  camera = new THREE.PerspectiveCamera(60, width / height, 0.1, 1000)
  camera.position.set(30, 25, 30)
  
  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(width, height)
  renderer.setPixelRatio(window.devicePixelRatio)
  renderer.shadowMap.enabled = true
  canvasContainer.value.appendChild(renderer.domElement)
  
  // 创建控制器
  controls = new OrbitControls(camera, renderer.domElement)
  controls.enableDamping = true
  controls.dampingFactor = 0.05
  controls.minDistance = 10
  controls.maxDistance = 100
  controls.maxPolarAngle = Math.PI / 2 - 0.1
  
  // 添加光源
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.6)
  scene.add(ambientLight)
  
  const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8)
  directionalLight.position.set(20, 30, 20)
  directionalLight.castShadow = true
  scene.add(directionalLight)
  
  // 添加网格
  const gridHelper = new THREE.GridHelper(60, 60, 0x444444, 0x222222)
  scene.add(gridHelper)
  
  // 创建仓库货架模型
  createWarehouseModel()
  
  // 开始渲染循环
  animate()
  
  // 绑定事件
  window.addEventListener('resize', onWindowResize)
  renderer.domElement.addEventListener('click', onMouseClick)
}

// 创建仓库模型
const createWarehouseModel = () => {
  const locationSize = 2
  const gap = 0.3
  const rows = 5
  const cols = 6
  const levels = 4
  const areas = ['A', 'B', 'C']
  
  // 区域颜色映射
  const areaColors: Record<string, number> = {
    'A': 0x409eff,
    'B': 0x67c23a,
    'C': 0xe6a23c
  }
  
  // 为每个区域创建货架
  areas.forEach((area, areaIndex) => {
    const areaOffset = (areaIndex - 1) * 18
    
    for (let row = 0; row < rows; row++) {
      for (let col = 0; col < cols; col++) {
        for (let level = 0; level < levels; level++) {
          // 创建货位立方体
          const geometry = new THREE.BoxGeometry(
            locationSize - gap,
            locationSize - gap,
            locationSize - gap
          )
          
          // 根据状态设置颜色
          const status = getRandomStatus()
          let color = 0x67c23a // 空闲 - 绿色
          if (status === 'occupied') {
            color = 0x409eff // 占用 - 蓝色
          } else if (status === 'reserved') {
            color = 0xe6a23c // 预留 - 黄色
          }
          
          const material = new THREE.MeshStandardMaterial({
            color: color,
            transparent: true,
            opacity: 0.8,
            metalness: 0.3,
            roughness: 0.7
          })
          
          const cube = new THREE.Mesh(geometry, material)
          
          // 计算位置
          const x = (col - cols / 2 + 0.5) * locationSize + areaOffset
          const y = (level + 0.5) * locationSize
          const z = (row - rows / 2 + 0.5) * locationSize
          
          cube.position.set(x, y, z)
          cube.castShadow = true
          cube.receiveShadow = true
          
          // 存储货位信息
          const locationId = areaIndex * 100 + row * 10 + col * 1 + level
          cube.userData = {
            id: locationId,
            locationCode: `${area}${String(row + 1).padStart(2, '0')}-${String(col + 1).padStart(2, '0')}-${level + 1}`,
            areaCode: area,
            areaName: `${area}区`,
            rowNum: row + 1,
            colNum: col + 1,
            levelNum: level + 1,
            currentStatus: status,
            locationType: level === 0 ? 'picking' : 'storage'
          }
          
          scene.add(cube)
          locationMeshes.set(locationId, cube)
        }
      }
      
      // 创建货架支架
      const支架Geometry = new THREE.BoxGeometry(cols * locationSize + 0.5, levels * locationSize + 0.5, 0.3)
      const支架Material = new THREE.MeshStandardMaterial({ color: 0x666666, metalness: 0.5, roughness: 0.5 })
      
      // 前支架
      const front支架 = new THREE.Mesh(支架Geometry,支架Material)
      front支架.position.set(areaOffset, (levels * locationSize) / 2, (row - rows / 2 + 0.5) * locationSize + locationSize / 2 + 0.15)
      scene.add(front支架)
      
      // 后支架
      const back支架 = new THREE.Mesh(支架Geometry,支架Material)
      back支架.position.set(areaOffset, (levels * locationSize) / 2, (row - rows / 2 + 0.5) * locationSize - locationSize / 2 - 0.15)
      scene.add(back支架)
    }
  })
  
  // 更新统计
  stats.total = locationMeshes.size
  stats.empty = Math.floor(stats.total * 0.4)
  stats.occupied = Math.floor(stats.total * 0.5)
  stats.reserved = stats.total - stats.empty - stats.occupied
  
  // 更新区域统计
  areaStats.value = areas.map(area => ({
    areaCode: area,
    areaName: `${area}区`,
    count: Math.floor(stats.total / areas.length)
  }))
}

// 获取随机状态
const getRandomStatus = (): string => {
  const rand = Math.random()
  if (rand < 0.4) return 'empty'
  if (rand < 0.9) return 'occupied'
  return 'reserved'
}

// 动画循环
const animate = () => {
  animationId = requestAnimationFrame(animate)
  
  if (autoRotate.value) {
    scene.rotation.y += 0.002
  }
  
  controls.update()
  renderer.render(scene, camera)
}

// 窗口大小变化
const onWindowResize = () => {
  if (!canvasContainer.value) return
  const width = canvasContainer.value.clientWidth
  const height = canvasContainer.value.clientHeight
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)
}

// 鼠标点击
const onMouseClick = (event: MouseEvent) => {
  if (!canvasContainer.value) return
  
  const rect = canvasContainer.value.getBoundingClientRect()
  mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
  mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
  
  raycaster.setFromCamera(mouse, camera)
  const intersects = raycaster.intersectObjects(scene.children)
  
  for (const intersect of intersects) {
    if (intersect.object.userData && intersect.object.userData.id) {
      selectedLocation.value = intersect.object.userData
      highlightLocation(intersect.object as THREE.Mesh)
      break
    }
  }
}

// 高亮选中货位
const highlightLocation = (mesh: THREE.Mesh) => {
  // 重置所有货位颜色
  locationMeshes.forEach((m) => {
    const material = m.material as THREE.MeshStandardMaterial
    const status = m.userData.currentStatus
    if (status === 'empty') material.color.setHex(0x67c23a)
    else if (status === 'occupied') material.color.setHex(0x409eff)
    else material.color.setHex(0xe6a23c)
    material.opacity = 0.8
  })
  
  // 高亮选中货位
  const material = mesh.material as THREE.MeshStandardMaterial
  material.color.setHex(0xf56c6c)
  material.opacity = 1
}

// 视图控制
const resetCamera = () => {
  camera.position.set(30, 25, 30)
  controls.reset()
  scene.rotation.y = 0
}

const setTopView = () => {
  camera.position.set(0, 50, 0)
  camera.lookAt(0, 0, 0)
  controls.reset()
}

const setFrontView = () => {
  camera.position.set(0, 15, 50)
  camera.lookAt(0, 10, 0)
  controls.reset()
}

const setSideView = () => {
  camera.position.set(50, 15, 0)
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
    const res = await request.get<Result<any>>('/warehouse/statistics/overview')
    if (res.data) {
      stats.total = res.data.totalLocations || 0
      stats.empty = res.data.emptyLocations || 0
      stats.occupied = res.data.occupiedLocations || 0
    }
    ElMessage.success('数据刷新成功')
  } catch (error) {
    // 使用模拟数据
    console.log('使用模拟数据')
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

// 生命周期
onMounted(() => {
  initScene()
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('resize', onWindowResize)
  if (renderer) {
    renderer.domElement.removeEventListener('click', onMouseClick)
    renderer.dispose()
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
    background-color: rgba(0, 0, 0, 0.5);
    padding: 10px;
    border-radius: 8px;
  }
  
  .legend {
    position: absolute;
    bottom: 20px;
    left: 20px;
    background-color: rgba(0, 0, 0, 0.7);
    padding: 12px 16px;
    border-radius: 8px;
    display: flex;
    gap: 16px;
    
    .legend-item {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #fff;
      font-size: 13px;
      
      .legend-color {
        width: 16px;
        height: 16px;
        border-radius: 4px;
        
        &.empty {
          background-color: #67c23a;
        }
        
        &.occupied {
          background-color: #409eff;
        }
        
        &.reserved {
          background-color: #e6a23c;
        }
      }
    }
  }
  
  .selected-info {
    position: absolute;
    top: 20px;
    left: 20px;
    width: 280px;
    background-color: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 8px;
    overflow: hidden;
    
    .info-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      background-color: var(--primary-color);
      color: #fff;
      
      .info-title {
        font-weight: 600;
      }
    }
    
    .info-content {
      padding: 16px;
      
      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 0;
        border-bottom: 1px solid var(--border-color);
        
        &:last-child {
          border-bottom: none;
        }
        
        .label {
          color: var(--text-color-secondary);
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
    }
  }
  
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    
    .stat-card {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      background-color: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 8px;
      
      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: #fff;
        
        &.total {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        &.empty {
          background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
        }
        
        &.occupied {
          background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
        }
        
        &.reserved {
          background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
        }
      }
      
      .stat-info {
        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: var(--text-color);
        }
        
        .stat-label {
          font-size: 12px;
          color: var(--text-color-secondary);
        }
      }
    }
  }
  
  .usage-chart, .area-distribution, .recent-operations {
    background-color: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 8px;
    padding: 16px;
    
    h4 {
      margin: 0 0 16px;
      font-size: 14px;
      color: var(--text-color);
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
      gap: 12px;
      
      .area-item {
        .area-header {
          display: flex;
          justify-content: space-between;
          margin-bottom: 6px;
          
          .area-name {
            font-size: 13px;
            color: var(--text-color);
          }
          
          .area-count {
            font-size: 13px;
            color: var(--text-color-secondary);
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
