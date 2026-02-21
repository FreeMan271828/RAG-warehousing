<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">维修记录</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="recordCode" label="记录编码" width="150" />
        <el-table-column prop="equipmentName" label="设备" width="140" />
        <el-table-column prop="faultDesc" label="故障描述" />
        <el-table-column prop="faultLevel" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.faultLevel === 1 ? 'danger' : row.faultLevel === 2 ? 'warning' : 'info'">{{ row.faultLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 3 ? 'success' : 'warning'">{{ ['', '', '', '已完成'][row.status] || '处理中' }}</el-tag>
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
  { id: 1, recordCode: 'MTR-001', equipmentName: 'AGV-001', faultDesc: '电机故障', faultLevel: 2, status: 3 }
])
</script>
<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
