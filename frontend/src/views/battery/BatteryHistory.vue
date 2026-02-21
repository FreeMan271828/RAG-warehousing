<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">充电历史</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="batteryCode" label="电池编码" width="140" />
        <el-table-column prop="chargeStartTime" label="开始时间" width="180" />
        <el-table-column prop="chargeEndTime" label="结束时间" width="180" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="chargedCapacity" label="充电量(kWh)" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isCompleted ? 'success' : 'warning'">{{ row.isCompleted ? '已完成' : '未完成' }}</el-tag>
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
  { id: 1, batteryCode: 'BT-001', chargeStartTime: '2024-01-15 10:00:00', chargeEndTime: '2024-01-15 12:30:00', duration: 150, chargedCapacity: 8.5, isCompleted: true },
  { id: 2, batteryCode: 'BT-002', chargeStartTime: '2024-01-14 14:00:00', chargeEndTime: '2024-01-14 16:30:00', duration: 150, chargedCapacity: 9.2, isCompleted: true }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
