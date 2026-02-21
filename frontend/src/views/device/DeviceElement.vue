<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">零件管理</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="elementCode" label="零件编码" width="150" />
        <el-table-column prop="elementName" label="零件名称" width="180" />
        <el-table-column prop="specification" label="规格" />
        <el-table-column prop="stockQuantity" label="库存" width="100" />
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
  { id: 1, elementCode: 'ELM-001', elementName: '电机', specification: 'DC24V', stockQuantity: 10 },
  { id: 2, elementCode: 'ELM-002', elementName: '传感器', specification: '红外', stockQuantity: 20 }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
