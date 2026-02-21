<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">托盘管理</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="palletCode" label="托盘编码" width="150" />
        <el-table-column prop="palletName" label="托盘名称" width="160" />
        <el-table-column prop="specification" label="规格" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
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
  { id: 1, palletCode: 'PLT-001', palletName: '标准托盘1', specification: '1200*1000mm', status: 1 },
  { id: 2, palletCode: 'PLT-002', palletName: '标准托盘2', specification: '1200*1000mm', status: 1 }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
