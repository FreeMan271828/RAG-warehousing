<template>
  <div class="agv-container">
    <!-- 顶部统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><Van /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">小车总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon idle">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.idle }}</div>
          <div class="stat-label">空闲</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon working">
          <el-icon><Loading /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.working }}</div>
          <div class="stat-label">工作中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon charging">
          <el-icon><Lightning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.charging }}</div>
          <div class="stat-label">充电中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon fault">
          <el-icon><WarningFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.fault }}</div>
          <div class="stat-label">故障</div>
        </div>
      </div>
    </div>

    <!-- 小车列表 -->
    <el-card class="agv-list-card">
      <template #header>
        <div class="card-header">
          <span>AGV小车列表</span>
          <el-button type="primary" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="agvList" style="width: 100%" v-loading="loading">
        <el-table-column prop="equipmentCode" label="编号" width="120" />
        <el-table-column prop="equipmentName" label="名称" width="150" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.agvStatus)">
              {{ getStatusText(row.agvStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="电量" width="180">
          <template #default="{ row }">
            <el-progress 
              :percentage="row.batteryLevel" 
              :color="getBatteryColor(row.batteryLevel)"
              :status="row.batteryLevel < 20 ? 'exception' : ''"
            />
          </template>
        </el-table-column>
        <el-table-column prop="currentLocation" label="当前位置" width="120" />
        <el-table-column prop="targetLocation" label="目标位置" width="120" />
        <el-table-column prop="totalDistance" label="累计距离(m)" width="100">
          <template #default="{ row }">
            {{ row.totalDistance || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button 
              v-if="row.agvStatus === 'idle' || row.agvStatus === 'working'"
              type="warning" 
              size="small" 
              @click="openChargeDialog(row)"
            >
              充电
            </el-button>
            <el-button 
              v-if="row.agvStatus === 'charging'"
              type="success" 
              size="small" 
              @click="stopCharging(row)"
            >
              停止充电
            </el-button>
            <el-button 
              v-if="row.agvStatus === 'fault'"
              type="info" 
              size="small" 
              @click="repairAgv(row)"
            >
              维修
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 充电站列表 -->
    <el-card class="station-card">
      <template #header>
        <div class="card-header">
          <span>充电站</span>
        </div>
      </template>

      <el-table :data="stationList" style="width: 100%">
        <el-table-column prop="stationCode" label="编号" width="120" />
        <el-table-column prop="stationName" label="名称" width="150" />
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'info'">
              {{ row.status === 0 ? '禁用' : row.status === 1 ? '启用' : '使用中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="power" label="功率(W)" width="100" />
        <el-table-column prop="voltage" label="电压(V)" width="100" />
        <el-table-column prop="description" label="描述" />
      </el-table>
    </el-card>

    <!-- 充电对话框 -->
    <el-dialog v-model="chargeDialogVisible" title="开始充电" width="400px">
      <el-form :model="chargeForm" label-width="80px">
        <el-form-item label="小车">
          <el-input v-model="chargeForm.agvName" disabled />
        </el-form-item>
        <el-form-item label="当前电量">
          <el-input v-model="chargeForm.batteryLevel" disabled>
            <template #append>%</template>
          </el-input>
        </el-form-item>
        <el-form-item label="充电站">
          <el-select v-model="chargeForm.stationId" placeholder="请选择充电站">
            <el-option
              v-for="station in availableStations"
              :key="station.id"
              :label="station.stationName"
              :value="station.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="chargeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStartCharging">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { agvApi, chargingStationApi } from '@/api/warehouse'
import { Van, CircleCheck, Loading, Lightning, WarningFilled, Refresh } from '@element-plus/icons-vue'

// 数据
const loading = ref(false)
const agvList = ref<any[]>([])
const stationList = ref<any[]>([])
const availableStations = ref<any[]>([])
const chargeDialogVisible = ref(false)

// 自动刷新定时器
let autoRefreshTimer: ReturnType<typeof setInterval> | null = null

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
  }
  // 每3秒自动刷新一次
  autoRefreshTimer = setInterval(() => {
    refreshData(false) // 不显示loading
  }, 3000)
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
    autoRefreshTimer = null
  }
}

const chargeForm = reactive({
  agvId: 0,
  agvName: '',
  batteryLevel: 0,
  stationId: 0
})

// 统计
const stats = computed(() => {
  const list = agvList.value
  return {
    total: list.length,
    idle: list.filter((a: any) => a.agvStatus === 'idle').length,
    working: list.filter((a: any) => a.agvStatus === 'working').length,
    charging: list.filter((a: any) => a.agvStatus === 'charging').length,
    fault: list.filter((a: any) => a.agvStatus === 'fault').length
  }
})

// 获取状态类型
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    idle: 'success',
    working: 'primary',
    charging: 'warning',
    returning: 'warning',
    fault: 'danger'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    idle: '空闲',
    working: '工作中',
    charging: '充电中',
    returning: '返回中',
    fault: '故障'
  }
  return map[status] || status
}

// 获取电量颜色
const getBatteryColor = (level: number) => {
  if (level >= 60) return '#67c23a'
  if (level >= 20) return '#e6a23c'
  return '#f56c6c'
}

// 刷新数据
const refreshData = async (showLoading: boolean = true) => {
  if (showLoading) {
    loading.value = true
  }
  try {
    const [agvRes, stationRes] = await Promise.all([
      agvApi.getAgvList(),
      chargingStationApi.getStationList()
    ])
    agvList.value = agvRes.data || []
    stationList.value = stationRes.data || []
    
    // 获取可用充电站
    const stationAvailRes = await chargingStationApi.getAvailableStations()
    availableStations.value = stationAvailRes.data || []
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    if (showLoading) {
      loading.value = false
    }
  }
}

// 打开充电对话框
const openChargeDialog = (row: any) => {
  chargeForm.agvId = row.agvId
  chargeForm.agvName = row.equipmentName || row.equipmentCode
  chargeForm.batteryLevel = row.batteryLevel
  chargeForm.stationId = availableStations.value[0]?.id || 0
  chargeDialogVisible.value = true
}

// 确认开始充电
const confirmStartCharging = async () => {
  if (!chargeForm.stationId) {
    ElMessage.warning('请选择充电站')
    return
  }
  try {
    await agvApi.startCharging(chargeForm.agvId, chargeForm.stationId)
    ElMessage.success('开始充电成功')
    chargeDialogVisible.value = false
    refreshData()
  } catch (error) {
    ElMessage.error('开始充电失败')
  }
}

// 停止充电
const stopCharging = async (row: any) => {
  try {
    await agvApi.endCharging(row.agvId, 'success')
    ElMessage.success('停止充电成功')
    refreshData()
  } catch (error) {
    ElMessage.error('停止充电失败')
  }
}

// 维修
const repairAgv = async (row: any) => {
  try {
    await agvApi.updateStatus(row.agvId, 'idle')
    await agvApi.updateBattery(row.agvId, 50)
    ElMessage.success('维修完成，小车已恢复正常')
    refreshData()
  } catch (error) {
    ElMessage.error('维修失败')
  }
}

onMounted(() => {
  refreshData()
  // 启动自动刷新
  startAutoRefresh()
})

onUnmounted(() => {
  // 停止自动刷新
  stopAutoRefresh()
})
</script>

<style scoped>
.agv-container {
  padding: 20px;
}

.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
}

.stat-icon.total {
  background: #409eff20;
  color: #409eff;
}

.stat-icon.idle {
  background: #6723c20;
  color: #67c23a;
}

.stat-icon.working {
  background: #409eff20;
  color: #409eff;
}

.stat-icon.charging {
  background: #e6a23c20;
  color: #e6a23c;
}

.stat-icon.fault {
  background: #f56c6c20;
  color: #f56c6c;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.agv-list-card,
.station-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
