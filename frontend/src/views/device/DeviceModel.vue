<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">设备型号</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="modelCode" label="型号编码" width="150" />
        <el-table-column prop="modelName" label="型号名称" width="160" />
        <el-table-column prop="categoryName" label="所属类别" width="120" />
        <el-table-column prop="manufacturer" label="制造商" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
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
  { id: 1, modelCode: 'AGV-001', modelName: 'AGV搬运车', categoryName: '设备', manufacturer: '海康机器人', status: 1 },
  { id: 2, modelCode: 'STACKER-001', modelName: '堆垛机', categoryName: '设备', manufacturer: '昆船物流', status: 1 }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
