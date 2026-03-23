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
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { usedElementApi } from '@/api'

const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await usedElementApi.getUsedElementPage({ pageNum: 1, pageSize: 100 })
    if (res.data) {
      tableData.value = res.data.records || []
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const handleView = (row: any) => {
  ElMessage.info(`查看零件更换记录: ${row.recordCode}`)
}

onMounted(() => {
  loadData()
})
</script>
<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
