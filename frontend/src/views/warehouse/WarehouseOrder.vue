<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">出入库工单</h2>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="工单编码">
          <el-input v-model="searchForm.orderCode" placeholder="请输入工单编码" clearable />
        </el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="searchForm.orderType" placeholder="请选择类型" clearable>
            <el-option label="入库" value="in" />
            <el-option label="出库" value="out" />
            <el-option label="调拨" value="transfer" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单状态">
          <el-select v-model="searchForm.orderStatus" placeholder="请选择状态" clearable>
            <el-option v-for="status in statusList" :key="status.code" :label="status.name" :value="status.code" />
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
        <el-icon><Plus /></el-icon>新建工单
      </el-button>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="orderCode" label="工单编码" width="160" />
        <el-table-column prop="orderType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.orderType === 'in' ? 'success' : row.orderType === 'out' ? 'warning' : 'primary'">
              {{ getTypeName(row.orderType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.orderStatus)">{{ getStatusName(row.orderStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="itemName" label="物料名称" min-width="120" />
        <el-table-column prop="itemQuantity" label="数量" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <!-- 编辑按钮：有编辑权限时显示 -->
            <el-button type="warning" link @click="handleEdit(row)" v-if="canEditData">编辑</el-button>
            <!-- 状态推进按钮：有状态推进权限或有可用操作时显示 -->
            <el-dropdown @command="(cmd: string) => handleTransition(row, cmd)" v-if="canTransition || getAvailableActionsSync(row).length > 0">
              <el-button type="success" link>
                状态推进<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="action in getAvailableActionsSync(row)" :key="action" :command="action">
                    {{ getActionName(action) }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <!-- 无编辑权限时显示详情 -->
            <el-button type="primary" link @click="handleView(row)" v-if="!canEditData">详情</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单类型" prop="orderType">
              <el-select v-model="form.orderType" placeholder="请选择工单类型" style="width: 100%" @change="handleOrderTypeChange">
                <el-option label="入库" value="in" />
                <el-option label="出库" value="out" />
                <el-option label="调拨" value="transfer" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.orderType === 'transfer'">
            <el-form-item label="调拨类型" prop="transferType">
              <el-select v-model="form.transferType" placeholder="请选择调拨类型" style="width: 100%">
                <el-option label="内部调拨" value="internal" />
                <el-option label="外部调拨" value="external" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option label="低" :value="1" />
                <el-option label="中" :value="2" />
                <el-option label="高" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">物料信息</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="物料编码" prop="itemCode">
              <el-input v-model="form.itemCode" placeholder="请输入物料编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物料名称" prop="itemName">
              <el-input v-model="form.itemName" placeholder="请输入物料名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="物料数量" prop="itemQuantity">
              <el-input-number v-model="form.itemQuantity" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物料单位" prop="itemUnit">
              <el-input v-model="form.itemUnit" placeholder="如：箱、件、kg" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批次号">
              <el-input v-model="form.batchNo" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">{{ form.orderType === 'transfer' ? '调拨' : '目标' }}信息</el-divider>
        
        <!-- 内部调拨时显示来源和目标仓库 -->
        <template v-if="form.orderType === 'transfer'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="源仓库">
                <el-input v-model="form.sourceWarehouseName" placeholder="请输入源仓库名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="源货位">
                <el-input v-model="form.sourceLocationCode" placeholder="请输入源货位编码" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="目标仓库">
                <el-input v-model="form.targetWarehouseName" placeholder="请输入目标仓库名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="目标货位">
                <el-input v-model="form.targetLocationCode" placeholder="请输入目标货位编码" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
        <!-- 入库时显示货物来源 -->
        <template v-else-if="form.orderType === 'in'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="货物来源">
                <el-input v-model="form.sourceWarehouseName" placeholder="请输入货物来源" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="源货位">
                <el-input v-model="form.sourceLocationCode" placeholder="请输入源货位编码" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="目标货位">
                <el-input v-model="form.targetLocationCode" placeholder="请输入目标货位编码" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
        <!-- 出库时显示目标 -->
        <template v-else>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="目标仓库">
                <el-input v-model="form.targetWarehouseName" placeholder="请输入目标仓库" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="目标货位">
                <el-input v-model="form.targetLocationCode" placeholder="请输入目标货位编码" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
        
        <el-divider content-position="left">运输信息</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="承运人">
              <el-input v-model="form.transporter" placeholder="请输入承运人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车牌号">
              <el-input v-model="form.vehicleNo" placeholder="请输入车牌号" />
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
    
    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="工单详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单编码">{{ formData.orderCode }}</el-descriptions-item>
        <el-descriptions-item label="工单类型">
          <el-tag :type="formData.orderType === 'in' ? 'success' : formData.orderType === 'out' ? 'warning' : 'primary'">
            {{ getTypeName(formData.orderType) }}
          </el-tag>
          <el-tag v-if="formData.transferType" type="info" style="margin-left: 8px">
            {{ formData.transferType === 'internal' ? '内部调拨' : '外部调拨' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="工单状态" :span="2">
          <el-tag :type="getStatusColor(formData.orderStatus)">{{ getStatusName(formData.orderStatus) }}</el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="批次号">{{ formData.batchNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="formData.priority === 3 ? 'danger' : formData.priority === 2 ? 'warning' : 'info'">
            {{ ['', '低', '中', '高'][formData.priority] || '-' }}
          </el-tag>
        </el-descriptions-item>
        
        <el-divider content-position="left">物料信息</el-divider>
        
        <el-descriptions-item label="物料编码">{{ formData.itemCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物料名称">{{ formData.itemName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物料数量">{{ formData.itemQuantity || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物料单位">{{ formData.itemUnit || '-' }}</el-descriptions-item>
        
        <el-divider content-position="left">{{ formData.orderType === 'transfer' ? '调拨' : '目标' }}信息</el-divider>
        
        <el-descriptions-item label="源仓库">{{ formData.sourceWarehouseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="源货位">{{ formData.sourceLocationCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目标仓库">{{ formData.targetWarehouseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目标货位">{{ formData.targetLocationCode || '-' }}</el-descriptions-item>
        
        <el-divider content-position="left">运输信息</el-divider>
        
        <el-descriptions-item label="承运人">{{ formData.transporter || '-' }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ formData.vehicleNo || '-' }}</el-descriptions-item>
        
        <el-divider content-position="left">流程信息</el-divider>
        
        <el-descriptions-item label="创建时间">{{ formData.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ formData.operatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核人" v-if="formData.reviewerName">{{ formData.reviewerName }}</el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="formData.reviewTime">{{ formData.reviewTime }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" v-if="formData.reviewRemark" :span="2">{{ formData.reviewRemark }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ formData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 状态推进操作（仅编辑权限可见） -->
      <div v-if="canEditData && availableActions.length > 0" class="action-section">
        <el-divider>状态推进</el-divider>
        <el-space wrap>
          <el-button 
            v-for="action in availableActions" 
            :key="action" 
            :type="getActionButtonType(action)" 
            @click="handleTransitionFromDetail(action)"
          >
            {{ getActionName(action) }}
          </el-button>
        </el-space>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 状态推进确认对话框 -->
    <el-dialog v-model="transitionDialogVisible" title="确认状态推进" width="400px">
      <p>确定要将工单状态变更为 <strong>{{ getStatusName(pendingTransitionStatus) }}</strong> 吗？</p>
      <el-input v-model="transitionRemark" type="textarea" :rows="3" placeholder="请输入备注（可选）" style="margin-top: 16px" />
      <template #footer>
        <el-button @click="transitionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmTransition">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { inOutOrderApi } from '@/api'
import { hasPermission, getCurrentUserInfo } from '@/utils/permission'

// 权限编码
const PERMISSION_EDIT = 'warehouse:order:edit'
const PERMISSION_ADD = 'warehouse:order:add'
const PERMISSION_TRANSITION = 'warehouse:order:transition'
const PERMISSION_DELETE = 'warehouse:order:delete'

// 检查用户是否有任何仓库工单权限
const userInfo = getCurrentUserInfo()
const hasAnyWarehouseOrderPermission = computed(() => {
  if (!userInfo) return false
  // 管理员拥有所有权限
  if (userInfo.roleCodes.includes('admin')) return true
  // 仓库管理员
  if (userInfo.roleCodes.includes('warehouse_admin')) return true
  // 检查是否有任何工单相关权限
  const perms = userInfo.permissionCodes || []
  return perms.some(p => p.startsWith('warehouse:order:'))
})

// 是否有编辑权限（包含状态推进权限）
const canEditData = computed(() => hasPermission(PERMISSION_EDIT) || hasPermission(PERMISSION_TRANSITION) || hasAnyWarehouseOrderPermission.value)
// 是否有新增权限
const canAddData = computed(() => hasPermission(PERMISSION_ADD))
// 是否有状态推进权限（系统管理员和仓库管理员都显示按钮）
const canTransition = computed(() => hasPermission(PERMISSION_TRANSITION) || userInfo?.roleCodes.includes('admin') || userInfo?.roleCodes.includes('warehouse_admin') || hasAnyWarehouseOrderPermission.value)

// 状态列表
const statusList = ref([
  { code: 'created', name: '新建' },
  { code: 'pending_review', name: '待审核' },
  { code: 'rejected', name: '已拒绝' },
  { code: 'pending_in', name: '待入货' },
  { code: 'in_progress', name: '入库中' },
  { code: 'stored', name: '存储中' },
  { code: 'pending_transfer', name: '待调拨' },
  { code: 'transferring', name: '调拨中' },
  { code: 'transfer_completed', name: '调拨完成' },
  { code: 'out_progress', name: '出库中' },
  { code: 'completed', name: '已完成' },
  { code: 'cancelled', name: '已取消' }
])

// 搜索表单
const searchForm = reactive({
  orderCode: '',
  orderType: '',
  orderStatus: ''
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
const orderActionsMap = ref<Record<number, string[]>>({})

// 对话框
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const transitionDialogVisible = ref(false)
const dialogTitle = ref('新建工单')
const formRef = ref<FormInstance>()
const currentRow = ref<any>(null)
const availableActions = ref<string[]>([])
const pendingTransitionStatus = ref('')
const transitionRemark = ref('')

const form = reactive({
  id: undefined as number | undefined,
  orderType: 'in',
  transferType: 'internal',
  priority: 2,
  itemCode: '',
  itemName: '',
  itemQuantity: 0,
  itemUnit: '',
  // 货物来源
  sourceWarehouseCode: '',
  sourceWarehouseName: '',
  sourceLocationCode: '',
  // 目标
  targetWarehouseCode: '',
  targetWarehouseName: '',
  targetLocationCode: '',
  // 运输信息
  transporter: '',
  vehicleNo: '',
  batchNo: '',
  remark: ''
})

const formData = ref<any>({})

const rules: FormRules = {
  orderType: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
  itemCode: [{ required: true, message: '请输入物料编码', trigger: 'blur' }],
  itemName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }]
}

// 获取类型名称
const getTypeName = (type: string) => {
  const map: Record<string, string> = { in: '入库', out: '出库', transfer: '调拨' }
  return map[type] || type
}

// 获取状态名称
const getStatusName = (status: string) => {
  if (!status) return '-'
  const s = statusList.value.find(item => item.code === status)
  if (s) return s.name
  // 如果找不到，尝试翻译一些常见的状态
  const fallbackMap: Record<string, string> = {
    'pending': '待处理',
    'processing': '处理中',
    'pending_processing': '待处理'
  }
  return fallbackMap[status] || status
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    created: 'info',
    pending_review: 'warning',
    rejected: 'danger',
    pending_in: 'primary',
    in_progress: 'primary',
    stored: 'success',
    pending_transfer: 'primary',
    transferring: 'primary',
    transfer_completed: 'success',
    out_progress: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return colorMap[status] || 'info'
}

// 获取操作名称
const getActionName = (action: string) => {
  const actionMap: Record<string, string> = {
    // 提交审核
    pending_review: '提交审核',
    // 审核通过（根据工单类型显示不同）
    pending_in: '审核通过',
    pending_transfer: '审核通过',
    // 审核拒绝
    rejected: '重新提交',
    // 入库相关
    in_progress: '开始入库',
    stored: '入库完成',
    // 调拨相关
    transferring: '开始调拨',
    transfer_completed: '调拨完成',
    // 出库相关
    out_progress: '开始出库',
    // 完成
    completed: '完成工单'
  }
  return actionMap[action] || action
}

// 获取操作按钮类型
const getActionButtonType = (action: string) => {
  const typeMap: Record<string, string> = {
    pending_review: 'primary',
    pending_in: 'success',
    rejected: 'danger',
    in_progress: 'primary',
    stored: 'success',
    out_progress: 'primary',
    completed: 'success'
  }
  return typeMap[action] || 'primary'
}

// 获取可用操作
const getAvailableActions = async (row: any) => {
  // 如果已经有缓存，直接返回
  if (orderActionsMap.value[row.id]) {
    return orderActionsMap.value[row.id]
  }
  // 否则动态加载
  try {
    const res = await inOutOrderApi.getAvailableActions(row.id)
    if (res.data) {
      orderActionsMap.value[row.id] = res.data
      return res.data
    }
  } catch (error) {
    console.error('加载操作失败:', error)
  }
  return []
}

// 获取可用操作（同步版本，用于模板）
const getAvailableActionsSync = (row: any) => {
  return orderActionsMap.value[row.id] || []
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, { orderCode: '', orderType: '', orderStatus: '' })
  handleSearch()
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      orderCode: searchForm.orderCode || undefined,
      orderType: searchForm.orderType || undefined,
      orderStatus: searchForm.orderStatus || undefined
    }
    const res = await inOutOrderApi.getInOutPage(params)
    if (res.data) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
      
      // 加载每个工单的可用操作
      for (const row of tableData.value) {
        loadActions(row.id)
      }
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 加载可用操作
const loadActions = async (orderId: number) => {
  try {
    const res = await inOutOrderApi.getAvailableActions(orderId)
    if (res.data) {
      orderActionsMap.value[orderId] = res.data
    }
  } catch (error) {
    console.error('加载操作失败:', error)
  }
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新建工单'
  Object.assign(form, {
    id: undefined,
    orderType: 'in',
    transferType: 'internal',
    priority: 2,
    itemCode: '',
    itemName: '',
    itemQuantity: 0,
    itemUnit: '',
    sourceWarehouseCode: '',
    sourceWarehouseName: '',
    sourceLocationCode: '',
    targetWarehouseCode: '',
    targetWarehouseName: '',
    targetLocationCode: '',
    transporter: '',
    vehicleNo: '',
    batchNo: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 工单类型变更
const handleOrderTypeChange = () => {
  // 清空相关字段
  form.sourceWarehouseCode = ''
  form.sourceWarehouseName = ''
  form.sourceLocationCode = ''
  form.targetWarehouseCode = ''
  form.targetWarehouseName = ''
  form.targetLocationCode = ''
}

// 编辑
const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑工单'
  try {
    const res = await inOutOrderApi.getInOutById(row.id)
    if (res.data) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取工单详情失败')
  }
}

// 查看详情
const handleView = async (row: any) => {
  try {
    const res = await inOutOrderApi.getInOutById(row.id)
    if (res.data) {
      formData.value = res.data
      // 加载可用操作
      const actionsRes = await inOutOrderApi.getAvailableActions(row.id)
      availableActions.value = actionsRes.data || []
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取工单详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await inOutOrderApi.updateInOut(form)
          ElMessage.success('修改成功')
        } else {
          await inOutOrderApi.createInOut(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(form.id ? '修改失败' : '创建失败')
      }
    }
  })
}

// 状态推进（从下拉菜单）
const handleTransition = async (row: any, targetStatus: string) => {
  currentRow.value = row
  pendingTransitionStatus.value = targetStatus
  transitionRemark.value = ''
  
  // 先动态获取最新操作列表，确保状态未改变
  const actions = await getAvailableActions(row)
  if (!actions.includes(targetStatus)) {
    ElMessage.warning('工单状态已变化，请刷新页面')
    loadData()
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要将工单 "${row.orderCode}" 状态变更为 "${getStatusName(targetStatus)}" 吗？`,
      '确认状态推进',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await executeTransition(targetStatus, '')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('状态推进失败')
    }
  }
}

// 从详情对话框推进状态
const handleTransitionFromDetail = (targetStatus: string) => {
  pendingTransitionStatus.value = targetStatus
  transitionRemark.value = ''
  transitionDialogVisible.value = true
}

// 确认状态推进
const confirmTransition = async () => {
  await executeTransition(pendingTransitionStatus.value, transitionRemark.value)
  transitionDialogVisible.value = false
}

// 执行状态推进
const executeTransition = async (targetStatus: string, remark: string) => {
  const userInfo = getCurrentUserInfo()
  if (!userInfo) {
    ElMessage.error('请先登录')
    return
  }
  
  try {
    await inOutOrderApi.transitionStatus(
      currentRow.value.id,
      targetStatus,
      userInfo.id,
      userInfo.realName || userInfo.username,
      remark
    )
    ElMessage.success('状态推进成功')
    detailDialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '状态推进失败')
  }
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

.action-section {
  margin-top: 20px;
  padding-top: 20px;
}
</style>
