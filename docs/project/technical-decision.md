# Technical Decision

## 1. 项目类型
- 推荐：`Type A`（前后端都需要）
- 原因：后端负责读取本地 MD 文件、持久化标记记录；前端提供现代化 UI 和交互动效

## 2. 技术栈
- frontend: Vue 3 + Vite + Vue Router + axios
- backend: Spring Boot 3.x + Java 17
- database: SQLite（通过 spring-boot-starter-data-jpa + sqlite-jdbc）
- css: 原生 CSS（设计系统已定义，无需 UI 框架）
- markdown: marked.js（前端渲染）+ highlight.js（代码高亮）
- cache: 无（题目每次从 MD 文件实时读取，或内存缓存，无需 Redis）

## 3. 架构边界
- 单体 / 多服务：单体（前后端分离，但同机运行）
- 是否需要文件上传：否（直接读取本地路径）
- 是否需要搜索：否（本轮不做）
- 是否需要权限：否（单用户本地工具）
- 是否需要管理后台：否
- 是否需要异步任务：否

## 4. 本地开发方式
- 推荐：`Hybrid Dev`
- 原因：前端 `npm run dev`（Vite HMR 快），后端 `mvn spring-boot:run`，各自独立启动，开发体验最佳
- 前端通过 Vite proxy 转发 `/api` 请求到后端，避免跨域

## 5. 部署方式
- 本机本地运行，无需部署上线
- 运行方式：手动启动前后端进程即可（Process Only）

## 6. 端口规划
- frontend dev server: `3001`
- backend api: `8081`
- SQLite: 无端口（文件数据库，路径 `./data/interview.db`）

## 7. 目录结构规划
```
project1/
├── backend/                  # Spring Boot 项目
│   ├── src/
│   ├── pom.xml
│   └── data/                 # SQLite 数据库文件（gitignore）
├── frontend/                 # Vue 3 + Vite 项目
│   ├── src/
│   ├── package.json
│   └── vite.config.js
├── 面试/                      # 原始 Markdown 题库（只读）
├── docs/project/             # 工程文档
├── preview/                  # 设计预览页
├── DESIGN.md
└── README.md
```

## 8. MD 文件解析规则
- **八股文**（`面试/面经/后端/八股/*.md`）：按 `## 数字. 标题` 拆分，每个二级标题为一道题
- **算法题**（`面试/算法题/**/*.md`）：每个文件为一道题，文件名为题目标题
- **项目面经**（`面试/面经/项目面经/*.md`）：按 `## 标题` 拆分
- 分类名由目录结构推断

## 9. 外部依赖
- Google Fonts（Plus Jakarta Sans + JetBrains Mono）：前端加载，需联网
- 无其他外部服务依赖

## 10. 主要风险
- MD 文件格式不统一：需要对不同目录的文件格式分别处理
- 中文路径：Windows 环境下文件路径含中文，需确保 UTF-8 编码处理

## 11. 未决问题
- 无阻塞项
