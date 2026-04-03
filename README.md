# 面试复习 App

本地面试复习工具。从 Markdown 文件自动解析题目，答题后对比参考答案，打标记追踪掌握情况。

## 快速启动

### 前置要求
- Java 17+
- Maven 3.9+
- Node.js 18+

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
# 运行在 http://localhost:8081
```

### 2. 启动前端

```bash
cd frontend
npm install   # 首次运行
npm run dev
# 运行在 http://localhost:3001
```

打开浏览器访问 http://localhost:3001

## 配置

复制 `.env.example` 为 `.env`，按需修改题库路径：

```bash
cp .env.example .env
```

后端读取 `.env` 中的 `CONTENT_PATH`，默认指向项目根目录下的 `面试/` 文件夹。

## 项目结构

```
project1/
├── backend/          # Spring Boot 后端（端口 8081）
├── frontend/         # Vue 3 + Vite 前端（端口 3001）
├── 面试/             # Markdown 题库（只读）
├── docs/project/     # 工程文档
├── preview/          # 设计预览页
├── DESIGN.md         # 设计系统
└── README.md
```

## 题库格式

- **八股文**：`面试/面经/后端/八股/*.md`，每个 `## 标题` 为一道题
- **算法题**：`面试/算法题/**/*.md`，每个文件为一道题
- **项目面经**：`面试/面经/项目面经/*.md`，每个 `## 标题` 为一道题
