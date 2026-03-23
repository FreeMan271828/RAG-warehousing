<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">设备管理</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="equipmentCode" label="设备编码" width="150" />
        <el-table-column prop="equipmentName" label="设备名称" width="180" />
        <el-table-column prop="modelName" label="型号" width="120" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'danger'">{{ ['', '正常', '待机', '故障'][row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="warning" link @click="handleEdit(row)" v-if="canEditData">编辑</el-button>
            <el-button type="primary" link @click="handleView(row)" v-if="!canEditData">详情</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-if="canDeleteData">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 详情/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="设备编码">
          <el-input v-model="formData.equipmentCode" :disabled="!canEditData" />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="formData.equipmentName" :disabled="!canEditData" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="formData.modelName" :disabled="!canEditData" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="formData.location" :disabled="!canEditData" />
        </el-form-item>
        <el-form-item label="状态" v-if="canEditData">
          <el-select v-model="formData.status" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="待机" :value="2" />
            <el-option label="故障" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" v-else>
          <el-tag :type="formData.status === 1 ? 'success' : formData.status === 2 ? 'warning' : 'danger'">
            {{ ['', '正常', '待机', '故障'][formData.status] }}
          </el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleSubmit" v-if="canEditData">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { equipmentApi } from '@/api'
import { hasPermission } from '@/utils/permission'

// 权限编码
const PERMISSION_EDIT = 'device:equipment:edit'
const PERMISSION_DELETE = 'device:equipment:delete'

// 是否有编辑权限
const canEditData = computed(() => hasPermission(PERMISSION_EDIT))
// 是否有删除权限
const canDeleteData = computed(() => hasPermission(PERMISSION_DELETE))

const loading = ref(false)
const tableData = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('设备详情')
const formData = ref<any>({})

const loadData = async () => {
  loading.value = true
  try {
    const res = await equipmentApi.getEquipmentPage({ pageNum: 1, pageSize: 100 })
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
  formData.value = { ...row }
  dialogTitle.value = '设备详情'
  dialogVisible.value = true
}

// 编辑设备
const handleEdit = (row: any) => {
  formData.value = { ...row }
  dialogTitle.value = '编辑设备'
  dialogVisible.value = true
}

// 提交保存
const handleSubmit = async () => {
  try {
    await equipmentApi.updateEquipment(formData.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 删除设备
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该设备吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await equipmentApi.deleteEquipment(row.id)
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
