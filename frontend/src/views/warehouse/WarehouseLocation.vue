<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">货位管理</h2>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="货位编码">
          <el-input v-model="searchForm.locationCode" placeholder="请输入货位编码" clearable />
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="searchForm.areaCode" placeholder="请选择区域" clearable>
            <el-option label="A区" value="A" />
            <el-option label="B区" value="B" />
            <el-option label="C区" value="C" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="空闲" value="empty" />
            <el-option label="占用" value="occupied" />
            <el-option label="预留" value="reserved" />
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
        <el-icon><Plus /></el-icon>新增货位
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
        <el-table-column prop="locationCode" label="货位编码" width="150" />
        <el-table-column prop="areaName" label="区域" width="100" />
        <el-table-column prop="rowNum" label="行号" width="80" />
        <el-table-column prop="colNum" label="列号" width="80" />
        <el-table-column prop="levelNum" label="层号" width="80" />
        <el-table-column prop="locationType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.locationType === 'storage'" type="primary">存储位</el-tag>
            <el-tag v-else-if="row.locationType === 'picking'" type="success">拣选位</el-tag>
            <el-tag v-else type="warning">缓存位</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.currentStatus === 'empty'" type="success">空闲</el-tag>
            <el-tag v-else-if="row.currentStatus === 'occupied'" type="primary">占用</el-tag>
            <el-tag v-else type="warning">预留</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="容量(m³)" width="100" />
        <el-table-column prop="maxWeight" label="最大承重(kg)" width="120" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleView(row)">查看</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="货位编码" prop="locationCode">
          <el-input v-model="form.locationCode" placeholder="请输入货位编码" />
        </el-form-item>
        <el-form-item label="货位名称" prop="locationName">
          <el-input v-model="form.locationName" placeholder="请输入货位名称" />
        </el-form-item>
        <el-form-item label="区域" prop="areaCode">
          <el-select v-model="form.areaCode" placeholder="请选择区域">
            <el-option label="A区" value="A" />
            <el-option label="B区" value="B" />
            <el-option label="C区" value="C" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="行号" prop="rowNum">
              <el-input-number v-model="form.rowNum" :min="1" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="列号" prop="colNum">
              <el-input-number v-model="form.colNum" :min="1" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="层号" prop="levelNum">
              <el-input-number v-model="form.levelNum" :min="1" :max="20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="货位类型" prop="locationType">
          <el-radio-group v-model="form.locationType">
            <el-radio label="storage">存储位</el-radio>
            <el-radio label="picking">拣选位</el-radio>
            <el-radio label="buffer">缓存位</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">禁用</el-radio>
            <el-radio :label="1">启用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="容量(m³)" prop="capacity">
          <el-input-number v-model="form.capacity" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="最大承重(kg)" prop="maxWeight">
          <el-input-number v-model="form.maxWeight" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  locationCode: '',
  areaCode: '',
  status: ''
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    locationCode: 'A01-01-01',
    locationName: 'A区01-01-01',
    areaCode: 'A',
    areaName: 'A区',
    rowNum: 1,
    colNum: 1,
    levelNum: 1,
    locationType: 'storage',
    currentStatus: 'empty',
    capacity: 2.5,
    maxWeight: 500,
    description: '标准存储位'
  },
  {
    id: 2,
    locationCode: 'A01-01-02',
    locationName: 'A区01-01-02',
    areaCode: 'A',
    areaName: 'A区',
    rowNum: 1,
    colNum: 1,
    levelNum: 2,
    locationType: 'storage',
    currentStatus: 'occupied',
    capacity: 2.5,
    maxWeight: 500,
    description: '标准存储位'
  },
  {
    id: 3,
    locationCode: 'A01-02-01',
    locationName: 'A区01-02-01',
    areaCode: 'A',
    areaName: 'A区',
    rowNum: 1,
    colNum: 2,
    levelNum: 1,
    locationType: 'picking',
    currentStatus: 'reserved',
    capacity: 2.5,
    maxWeight: 500,
    description: '拣选位'
  }
])

const selectedRows = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增货位')
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as number | undefined,
  locationCode: '',
  locationName: '',
  areaCode: '',
  rowNum: 1,
  colNum: 1,
  levelNum: 1,
  locationType: 'storage',
  status: 1,
  capacity: 2.5,
  maxWeight: 500,
  description: ''
})

const rules: FormRules = {
  locationCode: [{ required: true, message: '请输入货位编码', trigger: 'blur' }],
  areaCode: [{ required: true, message: '请选择区域', trigger: 'change' }],
  locationType: [{ required: true, message: '请选择货位类型', trigger: 'change' }]
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    locationCode: '',
    areaCode: '',
    status: ''
  })
  handleSearch()
}

// 加载数据
const loadData = () => {
  loading.value = true
  setTimeout(() => {
    pagination.total = 100
    loading.value = false
  }, 500)
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增货位'
  Object.assign(form, {
    id: undefined,
    locationCode: '',
    locationName: '',
    areaCode: '',
    rowNum: 1,
    colNum: 1,
    levelNum: 1,
    locationType: 'storage',
    status: 1,
    capacity: 2.5,
    maxWeight: 500,
    description: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑货位'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 查看
const handleView = (row: any) => {
  ElMessage.info(`查看货位: ${row.locationCode}`)
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该货位吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadData()
  })
}

// 批量删除
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

// 选择变化
const handleSelectionChange = (rows: any[]) => {
  selectedRows.value = rows
}

// 提交表单
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

// 分页变化
const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
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
  
  .search-form {
    margin-bottom: 0;
  }
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
