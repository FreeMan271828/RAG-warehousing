<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">实时充电监控</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="batteryCode" label="电池编码" width="140" />
        <el-table-column prop="chargeStartTime" label="开始时间" width="180" />
        <el-table-column prop="voltage" label="电压(V)" width="100" />
        <el-table-column prop="current" label="电流(A)" width="100" />
        <el-table-column prop="temperature" label="温度(°C)" width="100" />
        <el-table-column prop="soc" label="SOC(%)" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="success">充电中</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { chargingNowApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
let autoRefreshTimer: ReturnType<typeof setInterval> | null = null

// 启动自动刷新
const startAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
  }
  // 每2秒自动刷新一次（充电数据变化快）
  autoRefreshTimer = setInterval(() => {
    loadData(false)
  }, 2000)
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
    autoRefreshTimer = null
  }
}

const loadData = async (showLoading: boolean = true) => {
  if (showLoading) {
    loading.value = true
  }
  try {
    const res = await chargingNowApi.getChargingList()
    if (res.data) {
      tableData.value = res.data || []
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    if (showLoading) {
      loading.value = false
    }
  }
}

onMounted(() => {
  loadData()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
