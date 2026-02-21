<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">视频管理</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="videoTitle" label="视频标题" />
        <el-table-column prop="duration" label="时长" width="100" />
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
  { id: 1, videoTitle: '设备操作教程', duration: '15:30', viewCount: 200, createTime: '2024-01-01 10:00:00' }
])
</script>
<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
