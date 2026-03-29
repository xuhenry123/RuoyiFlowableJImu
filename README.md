# RuoYi Flowable Jimu 应用开发平台

基于 RuoYi-Vue-Plus 5.x，整合 Flowable 工作流引擎和积木报表组件的通用应用软件开发平台。

## 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| 基础框架 | RuoYi-Vue-Plus | 5.6.0 |
| 流程引擎 | Flowable | 6.8.0 |
| 报表组件 | JimuReport | 1.9.0 |
| 后端 | Spring Boot + MyBatis-Plus | Boot 3.5.12 |
| 前端 | Vue3 + Element Plus + Vite | Vue 3.4 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |

## 项目结构

```
ruoyi-flowable-jimu/
├── java/                    # 后端项目
│   ├── ruoyi-admin/         # Web服务入口
│   ├── ruoyi-common/       # 公共模块
│   ├── ruoyi-extend/       # 扩展模块
│   ├── ruoyi-modules/       # 业务模块
│   │   ├── ruoyi-system/   # 系统管理模块
│   │   ├── ruoyi-workflow/  # WarmFlow工作流(原有)
│   │   ├── ruoyi-flowable/ # Flowable工作流(新增)
│   │   └── ruoyi-report/    # 积木报表模块
│   └── pom.xml
│
├── ruoyi-ui/                # 前端项目
│   ├── src/
│   │   ├── views/          # 页面视图
│   │   ├── router/         # 路由配置
│   │   └── api/            # API接口
│   └── package.json
│
└── README.md
```

## 功能特性

### 工作流模块
- 流程定义管理（部署、激活、挂起）
- 流程实例管理（启动、监控）
- 任务管理（待办、已办、签收、办理）
- Flowable + WarmFlow 双引擎支持

### 报表模块
- 报表列表管理
- 报表预览
- 积木报表设计器集成
- 图表/表格报表支持

### 系统管理
- 用户管理
- 角色管理
- 菜单管理
- 部门管理
- 多租户支持

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
