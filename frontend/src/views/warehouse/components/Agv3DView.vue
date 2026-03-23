<template>
  <!-- AGV 3D模型渲染区域 - 由父组件控制显示 -->
  <div ref="agvCanvas" class="agv-canvas"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'

// Props
interface Props {
  agvList?: any[]
  chargingStations?: any[]
}

const props = withDefaults(defineProps<Props>(), {
  agvList: () => [],
  chargingStations: () => []
})

// Refs
const agvCanvas = ref<HTMLDivElement>()

// Three.js 相关
let scene: THREE.Scene
let camera: THREE.PerspectiveCamera
let renderer: THREE.WebGLRenderer
let controls: OrbitControls
let animationId: number
const agvMeshes: Map<number, THREE.Group> = new Map()
const stationMeshes: Map<number, THREE.Mesh> = new Map()

// AGV状态颜色
const AGV_COLORS: Record<string, number> = {
  idle: 0x67c23a,      // 绿色 - 空闲
  working: 0x409eff,   // 蓝色 - 工作中
  charging: 0xe6a23c,  // 黄色 - 充电中
  returning: 0xf39c12, // 橙色 - 返回中
  fault: 0xf56c6c      // 红色 - 故障
}

// 初始化场景
const initScene = () => {
  if (!agvCanvas.value) return

  // 创建场景
  scene = new THREE.Scene()
  scene.background = new THREE.Color(0x1a1a2e)

  // 创建相机
  const width = agvCanvas.value.clientWidth
  const height = agvCanvas.value.clientHeight
  camera = new THREE.PerspectiveCamera(60, width / height, 0.1, 1000)
  camera.position.set(10, 15, 20)

  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(width, height)
  renderer.setPixelRatio(window.devicePixelRatio)
  agvCanvas.value.appendChild(renderer.domElement)

  // 添加灯光
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.6)
  scene.add(ambientLight)

  const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8)
  directionalLight.position.set(10, 20, 10)
  scene.add(directionalLight)

  // 添加控制器
  controls = new OrbitControls(camera, renderer.domElement)
  controls.enableDamping = true
  controls.dampingFactor = 0.05

  // 添加地面
  createGround()

  // 渲染循环
  animate()
}

// 创建地面
const createGround = () => {
  const gridHelper = new THREE.GridHelper(50, 50, 0x444444, 0x222222)
  scene.add(gridHelper)

  const groundGeometry = new THREE.PlaneGeometry(50, 50)
  const groundMaterial = new THREE.MeshBasicMaterial({ 
    color: 0x1a1a2e,
    transparent: true,
    opacity: 0.8
  })
  const ground = new THREE.Mesh(groundGeometry, groundMaterial)
  ground.rotation.x = -Math.PI / 2
  ground.position.y = -0.01
  scene.add(ground)
}

// 创建AGV模型
const createAgvModel = (agv: any): THREE.Group => {
  const group = new THREE.Group()
  
  // 车身 - 扁平的盒子
  const bodyGeometry = new THREE.BoxGeometry(1.2, 0.4, 0.8)
  const bodyMaterial = new THREE.MeshStandardMaterial({ 
    color: AGV_COLORS[agv.agvStatus] || 0x67c23a,
    metalness: 0.5,
    roughness: 0.5
  })
  const body = new THREE.Mesh(bodyGeometry, bodyMaterial)
  body.position.y = 0.3
  group.add(body)

  // 顶部指示灯
  const lightGeometry = new THREE.SphereGeometry(0.1, 16, 16)
  const lightMaterial = new THREE.MeshStandardMaterial({ 
    color: AGV_COLORS[agv.agvStatus] || 0x67c23a,
    emissive: AGV_COLORS[agv.agvStatus] || 0x67c23a,
    emissiveIntensity: 0.5
  })
  const light = new THREE.Mesh(lightGeometry, lightMaterial)
  light.position.y = 0.6
  group.add(light)

  // 车轮
  const wheelGeometry = new THREE.CylinderGeometry(0.15, 0.15, 0.1, 16)
  const wheelMaterial = new THREE.MeshStandardMaterial({ color: 0x333333 })
  
  const positions = [
    [-0.5, 0.15, 0.4],
    [0.5, 0.15, 0.4],
    [-0.5, 0.15, -0.4],
    [0.5, 0.15, -0.4]
  ]
  
  positions.forEach(pos => {
    const wheel = new THREE.Mesh(wheelGeometry, wheelMaterial)
    wheel.rotation.x = Math.PI / 2
    wheel.position.set(pos[0], pos[1], pos[2])
    group.add(wheel)
  })

  // 标签 - 显示AGV编号
  // 这里可以添加文本标签

  return group
}

// 创建充电站模型
const createStationModel = (station: any): THREE.Mesh => {
  // 充电站底座
  const baseGeometry = new THREE.BoxGeometry(2, 0.1, 1.5)
  const baseMaterial = new THREE.MeshStandardMaterial({ 
    color: station.status === 1 ? 0x409eff : station.status === 2 ? 0xe6a23c : 0x666666,
    metalness: 0.3,
    roughness: 0.7
  })
  const base = new THREE.Mesh(baseGeometry, baseMaterial)
  base.position.y = 0.05

  // 充电指示灯
  const lightGeometry = new THREE.BoxGeometry(0.3, 0.3, 0.3)
  const lightMaterial = new THREE.MeshStandardMaterial({ 
    color: station.status === 2 ? 0x67c23a : 0x666666,
    emissive: station.status === 2 ? 0x67c23a : 0x000000,
    emissiveIntensity: station.status === 2 ? 0.8 : 0
  })
  const light = new THREE.Mesh(lightGeometry, lightMaterial)
  light.position.y = 0.25
  base.add(light)

  // 设置位置
  base.position.set(
    station.xPosition || 0,
    0,
    station.yPosition || 0
  )

  return base
}

// 更新AGV模型
const updateAgvModels = () => {
  // 移除旧的模型
  agvMeshes.forEach(mesh => {
    scene.remove(mesh)
  })
  agvMeshes.clear()

  // 创建新的模型
  props.agvList.forEach(agv => {
    const model = createAgvModel(agv)
    model.position.set(
      agv.xPosition || agv.agvId * 2,
      0,
      agv.yPosition || 0
    )
    scene.add(model)
    agvMeshes.set(agv.agvId, model)
  })
}

// 更新充电站模型
const updateStationModels = () => {
  // 移除旧的模型
  stationMeshes.forEach(mesh => {
    scene.remove(mesh)
  })
  stationMeshes.clear()

  // 创建新的模型
  props.chargingStations.forEach(station => {
    const model = createStationModel(station)
    scene.add(model)
    stationMeshes.set(station.id, model)
  })
}

// 动画循环
const animate = () => {
  animationId = requestAnimationFrame(animate)
  controls?.update()
  renderer?.render(scene, camera)
}

// 窗口大小调整
const onWindowResize = () => {
  if (!agvCanvas.value || !camera || !renderer) return
  const width = agvCanvas.value.clientWidth
  const height = agvCanvas.value.clientHeight
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)
}

// 监听数据变化
watch(() => props.agvList, updateAgvModels, { deep: true })
watch(() => props.chargingStations, updateStationModels, { deep: true })

// 生命周期
onMounted(() => {
  initScene()
  window.addEventListener('resize', onWindowResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', onWindowResize)
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  if (renderer) {
    renderer.dispose()
  }
})

// 暴露方法
defineExpose({
  updateAgvModels,
  updateStationModels
})
</script>

<style scoped>
.agv-canvas {
  width: 100%;
  height: 100%;
  min-height: 400px;
}
</style>
