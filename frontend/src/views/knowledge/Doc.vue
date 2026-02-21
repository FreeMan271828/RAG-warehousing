<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">文档管理</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="docTitle" label="文档标题" />
        <el-table-column prop="docType" label="类型" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
const loading = ref(false)
const tableData = ref([
  { id: 1, docTitle: '设备操作手册', docType: 'PDF', viewCount: 100, createTime: '2024-01-01 10:00:00' }
])
</script>
<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
