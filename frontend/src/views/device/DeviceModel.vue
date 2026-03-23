<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">设备型号</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="modelCode" label="型号编码" width="150" />
        <el-table-column prop="modelName" label="型号名称" width="160" />
        <el-table-column prop="categoryName" label="所属类别" width="120" />
        <el-table-column prop="manufacturer" label="制造商" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
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
        <el-form-item label="型号编码">
          <el-input v-model="formData.modelCode" :disabled="!canEditData" />
        </el-form-item>
        <el-form-item label="型号名称">
          <el-input v-model="formData.modelName" :disabled="!canEditData" />
        </el-form-item>
        <el-form-item label="所属类别">
          <el-input v-model="formData.categoryName" :disabled="!canEditData" />
        </el-form-item>
        <el-form-item label="制造商">
          <el-input v-model="formData.manufacturer" :disabled="!canEditData" />
        </el-form-item>
        <el-form-item label="状态" v-if="canEditData">
          <el-select v-model="formData.status" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" v-else>
          <el-tag :type="formData.status === 1 ? 'success' : 'info'">{{ formData.status === 1 ? '启用' : '禁用' }}</el-tag>
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
import { deviceModelApi } from '@/api'
import { hasPermission } from '@/utils/permission'

// 权限编码
const PERMISSION_EDIT = 'device:model:edit'
const PERMISSION_DELETE = 'device:model:delete'

// 是否有编辑权限
const canEditData = computed(() => hasPermission(PERMISSION_EDIT))
// 是否有删除权限
const canDeleteData = computed(() => hasPermission(PERMISSION_DELETE))

const loading = ref(false)
const tableData = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('型号详情')
const formData = ref<any>({})

const loadData = async () => {
  loading.value = true
  try {
    const res = await deviceModelApi.getModelPage({ pageNum: 1, pageSize: 100 })
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
  dialogTitle.value = '型号详情'
  dialogVisible.value = true
}

// 编辑型号
const handleEdit = (row: any) => {
  formData.value = { ...row }
  dialogTitle.value = '编辑型号'
  dialogVisible.value = true
}

// 提交保存
const handleSubmit = async () => {
  try {
    await deviceModelApi.updateModel(formData.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 删除型号
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该设备型号吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deviceModelApi.deleteModel(row.id)
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
