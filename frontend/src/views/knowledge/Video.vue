<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">视频管理</h2>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="视频标题">
          <el-input v-model="searchForm.videoTitle" placeholder="请输入视频标题" clearable />
        </el-form-item>
        <el-form-item label="视频分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类" clearable>
            <el-option label="培训" value="培训" />
            <el-option label="演示" value="演示" />
            <el-option label="教程" value="教程" />
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
        <el-icon><Plus /></el-icon>新增视频
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
        <el-table-column prop="videoTitle" label="视频标题" min-width="200" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.category === '培训'" type="primary">培训</el-tag>
            <el-tag v-else-if="row.category === '演示'" type="success">演示</el-tag>
            <el-tag v-else type="warning">教程</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="{ row }">
            {{ formatDuration(row.duration) }}
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
        <el-form-item label="视频标题" prop="videoTitle">
          <el-input v-model="form.videoTitle" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="视频分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择视频分类">
            <el-option label="培训" value="培训" />
            <el-option label="演示" value="演示" />
            <el-option label="教程" value="教程" />
          </el-select>
        </el-form-item>
        <el-form-item label="视频时长" prop="duration">
          <el-input v-model="form.duration" placeholder="如: 05:30" />
        </el-form-item>
        <el-form-item label="视频地址" prop="videoUrl">
          <el-input v-model="form.videoUrl" placeholder="请输入视频URL地址" />
        </el-form-item>
        <el-form-item label="视频描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入视频描述" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="查看视频" width="700px">
      <div v-if="currentRow" class="view-content">
        <div class="view-item">
          <span class="label">视频标题：</span>
          <span>{{ currentRow.videoTitle }}</span>
        </div>
        <div class="view-item">
          <span class="label">视频分类：</span>
          <el-tag>{{ currentRow.category }}</el-tag>
        </div>
        <div class="view-item">
          <span class="label">视频时长：</span>
          <span>{{ formatDuration(currentRow.duration) }}</span>
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
          <span class="label">视频地址：</span>
          <el-link v-if="currentRow.videoUrl" type="primary" :href="currentRow.videoUrl" target="_blank">
            {{ currentRow.videoUrl }}
          </el-link>
          <span v-else>无</span>
        </div>
        <div class="view-item">
          <span class="label">视频描述：</span>
          <div class="content-box">{{ currentRow.description || '无' }}</div>
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
import { guideVideoApi } from '@/api'
import { hasPermission, canView } from '@/utils/permission'

// 权限编码
const PERMISSION_VIEW = 'knowledge:video:view'
const PERMISSION_ADD = 'knowledge:video:add'
const PERMISSION_EDIT = 'knowledge:video:edit'
const PERMISSION_DELETE = 'knowledge:video:delete'

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
  videoTitle: '',
  category: ''
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
const dialogTitle = ref('新增视频')
const formRef = ref<FormInstance>()
const currentRow = ref<any>(null)

const form = reactive({
  id: undefined as number | undefined,
  videoTitle: '',
  category: '',
  duration: '',
  videoUrl: '',
  description: '',
  remark: ''
})

const rules: FormRules = {
  videoTitle: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择视频分类', trigger: 'change' }]
}

// 格式化时长（秒转为 mm:ss）
const formatDuration = (duration: any) => {
  if (!duration) return '00:00'
  const minutes = Math.floor(duration / 60)
  const seconds = duration % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 转换时长（mm:ss 转为秒数）
const parseDuration = (durationStr: string): number => {
  if (!durationStr) return 0
  const parts = durationStr.split(':')
  if (parts.length === 2) {
    const minutes = parseInt(parts[0], 10) || 0
    const seconds = parseInt(parts[1], 10) || 0
    return minutes * 60 + seconds
  }
  return parseInt(durationStr, 10) || 0
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    videoTitle: '',
    category: ''
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
      videoTitle: searchForm.videoTitle || undefined,
      category: searchForm.category || undefined
    }
    const res = await guideVideoApi.getVideoPage(params)
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
  dialogTitle.value = '新增视频'
  Object.assign(form, {
    id: undefined,
    videoTitle: '',
    category: '',
    duration: '',
    videoUrl: '',
    description: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑视频'
  try {
    const res = await guideVideoApi.getVideoById(row.id)
    if (res.data) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取视频详情失败')
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
    await ElMessageBox.confirm('确定要删除该视频吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await guideVideoApi.deleteVideo(row.id)
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
      await guideVideoApi.deleteVideo(row.id)
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
        // 转换时长为秒数
        const submitData = { ...form, duration: parseDuration(form.duration) }
        
        if (submitData.id) {
          // 编辑 - 调用更新接口
          await guideVideoApi.updateVideo(submitData)
          ElMessage.success('修改成功')
        } else {
          // 新增 - 调用创建接口
          await guideVideoApi.createVideo(submitData)
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
      max-height: 200px;
      overflow-y: auto;
    }
  }
}
</style>
