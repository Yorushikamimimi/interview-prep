<template>
  <div class="home">
    <div class="home-inner">

      <!-- Header -->
      <div class="home-header">
        <div class="home-eyebrow">面试复习</div>
        <h1 class="home-title">今天刷什么？</h1>
        <p class="home-sub">选择分类和模式，开始练习</p>
      </div>

      <!-- Resume banner -->
      <div v-if="savedProgress" class="resume-banner">
        <div class="resume-info">
          <span class="resume-icon">⏸</span>
          <span class="resume-text">
            上次练习进行到第 <strong>{{ savedProgress.currentIndex + 1 }}</strong> /
            {{ savedProgress.questionIds.length }} 题
          </span>
        </div>
        <div class="resume-actions">
          <button class="btn-resume" @click="resumePractice">继续上次</button>
          <button class="text-btn" @click="clearSavedProgress">清除</button>
        </div>
      </div>

      <!-- Category selection -->
      <section class="home-section">
        <div class="section-label">
          选择分类
          <span class="section-hint">（可多选）</span>
        </div>

        <div v-if="loading" class="loading-text">加载中...</div>
        <div v-else-if="error" class="error-text">{{ error }}</div>
        <div v-else class="tag-grid">
          <button
            v-for="cat in categories"
            :key="cat.name"
            class="tag"
            :class="{ selected: selectedCategories.includes(cat.name) }"
            @click="toggleCategory(cat.name)"
          >
            {{ cat.name }}
            <span class="tag-count">{{ cat.count }}</span>
          </button>
        </div>

        <div class="tag-actions">
          <button class="text-btn" @click="selectAll">全选</button>
          <button class="text-btn" @click="clearAll">清空</button>
        </div>
      </section>

      <!-- Mode selection -->
      <section class="home-section">
        <div class="section-label">出题模式</div>
        <div class="mode-grid">
          <button
            class="mode-btn"
            :class="{ selected: mode === 'sequential' }"
            @click="mode = 'sequential'"
          >
            <span class="mode-icon">→</span>
            <span class="mode-name">顺序出题</span>
            <span class="mode-desc">按文件原顺序</span>
          </button>
          <button
            class="mode-btn"
            :class="{ selected: mode === 'random' }"
            @click="mode = 'random'"
          >
            <span class="mode-icon">⇄</span>
            <span class="mode-name">随机打乱</span>
            <span class="mode-desc">乱序考验记忆</span>
          </button>
        </div>
      </section>

      <!-- Actions -->
      <section class="home-actions">
        <button
          class="btn-start"
          :disabled="selectedCategories.length === 0"
          @click="startPractice"
        >
          开始练习
          <span v-if="selectedCategories.length > 0" class="btn-start-count">
            {{ totalSelected }} 题
          </span>
        </button>

        <div class="quick-links">
          <button class="btn-quick" @click="startReview">
            🔁 待复习
          </button>
          <button class="btn-quick" @click="$router.push('/stats')">
            📊 答题历史
          </button>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories } from '../api/index.js'

const PROGRESS_KEY = 'interview_v1_progress'
const router = useRouter()

const categories = ref([])
const loading = ref(true)
const error = ref('')
const selectedCategories = ref([])
const mode = ref('sequential')
const savedProgress = ref(null)

const totalSelected = computed(() => {
  return categories.value
    .filter(c => selectedCategories.value.includes(c.name))
    .reduce((sum, c) => sum + c.count, 0)
})

onMounted(async () => {
  // Check saved progress
  try {
    const saved = localStorage.getItem(PROGRESS_KEY)
    if (saved) {
      const p = JSON.parse(saved)
      // Only show if not at the very last question
      if (p.questionIds?.length && p.currentIndex < p.questionIds.length - 1) {
        savedProgress.value = p
      }
    }
  } catch {}

  // Load categories
  try {
    const res = await getCategories()
    categories.value = res.data
    selectedCategories.value = res.data.map(c => c.name)
  } catch (e) {
    error.value = '无法连接后端，请确认后端已启动（localhost:8081）'
  } finally {
    loading.value = false
  }
})

function toggleCategory(name) {
  const idx = selectedCategories.value.indexOf(name)
  if (idx >= 0) {
    selectedCategories.value.splice(idx, 1)
  } else {
    selectedCategories.value.push(name)
  }
}

function selectAll() {
  selectedCategories.value = categories.value.map(c => c.name)
}

function clearAll() {
  selectedCategories.value = []
}

function startPractice() {
  if (selectedCategories.value.length === 0) return
  router.push({
    path: '/answer',
    query: {
      categories: selectedCategories.value.join(','),
      mode: mode.value,
    },
  })
}

function startReview() {
  router.push({ path: '/answer', query: { mode: 'review' } })
}

function resumePractice() {
  const p = savedProgress.value
  if (!p) return
  router.push({
    path: '/answer',
    query: {
      categories: p.categories,
      mode: p.mode,
      resume: '1',
    },
  })
}

function clearSavedProgress() {
  localStorage.removeItem(PROGRESS_KEY)
  savedProgress.value = null
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 64px 24px 80px;
}

.home-inner {
  width: 100%;
  max-width: 820px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

/* Header */
.home-header { display: flex; flex-direction: column; gap: 10px; }
.home-eyebrow {
  font-family: var(--font-mono);
  font-size: 12px;
  letter-spacing: .14em;
  text-transform: uppercase;
  color: var(--accent);
}
.home-title {
  font-size: 42px;
  font-weight: 700;
  letter-spacing: -.03em;
  color: var(--text-1);
  line-height: 1.1;
}
.home-sub { font-size: 15px; color: var(--text-3); }

/* Resume banner */
.resume-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  background: var(--accent-dim);
  border: 1px solid rgba(99,102,241,.3);
  border-radius: var(--r-lg);
  padding: 14px 18px;
}
.resume-info { display: flex; align-items: center; gap: 10px; }
.resume-icon { font-size: 16px; }
.resume-text { font-size: 14px; color: var(--text-2); }
.resume-text strong { color: var(--accent-hover); }
.resume-actions { display: flex; align-items: center; gap: 12px; }
.btn-resume {
  padding: 7px 16px; border-radius: var(--r-md);
  border: none; background: var(--accent); color: #fff;
  font-size: 13px; font-weight: 600; cursor: pointer;
  transition: background .15s; font-family: var(--font-sans);
}
.btn-resume:hover { background: var(--accent-hover); }

/* Sections */
.home-section { display: flex; flex-direction: column; gap: 16px; }

.section-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-3);
  text-transform: uppercase;
  letter-spacing: .08em;
}
.section-hint { font-weight: 400; text-transform: none; letter-spacing: 0; }

/* Tags */
.tag-grid { display: flex; flex-wrap: wrap; gap: 10px; }

.tag {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 8px 16px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 500;
  background: var(--surface);
  color: var(--text-2);
  border: 1px solid var(--border);
  cursor: pointer;
  transition: all .15s var(--ease);
}
.tag:hover { border-color: var(--accent); color: var(--text-1); }
.tag.selected {
  background: var(--accent-dim);
  color: var(--accent-hover);
  border-color: rgba(99,102,241,.4);
}
.tag-count {
  font-family: var(--font-mono);
  font-size: 11px;
  color: inherit;
  opacity: .7;
}

.tag-actions { display: flex; gap: 12px; }
.text-btn {
  font-size: 13px;
  color: var(--text-3);
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  transition: color .15s;
}
.text-btn:hover { color: var(--text-2); }

/* Mode */
.mode-grid { display: flex; gap: 12px; }
.mode-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 20px 16px;
  border-radius: var(--r-lg);
  border: 1px solid var(--border);
  background: var(--surface);
  color: var(--text-2);
  cursor: pointer;
  transition: all .15s var(--ease);
}
.mode-btn:hover { border-color: var(--text-3); color: var(--text-1); }
.mode-btn.selected {
  border-color: rgba(99,102,241,.5);
  background: var(--accent-dim);
  color: var(--accent-hover);
}
.mode-icon { font-size: 22px; line-height: 1; }
.mode-name { font-size: 15px; font-weight: 600; }
.mode-desc { font-size: 12px; opacity: .6; }

/* Actions */
.home-actions { display: flex; flex-direction: column; gap: 12px; }

.btn-start {
  width: 100%;
  padding: 16px;
  border-radius: var(--r-md);
  border: none;
  background: var(--accent);
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: all .15s var(--ease);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}
.btn-start:hover:not(:disabled) {
  background: var(--accent-hover);
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(99,102,241,.35);
}
.btn-start:disabled {
  opacity: .35;
  cursor: not-allowed;
}
.btn-start-count {
  font-family: var(--font-mono);
  font-size: 13px;
  opacity: .8;
  background: rgba(255,255,255,.15);
  padding: 2px 9px;
  border-radius: 999px;
}

.quick-links { display: flex; gap: 10px; }
.btn-quick {
  flex: 1;
  padding: 12px;
  border-radius: var(--r-md);
  border: 1px solid var(--border);
  background: var(--surface);
  color: var(--text-3);
  font-size: 14px;
  cursor: pointer;
  transition: all .15s var(--ease);
}
.btn-quick:hover { color: var(--text-2); border-color: var(--text-3); background: var(--surface-raised); }

/* States */
.loading-text { font-size: 14px; color: var(--text-3); padding: 8px 0; }
.error-text {
  font-size: 14px;
  color: var(--yellow);
  background: var(--yellow-dim);
  border: 1px solid rgba(245,158,11,.25);
  padding: 10px 14px;
  border-radius: var(--r-md);
}
</style>
