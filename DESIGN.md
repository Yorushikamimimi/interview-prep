# Design System — 面试复习 App

## Product Context
- **What this is:** 本地面试复习工具，从 Markdown 文件解析题目，支持答题对比和进度追踪
- **Who it's for:** 开发者本人，单用户，笔记本浏览器使用
- **Space/industry:** 个人开发者工具 / 学习效率工具
- **Project type:** Web app（本地运行）

## Aesthetic Direction
- **Direction:** Industrial-Dark（工具感暗色）
- **Decoration level:** minimal（字体和色彩层次承担所有视觉工作，无装饰性元素）
- **Mood:** 像一个有品位的开发者工具——VS Code 的专注感，Linear 的精致感，不花哨但有自己的面孔。长时间刷题不累眼，代码和文字都舒适。
- **Reference:** Obsidian（暗色基调）、Linear（密度和精致感）

## Typography
- **Display/UI 标题:** Plus Jakarta Sans 700 — 现代干净，不是烂大街的 Inter，字重层次丰富
- **Body 正文:** Plus Jakarta Sans 400/500 — 阅读友好，中英文混排表现好
- **Code 代码:** JetBrains Mono 400/500 — 算法题必备，连字符好看，等宽数字支持
- **Metadata（题号/分类）:** JetBrains Mono — 强化「开发者工具」身份感
- **Loading:** Google Fonts CDN
- **Scale:**
  - display: 28px / 700
  - title: 20px / 600
  - body: 15px / 400
  - small: 13px / 400
  - label: 11px / 600 uppercase
  - mono-md: 13px
  - mono-sm: 11px

## Color
- **Approach:** restrained（accent 是稀缺资源，只用于最重要的交互节点）
- **Background:** `#111113` — 近黑非纯黑，有微妙暖意，长时间不刺眼
- **Surface:** `#1c1c20` — 卡片/面板背景，与底色色差分层
- **Surface Raised:** `#232328` — 悬浮/选中状态
- **Border:** `#2e2e35` — 分割线
- **Border Subtle:** `#202025` — 极细分割
- **Text-1:** `#e4e4e8` — 主文字，非纯白降低对比刺激
- **Text-2:** `#a0a0aa` — 次要文字
- **Text-3 (Muted):** `#60606a` — 辅助信息、占位符
- **Accent:** `#6366f1` — 冷调靛蓝，非 AI 烂大街紫色梯度
- **Accent Hover:** `#818cf8`
- **Accent Dim:** `rgba(99,102,241,.14)` — accent 背景
- **Mastered (Green):** `#22c55e` / dim: `rgba(34,197,94,.12)`
- **Unfamiliar (Yellow):** `#f59e0b` / dim: `rgba(245,158,11,.12)`
- **To-Review (Blue):** `#3b82f6` / dim: `rgba(59,130,246,.12)`
- **Dark mode:** 本项目仅暗色模式，无需亮色切换

## Spacing
- **Base unit:** 8px
- **Density:** comfortable（答题区留白充足，减少压迫感）
- **Scale:** 2(2px) 4(4px) 6(6px) 8(8px) 12(12px) 16(16px) 20(20px) 24(24px) 28(28px) 32(32px) 48(48px) 64(64px)

## Layout
- **Approach:** grid-disciplined（单列居中，内容优先）
- **Max content width:** 800px（答题页），900px（首页/统计页）
- **Grid:** 单列，无侧边栏
- **Border radius:**
  - sm: 6px（badge、小按钮）
  - md: 10px（按钮、输入框）
  - lg: 14px（卡片）
  - xl: 20px（大面板、mockup frame）
  - full: 9999px（tag、圆形badge）

## Motion
- **Approach:** intentional（只有承载意义的动效）
- **Answer reveal:** `max-height` + `opacity`，300ms ease-out — 答案展开是核心学习时刻，动效强化仪式感
- **Button hover:** `transform: translateY(-1px)` + `box-shadow`，150ms
- **Mark button press:** `transform: scale(1.04)`，180ms — 打标记要有力反馈
- **Easing:** enter `cubic-bezier(.4,0,.2,1)` / exit `cubic-bezier(.4,0,1,1)`
- **Duration:** micro 100ms / short 150ms / medium 250ms / reveal 300ms

## Design Risks (Intentional Departures)
1. **答案展开是「仪式感时刻」** — 不是加载更多内容，是带动效的区域从卡片下方滑出，颜色微深于题目区，视觉上像「翻开答案背面」
2. **标记按钮是情感中心** — 三个按钮够大，有 glow hover，点下去有 scale 反馈，打完标记才亮起「下一题」
3. **题号/分类用等宽字体** — `Redis · Q03 / 20` 用 JetBrains Mono，强化开发者工具身份，和普通笔记 app 拉开距离

## Decisions Log
| Date | Decision | Rationale |
|------|----------|-----------|
| 2026-04-02 | Industrial-Dark aesthetic | 用户偏好 Obsidian 风，长时间刷题护眼 |
| 2026-04-02 | Plus Jakarta Sans + JetBrains Mono | 现代不俗气，代码展示体验好 |
| 2026-04-02 | Accent #6366f1 indigo | 避免 AI 烂大街紫梯度，冷调靛蓝更沉稳 |
| 2026-04-02 | 单列居中布局，max-width 800px | 笔记本屏幕，单列阅读体验最佳 |
| 2026-04-02 | 仅暗色模式 | 用户场景是长时间刷题，暗色护眼 |
