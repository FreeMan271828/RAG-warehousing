<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">电池列表</h2>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="电池编码">
          <el-input v-model="searchForm.batteryCode" placeholder="请输入电池编码" clearable />
        </el-form-item>
        <el-form-item label="电池名称">
          <el-input v-model="searchForm.batteryName" placeholder="请输入电池名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="使用中" :value="2" />
            <el-option label="充电中" :value="3" />
            <el-option label="故障" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增电池
      </el-button>
      <el-button type="danger" :disabled="!selectedRows.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>批量删除
      </el-button>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="batteryCode" label="电池编码" width="140" />
        <el-table-column prop="batteryName" label="电池名称" width="160" />
        <el-table-column prop="batteryType" label="电池类型" width="100" />
        <el-table-column prop="batteryModel" label="型号" width="100" />
        <el-table-column prop="manufacturer" label="制造商" width="140" />
        <el-table-column prop="healthStatus" label="健康度" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.healthStatus" :color="getHealthColor(row.healthStatus)" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" link @click="handleEdit(row)" v-if="canEditData">编辑</el-button>
            <el-button type="primary" link @click="handleView(row)" v-if="!canEditData">详情</el-button>
            <el-button type="success" link @click="handleCharge(row)">充电</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-if="canDeleteData">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电池编码" prop="batteryCode">
              <el-input v-model="form.batteryCode" placeholder="请输入电池编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电池名称" prop="batteryName">
              <el-input v-model="form.batteryName" placeholder="请输入电池名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电池类型" prop="batteryType">
              <el-select v-model="form.batteryType" placeholder="请选择电池类型">
                <el-option label="磷酸铁锂" value="磷酸铁锂" />
                <el-option label="三元锂" value="三元锂" />
                <el-option label="铅酸" value="铅酸" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号" prop="batteryModel">
              <el-input v-model="form.batteryModel" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="制造商" prop="manufacturer">
              <el-input v-model="form.manufacturer" placeholder="请输入制造商" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="位置" prop="location">
              <el-input v-model="form.location" placeholder="请输入位置" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="额定容量" prop="ratedCapacity">
              <el-input-number v-model="form.ratedCapacity" :min="0" :precision="2" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="额定电压" prop="ratedVoltage">
              <el-input-number v-model="form.ratedVoltage" :min="0" :precision="2" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status">
                <el-option label="正常" :value="1" />
                <el-option label="使用中" :value="2" />
                <el-option label="充电中" :value="3" />
                <el-option label="故障" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { batteryApi } from '@/api'
import { hasPermission } from '@/utils/permission'

// 权限编码
const PERMISSION_EDIT = 'battery:edit'
const PERMISSION_DELETE = 'battery:delete'

// 是否有编辑权限
const canEditData = computed(() => hasPermission(PERMISSION_EDIT))
// 是否有删除权限
const canDeleteData = computed(() => hasPermission(PERMISSION_DELETE))

const searchForm = reactive({
  batteryCode: '',
  batteryName: '',
  status: null as number | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const loading = ref(false)
const tableData = ref([])

const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增电池')
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as number | undefined,
  batteryCode: '',
  batteryName: '',
  batteryType: '',
  batteryModel: '',
  manufacturer: '',
  ratedCapacity: 0,
  ratedVoltage: 0,
  status: 1,
  location: '',
  remark: ''
})

const rules: FormRules = {
  batteryCode: [{ required: true, message: '请输入电池编码', trigger: 'blur' }],
  batteryName: [{ required: true, message: '请输入电池名称', trigger: 'blur' }],
  batteryType: [{ required: true, message: '请选择电池类型', trigger: 'change' }]
}

const getHealthColor = (percentage: number) => {
  if (percentage >= 80) return '#67c23a'
  if (percentage >= 60) return '#e6a23c'
  return '#f56c6c'
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'success',
    2: 'primary',
    3: 'warning',
    4: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    1: '正常',
    2: '使用中',
    3: '充电中',
    4: '故障'
  }
  return map[status] || '未知'
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, { batteryCode: '', batteryName: '', status: null })
  handleSearch()
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await batteryApi.getBatteryPage(params)
    if (res.data) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增电池'
  Object.assign(form, {
    id: undefined,
    batteryCode: '',
    batteryName: '',
    batteryType: '',
    batteryModel: '',
    manufacturer: '',
    ratedCapacity: 0,
    ratedVoltage: 0,
    status: 1,
    location: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑电池'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = (row: any) => {
  ElMessage.info(`查看电池详情: ${row.batteryCode}`)
}

const handleCharge = (row: any) => {
  ElMessage.success(`开始充电: ${row.batteryCode}`)
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该电池吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleBatchDelete = () => {
  if (!selectedRows.value.length) return
  ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleSelectionChange = (rows: any[]) => {
  selectedRows.value = rows
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.page-header {
  margin-bottom: 20px;
  
  .page-title {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: var(--text-color);
  }
}

.search-area {
  background-color: var(--card-bg);
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.action-bar {
  margin-bottom: 16px;
}

.table-container {
  background-color: var(--card-bg);
  border-radius: 8px;
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
