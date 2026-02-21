<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">箱子管理</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="boxCode" label="箱子编码" width="150" />
        <el-table-column prop="boxName" label="箱子名称" width="160" />
        <el-table-column prop="specification" label="规格" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
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
  { id: 1, boxCode: 'BOX-001', boxName: '纸箱1', specification: '600*400*300mm', status: 1 },
  { id: 2, boxCode: 'BOX-002', boxName: '纸箱2', specification: '600*400*300mm', status: 1 }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
