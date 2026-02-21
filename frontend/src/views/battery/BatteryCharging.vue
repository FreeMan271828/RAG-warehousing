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
import { ref } from 'vue'
const loading = ref(false)
const tableData = ref([
  { id: 1, batteryCode: 'BT-001', chargeStartTime: '2024-01-15 10:00:00', voltage: 48.5, current: 10.5, temperature: 28, soc: 65 },
  { id: 2, batteryCode: 'BT-002', chargeStartTime: '2024-01-15 11:00:00', voltage: 50.2, current: 8.2, temperature: 25, soc: 80 }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
