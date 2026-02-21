<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">保养记录</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="recordCode" label="记录编码" width="150" />
        <el-table-column prop="equipmentName" label="设备" width="140" />
        <el-table-column prop="executeDate" label="执行日期" width="120" />
        <el-table-column prop="result" label="结果" width="100">
          <template #default="{ row }">
            <el-tag type="success">合格</el-tag>
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
  { id: 1, recordCode: 'MR-001', equipmentName: '堆垛机A', executeDate: '2024-01-15', result: '合格' }
])
</script>
<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
