<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">文档管理</h2>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="文档标题">
          <el-input v-model="searchForm.docTitle" placeholder="请输入文档标题" clearable />
        </el-form-item>
        <el-form-item label="文档类型">
          <el-select v-model="searchForm.docType" placeholder="请选择类型" clearable>
            <el-option label="PDF" value="PDF" />
            <el-option label="Word" value="Word" />
            <el-option label="Excel" value="Excel" />
            <el-option label="文本" value="文本" />
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
    <div class="action-bar" v-if="canAddData">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增文档
      </el-button>
      <el-button type="danger" :disabled="!selectedRows.length" @click="handleBatchDelete" v-if="canDeleteData">
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
        <el-table-column prop="docTitle" label="文档标题" min-width="200" />
        <el-table-column prop="docType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.docType === 'PDF'" type="danger">PDF</el-tag>
            <el-tag v-else-if="row.docType === 'Word'" type="primary">Word</el-tag>
            <el-tag v-else-if="row.docType === 'Excel'" type="success">Excel</el-tag>
            <el-tag v-else type="info">文本</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" link @click="handleEdit(row)" v-if="canEditData">编辑</el-button>
            <el-button type="primary" link @click="handleView(row)" v-if="!canEditData">查看</el-button>
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
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="文档标题" prop="docTitle">
          <el-input v-model="form.docTitle" placeholder="请输入文档标题" />
        </el-form-item>
        <el-form-item label="文档类型" prop="docType">
          <el-select v-model="form.docType" placeholder="请选择文档类型">
            <el-option label="PDF" value="PDF" />
            <el-option label="Word" value="Word" />
            <el-option label="Excel" value="Excel" />
            <el-option label="文本" value="文本" />
          </el-select>
        </el-form-item>
        <el-form-item label="文档内容" prop="docContent">
          <el-input v-model="form.docContent" type="textarea" :rows="6" placeholder="请输入文档内容" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="查看文档" width="700px">
      <div v-if="currentRow" class="view-content">
        <div class="view-item">
          <span class="label">文档标题：</span>
          <span>{{ currentRow.docTitle }}</span>
        </div>
        <div class="view-item">
          <span class="label">文档类型：</span>
          <el-tag>{{ currentRow.docType }}</el-tag>
        </div>
        <div class="view-item">
          <span class="label">浏览量：</span>
          <span>{{ currentRow.viewCount }}</span>
        </div>
        <div class="view-item">
          <span class="label">创建时间：</span>
          <span>{{ currentRow.createTime }}</span>
        </div>
        <div class="view-item">
          <span class="label">文档内容：</span>
          <div class="content-box">{{ currentRow.docContent || '无' }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { guideDocApi } from '@/api'
import { hasPermission, canView } from '@/utils/permission'

// 权限编码
const PERMISSION_VIEW = 'knowledge:doc:view'
const PERMISSION_ADD = 'knowledge:doc:add'
const PERMISSION_EDIT = 'knowledge:doc:edit'
const PERMISSION_DELETE = 'knowledge:doc:delete'

// 是否有查看权限
const canViewData = computed(() => canView())
// 是否有新增权限
const canAddData = computed(() => hasPermission(PERMISSION_ADD))
// 是否有编辑权限
const canEditData = computed(() => hasPermission(PERMISSION_EDIT))
// 是否有删除权限
const canDeleteData = computed(() => hasPermission(PERMISSION_DELETE))
// 是否有任何管理权限
const hasPermissionAny = computed(() => canAddData.value || canEditData.value || canDeleteData.value)

// 搜索表单
const searchForm = reactive({
  docTitle: '',
  docType: ''
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref([])

const selectedRows = ref<any[]>([])

// 对话框
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('新增文档')
const formRef = ref<FormInstance>()
const currentRow = ref<any>(null)

const form = reactive({
  id: undefined as number | undefined,
  docTitle: '',
  docType: '',
  docContent: '',
  remark: ''
})

const rules: FormRules = {
  docTitle: [{ required: true, message: '请输入文档标题', trigger: 'blur' }],
  docType: [{ required: true, message: '请选择文档类型', trigger: 'change' }]
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    docTitle: '',
    docType: ''
  })
  handleSearch()
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      docTitle: searchForm.docTitle || undefined,
      docType: searchForm.docType || undefined
    }
    const res = await guideDocApi.getDocPage(params)
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

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增文档'
  Object.assign(form, {
    id: undefined,
    docTitle: '',
    docType: '',
    docContent: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑文档'
  try {
    const res = await guideDocApi.getDocById(row.id)
    if (res.data) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取文档详情失败')
  }
}

// 查看
const handleView = (row: any) => {
  currentRow.value = row
  viewDialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该文档吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await guideDocApi.deleteDoc(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (!selectedRows.value.length) return
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    for (const row of selectedRows.value) {
      await guideDocApi.deleteDoc(row.id)
    }
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 选择变化
const handleSelectionChange = (rows: any[]) => {
  selectedRows.value = rows
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          // 编辑 - 调用更新接口
          await guideDocApi.updateDoc(form)
          ElMessage.success('修改成功')
        } else {
          // 新增 - 调用创建接口
          await guideDocApi.createDoc(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(form.id ? '修改失败' : '新增失败')
      }
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
  }
}

.search-area {
  background-color: var(--card-bg);
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
  .search-form { margin-bottom: 0; }
}

.action-bar { margin-bottom: 16px; }

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

.view-content {
  .view-item {
    margin-bottom: 16px;
    .label {
      font-weight: 500;
      color: #606266;
      margin-right: 8px;
    }
    .content-box {
      margin-top: 8px;
      padding: 12px;
      background: #f5f7fa;
      border-radius: 4px;
      white-space: pre-wrap;
      max-height: 300px;
      overflow-y: auto;
    }
  }
}
</style>
