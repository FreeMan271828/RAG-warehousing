<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">设备类别</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="categoryCode" label="类别编码" width="150" />
        <el-table-column prop="categoryName" label="类别名称" width="160" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
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
  { id: 1, categoryCode: 'EQUIPMENT', categoryName: '设备', description: '主要设备', sortOrder: 1, status: 1 },
  { id: 2, categoryCode: 'BATTERY', categoryName: '电池', description: '电池设备', sortOrder: 2, status: 1 },
  { id: 3, categoryCode: 'SENSOR', categoryName: '传感器', description: '传感器设备', sortOrder: 3, status: 1 }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
