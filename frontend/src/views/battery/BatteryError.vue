<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">故障记录</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="batteryCode" label="电池编码" width="140" />
        <el-table-column prop="errorType" label="故障类型" width="120" />
        <el-table-column prop="errorDesc" label="故障描述" />
        <el-table-column prop="errorTime" label="故障时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isResolved ? 'success' : 'danger'">{{ row.isResolved ? '已解决' : '未解决' }}</el-tag>
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
  { id: 1, batteryCode: 'BT-001', errorType: '温度过高', errorDesc: '电池温度超过60°C', errorTime: '2024-01-15 10:00:00', isResolved: false },
  { id: 2, batteryCode: 'BT-002', errorType: '充电异常', errorDesc: '充电电流不稳定', errorTime: '2024-01-14 15:00:00', isResolved: true }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
