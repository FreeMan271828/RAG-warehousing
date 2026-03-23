<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">保养计划</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="planCode" label="计划编码" width="150" />
        <el-table-column prop="planName" label="计划名称" width="180" />
        <el-table-column prop="equipmentName" label="设备" width="140" />
        <el-table-column prop="executeDate" label="执行日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '执行中' : '待执行' }}</el-tag>
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
import { maintenancePlanApi } from '@/api'
import { hasPermission } from '@/utils/permission'

// 权限编码
const PERMISSION_EDIT = 'maintenance:plan:edit'
const PERMISSION_DELETE = 'maintenance:plan:delete'

// 是否有编辑权限
const canEditData = computed(() => hasPermission(PERMISSION_EDIT))
// 是否有删除权限
const canDeleteData = computed(() => hasPermission(PERMISSION_DELETE))

const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await maintenancePlanApi.getPlanPage({ pageNum: 1, pageSize: 100 })
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
  ElMessage.info(`查看保养计划: ${row.planName}`)
}

// 编辑
const handleEdit = (row: any) => {
  ElMessage.info(`编辑保养计划: ${row.planName}`)
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该保养计划吗？', '提示', {
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
