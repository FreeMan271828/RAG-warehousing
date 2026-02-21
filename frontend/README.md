# 延安烟厂三维数字孪生系统 - 前端

## 项目介绍

这是一个基于 Vue 3 + TypeScript + Vite 构建的延安烟厂三维数字孪生系统前端项目。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **图表**: ECharts + vue-echarts
- **3D 渲染**: Three.js
- **样式**: SCSS
- **HTTP 客户端**: Axios

## 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/              # API 请求封装
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── layout/           # 布局组件
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 状态管理
│   ├── styles/           # 全局样式
│   ├── types/            # TypeScript 类型定义
│   ├── views/            # 页面组件
│   │   ├── battery/      # 电池管理
│   │   ├── dashboard/    # 仪表盘
│   │   ├── device/      # 设备管理
│   │   ├── knowledge/    # 知识库
│   │   ├── login/       # 登录
│   │   ├── maintenance/  # 运维管理
│   │   ├── system/      # 系统管理
│   │   ├── warehouse/   # 仓库管理
│   │   └── warehouse/   # 仓库3D
│   ├── App.vue           # 根组件
│   └── main.ts           # 入口文件
├── index.html            # HTML 入口
├── package.json          # 依赖配置
├── tsconfig.json         # TypeScript 配置
└── vite.config.ts        # Vite 配置
```

## 功能模块

### 1. 仪表盘
- 欢迎信息
- 统计数据卡片
- 库存统计图表
- 设备状态分布
- 充电趋势图
- 告警列表
- 保养计划

### 2. 仓库管理
- **3D仓库建模**: 使用 Three.js 实现三维仓库可视化
  - 多视角切换（俯视、前视、侧视）
  - 自动旋转
  - 货位点击查看详情
  - 实时统计面板
- 货位管理
- 托盘管理
- 箱子管理
- 出入库工单

### 3. 设备管理
- 设备类别
- 设备型号
- 设备管理
- 零件管理

### 4. 电池管理
- 电池列表
- 实时充电监控
- 充电历史
- 故障记录

### 5. 运维管理
- 保养计划
- 保养记录
- 维修记录
- 零件更换
- 保养点配置

### 6. 知识库
- 文档管理
- 视频管理

### 7. 系统管理
- 用户管理
- 角色管理
- 部门管理

## 主题系统

系统支持亮色和暗色两种主题：

- **亮色主题**: 白色背景，蓝色主色调
- **暗色主题**: 深蓝背景，更适合长时间查看

主题会自动记住用户的设置，并支持系统主题跟随。

## 开发

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

项目将在 http://localhost:3000 启动。

### 构建生产版本

```bash
npm run build
```

## API 配置

项目的 API 请求代理配置在 `vite.config.ts` 中：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

默认代理到后端服务 http://localhost:8080，请根据实际情况修改。

## 登录说明

演示环境使用以下账号登录：
- 用户名: admin
- 密码: admin123

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 许可证

MIT License
