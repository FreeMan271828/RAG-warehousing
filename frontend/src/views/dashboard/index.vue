<template>
  <div class="dashboard-container">
    <!-- 欢迎标题 -->
    <div class="welcome-section">
      <div class="welcome-text">
        <h1>欢迎回来，管理员</h1>
        <p>这是延安烟厂三维数字孪生系统的实时数据监控面板</p>
      </div>
      <div class="current-time">
        <el-icon><Clock /></el-icon>
        <span>{{ currentTime }}</span>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-title">设备总数</span>
          <el-icon class="stat-icon equipment"><Monitor /></el-icon>
        </div>
        <div class="stat-value">128</div>
        <div class="stat-change up">
          <el-icon><TrendCharts /></el-icon>
          <span>较昨日 +12%</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-title">在线设备</span>
          <el-icon class="stat-icon online"><Connection /></el-icon>
        </div>
        <div class="stat-value">98</div>
        <div class="stat-change up">
          <el-icon><TrendCharts /></el-icon>
          <span>在线率 76.5%</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-title">电池数量</span>
          <el-icon class="stat-icon battery"><Battery /></el-icon>
        </div>
        <div class="stat-value">256</div>
        <div class="stat-change">
          <el-icon><TrendCharts /></el-icon>
          <span>健康率 92.3%</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-title">待处理告警</span>
          <el-icon class="stat-icon warning"><Warning /></el-icon>
        </div>
        <div class="stat-value">8</div>
        <div class="stat-change down">
          <el-icon><TrendCharts /></el-icon>
          <span>较昨日 -5</span>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 库存统计 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>库存统计</h3>
        </div>
        <v-chart class="chart" :option="stockOption" autoresize />
      </div>
      
      <!-- 设备状态分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>设备状态分布</h3>
        </div>
        <v-chart class="chart" :option="deviceStatusOption" autoresize />
      </div>
      
      <!-- 充电趋势 -->
      <div class="chart-card full-width">
        <div class="chart-header">
          <h3>近7天充电趋势</h3>
        </div>
        <v-chart class="chart" :option="chargeTrendOption" autoresize />
      </div>
    </div>
    
    <!-- 表格区域 -->
    <div class="tables-grid">
      <!-- 告警列表 -->
      <div class="table-card">
        <div class="table-header">
          <h3>最新告警</h3>
          <el-button type="primary" link>查看全部</el-button>
        </div>
        <el-table :data="alarms" style="width: 100%">
          <el-table-column prop="time" label="时间" width="160" />
          <el-table-column prop="device" label="设备" width="120" />
          <el-table-column prop="level" label="级别" width="80">
            <template #default="{ row }">
              <el-tag :type="row.level === '严重' ? 'danger' : row.level === '一般' ? 'warning' : 'info'" size="small">
                {{ row.level }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="message" label="告警信息" />
        </el-table>
      </div>
      
      <!-- 保养计划 -->
      <div class="table-card">
        <div class="table-header">
          <h3>待执行保养计划</h3>
          <el-button type="primary" link>查看全部</el-button>
        </div>
        <el-table :data="maintenancePlans" style="width: 100%">
          <el-table-column prop="planName" label="计划名称" width="160" />
          <el-table-column prop="equipment" label="设备" width="120" />
          <el-table-column prop="executeDate" label="执行日期" width="120" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === '待执行' ? 'warning' : 'success'" size="small">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, LineChart, BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import VChart from 'vue-echarts'

// 注册ECharts组件
use([CanvasRenderer, PieChart, LineChart, BarChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent])

// 当前时间
const currentTime = ref('')
let timeTimer: number

// 更新时间的函数
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 库存统计图表配置
const stockOption = ref({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    right: 10,
    top: 'center'
  },
  series: [
    {
      name: '库存',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 1048, name: '原料' },
        { value: 735, name: '成品' },
        { value: 580, name: '辅料' },
        { value: 484, name: '包材' },
        { value: 300, name: '其他' }
      ]
    }
  ]
})

// 设备状态分布图表配置
const deviceStatusOption = ref({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'horizontal',
    bottom: 0
  },
  series: [
    {
      name: '设备状态',
      type: 'pie',
      radius: '65%',
      data: [
        { value: 98, name: '运行中' },
        { value: 15, name: '待机' },
        { value: 10, name: '告警' },
        { value: 5, name: '离线' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

// 充电趋势图表配置
const chargeTrendOption = ref({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['充电次数', '充电电量', '平均时长']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '充电次数',
      type: 'line',
      smooth: true,
      data: [120, 132, 101, 134, 90, 230, 210],
      itemStyle: { color: '#409eff' }
    },
    {
      name: '充电电量',
      type: 'line',
      smooth: true,
      data: [220, 182, 191, 234, 290, 330, 310],
      itemStyle: { color: '#67c23a' }
    },
    {
      name: '平均时长',
      type: 'line',
      smooth: true,
      data: [150, 232, 201, 154, 190, 330, 410],
      itemStyle: { color: '#e6a23c' }
    }
  ]
})

// 告警列表数据
const alarms = ref([
  { time: '2024-01-15 14:30', device: 'AGV-001', level: '严重', message: '电池温度过高' },
  { time: '2024-01-15 13:20', device: '提升机A', level: '一般', message: '电机异常振动' },
  { time: '2024-01-15 12:15', device: '堆垛机B', level: '一般', message: '位置传感器偏移' },
  { time: '2024-01-15 11:00', device: '输送线C', level: '提示', message: '运行速度降低' }
])

// 保养计划数据
const maintenancePlans = ref([
  { planName: '月度设备检查', equipment: '堆垛机A', executeDate: '2024-01-16', status: '待执行' },
  { planName: '电池均衡充电', equipment: 'AGV-003', executeDate: '2024-01-16', status: '待执行' },
  { planName: '润滑保养', equipment: '输送线B', executeDate: '2024-01-17', status: '待执行' },
  { planName: '传感器校准', equipment: '提升机C', executeDate: '2024-01-18', status: '待执行' }
])

onMounted(() => {
  updateTime()
  timeTimer = window.setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timeTimer) {
    clearInterval(timeTimer)
  }
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 0;
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: linear-gradient(135deg, var(--primary-color) 0%, #667eea 100%);
  border-radius: 12px;
  color: #fff;
  
  .welcome-text {
    h1 {
      margin: 0 0 8px;
      font-size: 24px;
      font-weight: 600;
    }
    
    p {
      margin: 0;
      font-size: 14px;
      opacity: 0.9;
    }
  }
  
  .current-time {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    opacity: 0.9;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  
  .stat-card {
    background-color: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 12px;
    padding: 20px;
    
    .stat-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      
      .stat-title {
        font-size: 14px;
        color: var(--text-color-secondary);
      }
      
      .stat-icon {
        font-size: 24px;
        
        &.equipment {
          color: #409eff;
        }
        
        &.online {
          color: #67c23a;
        }
        
        &.battery {
          color: #e6a23c;
        }
        
        &.warning {
          color: #f56c6c;
        }
      }
    }
    
    .stat-value {
      font-size: 32px;
      font-weight: 600;
      color: var(--text-color);
      margin-bottom: 8px;
    }
    
    .stat-change {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: var(--text-color-secondary);
      
      &.up {
        color: #67c23a;
      }
      
      &.down {
        color: #f56c6c;
      }
    }
  }
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  
  .chart-card {
    background-color: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 12px;
    padding: 20px;
    
    &.full-width {
      grid-column: span 2;
    }
    
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h3 {
        margin: 0;
        font-size: 16px;
        color: var(--text-color);
      }
    }
    
    .chart {
      height: 300px;
    }
  }
}

.tables-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  
  .table-card {
    background-color: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 12px;
    padding: 20px;
    
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h3 {
        margin: 0;
        font-size: 16px;
        color: var(--text-color);
      }
    }
  }
}

// 响应式布局
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-grid {
    .chart-card {
      &.full-width {
        grid-column: span 1;
      }
    }
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .charts-grid,
  .tables-grid {
    grid-template-columns: 1fr;
  }
}
</style>
