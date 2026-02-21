<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">部门管理</h2>
    </div>
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="deptName" label="部门名称" width="200" />
        <el-table-column prop="parentName" label="上级部门" width="160" />
        <el-table-column prop="leaderName" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button type="primary" link>编辑</el-button>
            <el-button type="danger" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const loading = ref(false)
const tableData = ref([
  { id: 1, deptName: '延安烟厂', parentName: '-', leaderName: '张总', phone: '123456789', sortOrder: 1, status: 1 },
  { id: 2, deptName: '设备管理部', parentName: '延安烟厂', leaderName: '李经理', phone: '123456789', sortOrder: 1, status: 1 },
  { id: 3, deptName: '运维部', parentName: '延安烟厂', leaderName: '王经理', phone: '123456789', sortOrder: 2, status: 1 },
  { id: 4, deptName: '仓储部', parentName: '延安烟厂', leaderName: '赵经理', phone: '123456789', sortOrder: 3, status: 1 }
])
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 20px; .page-title { margin: 0; font-size: 20px; font-weight: 600; } }
.table-container { background-color: var(--card-bg); border-radius: 8px; padding: 20px; }
</style>
