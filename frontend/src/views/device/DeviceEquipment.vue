<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">设备管理</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="equipmentCode" label="设备编码" width="150" />
        <el-table-column prop="equipmentName" label="设备名称" width="180" />
        <el-table-column prop="modelName" label="型号" width="120" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'danger'">{{ ['', '正常', '待机', '故障'][row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button type="primary" link>编辑</el-button>
            <el-button type="danger" link>删除</el-button>
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
  { id: 1, equipmentCode: 'AGV-001', equipmentName: 'AGV搬运车1', modelName: 'AGV-001', location: 'A区', status: 1 },
  { id: 2, equipmentCode: 'STACKER-001', equipmentName: '堆垛机1', modelName: 'STACKER-001', location: 'B区', status: 1 }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
