# RuoYi Flowable Jimu 应用开发平台

基于 RuoYi-Vue-Plus 5.6.0，整合 Flowable 6.8 工作流引擎、bpmn-js 流程设计器和积木报表组件的通用应用软件开发平台。

## 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| 基础框架 | RuoYi-Vue-Plus | 5.6.0 |
| 流程引擎 | Flowable | 6.8.0 |
| 流程设计器 | bpmn-js | 17.9.0 |
| 报表组件 | JimuReport | 1.9.0 |
| 后端 | Spring Boot + MyBatis-Plus | Boot 3.5.12 |
| 前端 | Vue3 + Element Plus + Vite | Vue 3.4 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |

## 项目结构

```
ruoyi-flowable-jimu/
├── java/                        # 后端项目
│   ├── ruoyi-admin/            # Web服务入口
│   ├── ruoyi-common/           # 公共模块
│   ├── ruoyi-extend/           # 扩展模块
│   ├── ruoyi-modules/          # 业务模块
│   │   ├── ruoyi-system/      # 系统管理模块
│   │   ├── ruoyi-workflow/     # WarmFlow工作流(原有)
│   │   ├── ruoyi-flowable/     # Flowable工作流模块(新增)
│   │   └── ruoyi-report/       # 积木报表模块(新增)
│   └── pom.xml
│
├── ruoyi-ui/                    # 前端项目
│   ├── src/
│   │   ├── views/              # 页面视图
│   │   │   ├── login/         # 登录页面
│   │   │   ├── index/         # 首页
│   │   │   ├── system/         # 系统管理
│   │   │   ├── workflow/       # 流程管理
│   │   │   │   ├── designer/  # 流程设计器(bpmn-js)
│   │   │   │   ├── definition/ # 流程定义
│   │   │   │   ├── instance/  # 流程实例
│   │   │   │   └── task/       # 任务管理
│   │   │   ├── report/         # 报表中心
│   │   │   ├── monitor/        # 系统监控
│   │   │   └── tool/           # 系统工具
│   │   ├── layout/            # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── store/              # 状态管理
│   │   ├── api/                # API接口
│   │   └── utils/              # 工具函数
│   └── package.json
│
└── README.md
```

## 功能特性

### 1. 流程管理模块

#### 流程设计器（bpmn-js）
- 可视化拖拽式流程建模
- 支持开始事件、结束事件、用户任务、服务任务、网关等元素
- 流程属性和元素属性配置
- BPMN XML 导入导出
- 直接部署设计的流程

#### 流程定义管理
- 流程列表查看
- 版本管理
- 激活/挂起控制
- 流程图查看

#### 流程实例管理
- 实例列表监控
- 实例状态跟踪
- 实例取消操作

#### 任务管理
- 待办任务处理
- 已办任务查看
- 任务签收/办理

### 2. 报表中心模块

- 报表列表管理
- 报表预览
- 积木报表设计器集成
- 支持图表/表格报表
- 数据导出（Excel/PDF/Word）

### 3. 系统管理模块

- 用户管理 - 用户 CRUD、状态管理
- 角色管理 - 权限分配、数据权限
- 菜单管理 - 动态菜单、按钮权限
- 部门管理 - 组织架构、树形结构
- 岗位管理 - 岗位序列、人员分配
- 字典管理 - 字典类型和数据维护
- 参数管理 - 系统参数配置
- 多租户支持

### 4. 系统监控模块

- 在线用户监控
- 操作日志记录
- 登录日志查看

### 5. 系统工具模块

- 表单构建（预留）
- 代码生成（预留）

## 快速开始

### 后端启动

```bash
cd java
mvn clean install -DskipTests
cd ruoyi-admin
java -jar target/ruoyi-admin.jar
```

### 前端启动

```bash
cd ruoyi-ui
npm install
npm run dev
```

### 访问地址

- 前端：http://localhost:3000
- 后端：http://localhost:8080
- 接口文档：http://localhost:8080/swagger-ui.html

## License

MIT
