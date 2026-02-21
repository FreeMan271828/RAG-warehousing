<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">出入库工单</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="orderCode" label="工单编码" width="150" />
        <el-table-column prop="orderType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.orderType === 'in' ? 'success' : 'warning'">{{ row.orderType === 'in' ? '入库' : '出库' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待执行</el-tag>
            <el-tag v-else-if="row.status === 'processing'" type="primary">执行中</el-tag>
            <el-tag v-else type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button type="primary" link>执行</el-button>
            <el-button type="success" link>详情</el-button>
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
  { id: 1, orderCode: 'IN-20240115-001', orderType: 'in', status: 'completed', createTime: '2024-01-15 10:00:00' },
  { id: 2, orderCode: 'OUT-20240115-001', orderType: 'out', status: 'pending', createTime: '2024-01-15 11:00:00' }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
