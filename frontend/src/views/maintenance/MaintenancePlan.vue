<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">保养计划</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="planCode" label="计划编码" width="150" />
        <el-table-column prop="planName" label="计划名称" width="180" />
        <el-table-column prop="equipmentName" label="设备" width="140" />
        <el-table-column prop="executeDate" label="执行日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '执行中' : '待执行' }}</el-tag>
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
  { id: 1, planCode: 'MP-001', planName: '月度保养', equipmentName: '堆垛机A', executeDate: '2024-01-20', status: 1 },
  { id: 2, planCode: 'MP-002', planName: '季度保养', equipmentName: 'AGV-001', executeDate: '2024-01-25', status: 2 }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
