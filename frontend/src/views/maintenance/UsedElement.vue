<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">零件更换</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="recordCode" label="记录编码" width="150" />
        <el-table-column prop="elementName" label="零件名称" width="140" />
        <el-table-column prop="replaceQuantity" label="数量" width="80" />
        <el-table-column prop="replaceDate" label="更换日期" width="120" />
      </el-table>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
const loading = ref(false)
const tableData = ref([
  { id: 1, recordCode: 'UE-001', elementName: '电机', replaceQuantity: 1, replaceDate: '2024-01-15' }
])
</script>
<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
