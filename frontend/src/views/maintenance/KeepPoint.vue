<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">保养点配置</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="pointCode" label="点编码" width="150" />
        <el-table-column prop="pointName" label="点名称" width="180" />
        <el-table-column prop="checkContent" label="检查内容" />
        <el-table-column prop="cycleType" label="周期" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.cycleType === 'daily' ? '日' : row.cycleType === 'weekly' ? '周' : '月' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="warning" link @click="handleEdit(row)" v-if="canEditData">编辑</el-button>
            <el-button type="primary" link @click="handleView(row)" v-if="!canEditData">详情</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-if="canDeleteData">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { keepPointApi } from '@/api'
import { hasPermission } from '@/utils/permission'

// 权限编码
const PERMISSION_EDIT = 'maintenance:point:edit'
const PERMISSION_DELETE = 'maintenance:point:delete'

// 是否有编辑权限
const canEditData = computed(() => hasPermission(PERMISSION_EDIT))
// 是否有删除权限
const canDeleteData = computed(() => hasPermission(PERMISSION_DELETE))

const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await keepPointApi.getKeepPointPage({ pageNum: 1, pageSize: 100 })
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
  ElMessage.info(`查看保养点: ${row.pointName}`)
}

// 编辑
const handleEdit = (row: any) => {
  ElMessage.info(`编辑保养点: ${row.pointName}`)
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该保养点吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>
<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
