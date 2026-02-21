<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">保养点配置</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="pointCode" label="点编码" width="150" />
        <el-table-column prop="pointName" label="点名称" width="180" />
        <el-table-column prop="checkContent" label="检查内容" />
        <el-table-column prop="cycleType" label="周期" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.cycleType === 'daily' ? '日' : row.cycleType === 'weekly' ? '周' : '月' }}</el-tag>
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
  { id: 1, pointCode: 'KP-001', pointName: '电机检查', checkContent: '检查电机运行状态', cycleType: 'daily' }
])
</script>
<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
