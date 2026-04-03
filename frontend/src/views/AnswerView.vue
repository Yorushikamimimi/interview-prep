<template>
  <div class="answer-view">

    <!-- Loading -->
    <div v-if="loading" class="center-state">
      <span class="state-text">加载题目中...</span>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="center-state">
      <span class="state-error">{{ error }}</span>
      <button class="btn-ghost" @click="$router.push('/')">← 返回首页</button>
    </div>

    <!-- Empty -->
    <div v-else-if="questions.length === 0" class="center-state">
      <span class="state-text">没有找到题目</span>
      <button class="btn-ghost" @click="$router.push('/')">← 返回首页</button>
    </div>

    <!-- Finished -->
    <div v-else-if="finished" class="center-state">
      <div class="finish-icon">✓</div>
      <h2 class="finish-title">练习完成</h2>
      <p class="finish-sub">本次共 {{ questions.length }} 题</p>
      <div class="finish-actions">
        <button class="btn-primary" @click="$router.push('/')">返回首页</button>
        <button class="btn-ghost" @click="$router.push('/stats')">查看统计</button>
      </div>
    </div>

    <!-- Quiz -->
    <div v-else class="quiz-wrap">

      <!-- Top bar -->
      <div class="topbar">
        <div class="topbar-left">
          <button class="btn-back" @click="$router.push('/')">←</button>
          <span class="badge-category">{{ currentQuestion.category }}</span>
          <span class="q-counter">
            Q{{ String(currentIndex + 1).padStart(2, '0') }} / {{ questions.length }}
          </span>
        </div>
        <div class="topbar-right">
          <button class="btn-restart" title="重新开始" @click="restartConfirm">↺ 重新开始</button>
          <div class="progress-wrap">
            <div class="progress-track">
              <div class="progress-fill" :style="{ width: progressPct + '%' }"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Question card -->
      <div class="question-card">
        <h2 class="question-title">{{ currentQuestion.title }}</h2>
        <!-- Algorithm question content (problem + examples) -->
        <div
          v-if="currentQuestion.questionContent"
          class="question-content markdown-body"
          v-html="renderedQuestionContent"
        ></div>
        <div class="question-source">
          <span class="source-path">{{ currentQuestion.sourceFile }}</span>
        </div>
      </div>

      <!-- Answer textarea -->
      <textarea
        v-model="answerText"
        class="answer-area"
        placeholder="写下你的思路和答案..."
        :disabled="isRevealed"
      ></textarea>

      <!-- Reveal button -->
      <div class="reveal-row">
        <button class="btn-reveal" :class="{ revealed: isRevealed }" @click="reveal">
          {{ isRevealed ? '收起答案' : '查看参考答案' }}
        </button>
      </div>

      <!-- Mark buttons (appear right after reveal, ABOVE the answer) -->
      <Transition name="slide-down">
        <div v-if="isRevealed" class="mark-section">
          <div class="mark-label">我的掌握程度</div>
          <div class="mark-row">
            <button
              class="mark-btn mastered"
              :class="{ active: selectedMark === 'MASTERED' }"
              @click="submitMark('MASTERED')"
            >✓ 已掌握</button>
            <button
              class="mark-btn unfamiliar"
              :class="{ active: selectedMark === 'UNFAMILIAR' }"
              @click="submitMark('UNFAMILIAR')"
            >⚠ 不熟</button>
            <button
              class="mark-btn review"
              :class="{ active: selectedMark === 'TO_REVIEW' }"
              @click="submitMark('TO_REVIEW')"
            >↺ 待复习</button>
          </div>
        </div>
      </Transition>

      <!-- Next button (appears after mark selected, still ABOVE the answer) -->
      <Transition name="slide-down">
        <div v-if="selectedMark !== null" class="next-row">
          <button class="btn-next" @click="next">
            {{ isLast ? '完成练习 ✓' : '下一题 →' }}
          </button>
        </div>
      </Transition>

      <!-- Answer panel (animated reveal, below marks) -->
      <div class="answer-panel-wrap" :class="{ open: isRevealed }">
        <div class="answer-panel-inner">
          <div class="answer-panel">
            <div class="answer-panel-label">参考答案</div>
            <!-- eslint-disable-next-line vue/no-v-html -->
            <div class="answer-content markdown-body" v-html="renderedAnswer"></div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { marked } from 'marked'
import hljs from 'highlight.js'
import { getQuestions, getToReview, submitMark as apiSubmitMark } from '../api/index.js'

const route  = useRoute()
const router = useRouter()

const PROGRESS_KEY = 'interview_v1_progress'

// ── State ──
const questions    = ref([])
const currentIndex = ref(0)
const loading      = ref(true)
const error        = ref('')
const finished     = ref(false)
const isRevealed   = ref(false)
const selectedMark = ref(null)
const answerText   = ref('')

// ── Configure marked with highlight.js ──
const renderer = new marked.Renderer()
renderer.code = function ({ text, lang }) {
  const language = lang && hljs.getLanguage(lang) ? lang : 'plaintext'
  const highlighted = hljs.highlight(text, { language }).value
  return `<pre class="code-block"><code class="hljs language-${language}">${highlighted}</code></pre>`
}
marked.use({ renderer })

// ── Computed ──
const currentQuestion = computed(() => questions.value[currentIndex.value])
const isLast          = computed(() => currentIndex.value === questions.value.length - 1)
const progressPct     = computed(() =>
  questions.value.length ? ((currentIndex.value + 1) / questions.value.length) * 100 : 0
)
const renderedAnswer = computed(() => {
  if (!currentQuestion.value?.answer) return ''
  return marked.parse(currentQuestion.value.answer)
})
const renderedQuestionContent = computed(() => {
  if (!currentQuestion.value?.questionContent) return ''
  return marked.parse(currentQuestion.value.questionContent)
})

// ── Progress persistence ──
function saveProgress() {
  const state = {
    questionIds: questions.value.map(q => q.id),
    currentIndex: currentIndex.value,
    categories: route.query.categories || '',
    mode: route.query.mode || 'sequential',
    savedAt: Date.now()
  }
  localStorage.setItem(PROGRESS_KEY, JSON.stringify(state))
}

function clearProgress() {
  localStorage.removeItem(PROGRESS_KEY)
}

// ── Load questions ──
onMounted(async () => {
  try {
    const mode = route.query.mode
    const resume = route.query.resume === '1'

    if (resume) {
      const saved = localStorage.getItem(PROGRESS_KEY)
      if (saved) {
        const p = JSON.parse(saved)
        const cats = p.categories ? p.categories.split(',') : null
        const res = await getQuestions(cats, p.mode || 'sequential')
        // Reorder to match saved order
        const idOrder = new Map(p.questionIds.map((id, i) => [id, i]))
        const sorted = [...res.data].sort(
          (a, b) => (idOrder.has(a.id) ? idOrder.get(a.id) : 999) - (idOrder.has(b.id) ? idOrder.get(b.id) : 999)
        )
        questions.value = sorted
        currentIndex.value = Math.min(p.currentIndex, sorted.length - 1)
      } else {
        // No saved state, fallback to normal load
        const res = await getQuestions(null, 'sequential')
        questions.value = res.data
      }
    } else if (mode === 'review') {
      const res = await getToReview()
      questions.value = res.data
    } else {
      const cats = route.query.categories
        ? route.query.categories.split(',')
        : null
      const res = await getQuestions(cats, mode || 'sequential')
      questions.value = res.data
    }
  } catch (e) {
    error.value = '加载失败，请确认后端已启动（localhost:8081）'
  } finally {
    loading.value = false
  }
})

// Reset state when question changes
watch(currentIndex, () => {
  isRevealed.value   = false
  selectedMark.value = null
  answerText.value   = ''
})

// ── Actions ──
function reveal() {
  isRevealed.value = !isRevealed.value
}

async function submitMark(mark) {
  selectedMark.value = mark
  try {
    await apiSubmitMark(currentQuestion.value.id, mark)
  } catch (e) {
    console.error('Failed to save mark', e)
  }
}

function next() {
  if (!selectedMark.value) return
  if (isLast.value) {
    clearProgress()
    finished.value = true
  } else {
    currentIndex.value++
    saveProgress()
    // Scroll back to top for next question
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

function restartConfirm() {
  if (confirm('重新开始将从第 1 题开始，确认吗？')) {
    clearProgress()
    currentIndex.value = 0
    isRevealed.value = false
    selectedMark.value = null
    answerText.value = ''
  }
}
</script>

<style scoped>
.answer-view {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  padding: 0 24px 80px;
}

/* ── Center states (loading / error / finish) ── */
.center-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  min-height: 100vh;
  text-align: center;
}
.state-text  { font-size: 14px; color: var(--text-3); }
.state-error {
  font-size: 13px; color: var(--yellow);
  background: var(--yellow-dim);
  border: 1px solid rgba(245,158,11,.25);
  padding: 12px 18px; border-radius: var(--r-md);
}
.finish-icon {
  width: 56px; height: 56px; border-radius: 50%;
  background: var(--green-dim); color: var(--green);
  display: flex; align-items: center; justify-content: center;
  font-size: 24px; border: 1px solid rgba(34,197,94,.3);
}
.finish-title { font-size: 24px; font-weight: 700; letter-spacing: -.02em; }
.finish-sub   { font-size: 14px; color: var(--text-3); }
.finish-actions { display: flex; gap: 10px; }

/* ── Quiz layout ── */
.quiz-wrap {
  width: 100%;
  max-width: 760px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-top: 24px;
}

/* Top bar */
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 4px;
}
.topbar-left  { display: flex; align-items: center; gap: 10px; }
.topbar-right { display: flex; align-items: center; gap: 12px; }
.btn-back {
  background: none; border: none; color: var(--text-3);
  font-size: 18px; padding: 4px 8px; cursor: pointer;
  transition: color .15s;
}
.btn-back:hover { color: var(--text-1); }
.btn-restart {
  background: none; border: 1px solid var(--border);
  color: var(--text-3); font-size: 11px; font-weight: 500;
  padding: 4px 10px; border-radius: var(--r-md); cursor: pointer;
  transition: all .15s; white-space: nowrap;
  font-family: var(--font-sans);
}
.btn-restart:hover { color: var(--text-2); border-color: var(--text-3); }
.badge-category {
  display: inline-flex; align-items: center;
  padding: 3px 10px; border-radius: 999px;
  font-size: 11px; font-weight: 600;
  background: var(--accent-dim); color: var(--accent-hover);
}
.q-counter {
  font-family: var(--font-mono);
  font-size: 12px; color: var(--text-3);
}
.progress-wrap { width: 120px; }
.progress-track { height: 3px; background: var(--border); border-radius: 99px; overflow: hidden; }
.progress-fill  {
  height: 100%; background: var(--accent); border-radius: 99px;
  transition: width .4s var(--ease);
}

/* Question card */
.question-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--r-lg);
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.question-title {
  font-size: 18px; font-weight: 600;
  letter-spacing: -.01em; line-height: 1.4;
  color: var(--text-1);
}
.question-content {
  border-top: 1px solid var(--border-subtle);
  padding-top: 12px;
}
.question-source { }
.source-path {
  font-family: var(--font-mono); font-size: 10px; color: var(--text-3);
}

/* Answer textarea */
.answer-area {
  width: 100%; min-height: 130px; resize: vertical;
  background: var(--surface); border: 1px solid var(--border);
  border-radius: var(--r-lg); padding: 14px 16px;
  color: var(--text-1); font-family: var(--font-sans);
  font-size: 14px; line-height: 1.7; outline: none;
  transition: border-color .15s var(--ease);
  box-sizing: border-box;
}
.answer-area:focus { border-color: var(--accent); }
.answer-area::placeholder { color: var(--text-3); }
.answer-area:disabled { opacity: .6; cursor: default; }

/* Reveal button */
.reveal-row { display: flex; justify-content: center; }
.btn-reveal {
  padding: 9px 28px;
  border-radius: var(--r-md);
  border: 1px solid var(--border);
  background: var(--surface);
  color: var(--text-2); font-size: 13px; font-weight: 600;
  cursor: pointer; transition: all .18s var(--ease);
}
.btn-reveal:hover,
.btn-reveal.revealed {
  border-color: var(--accent);
  color: var(--accent-hover);
  background: var(--accent-dim);
}

/* Slide-down transition for mark section and next button */
.slide-down-enter-active { transition: opacity .25s var(--ease), transform .25s var(--ease); }
.slide-down-leave-active { transition: opacity .15s var(--ease); }
.slide-down-enter-from { opacity: 0; transform: translateY(-6px); }
.slide-down-leave-to   { opacity: 0; }

/* Mark section (appears ABOVE answer panel) */
.mark-section {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--r-lg);
  padding: 16px 20px;
  display: flex; flex-direction: column; gap: 10px;
}
.mark-label { font-size: 12px; color: var(--text-3); font-weight: 500; }
.mark-row { display: flex; gap: 10px; }
.mark-btn {
  flex: 1; padding: 11px 8px;
  border-radius: var(--r-md); font-size: 13px; font-weight: 600;
  cursor: pointer; transition: all .18s var(--ease);
  font-family: var(--font-sans);
}
.mark-btn.mastered {
  background: var(--green-dim); color: var(--green);
  border: 1px solid rgba(34,197,94,.25);
}
.mark-btn.mastered:hover,
.mark-btn.mastered.active {
  background: rgba(34,197,94,.22);
  box-shadow: 0 4px 16px rgba(34,197,94,.2);
  transform: translateY(-1px);
}
.mark-btn.unfamiliar {
  background: var(--yellow-dim); color: var(--yellow);
  border: 1px solid rgba(245,158,11,.25);
}
.mark-btn.unfamiliar:hover,
.mark-btn.unfamiliar.active {
  background: rgba(245,158,11,.22);
  box-shadow: 0 4px 16px rgba(245,158,11,.2);
  transform: translateY(-1px);
}
.mark-btn.review {
  background: var(--blue-dim); color: var(--blue);
  border: 1px solid rgba(59,130,246,.25);
}
.mark-btn.review:hover,
.mark-btn.review.active {
  background: rgba(59,130,246,.22);
  box-shadow: 0 4px 16px rgba(59,130,246,.2);
  transform: translateY(-1px);
}
.mark-btn.active { transform: scale(1.03) translateY(-1px); }

/* Next row */
.next-row {
  display: flex;
  justify-content: flex-end;
}

/* Answer panel animated reveal (grid trick — no max-height limit) */
.answer-panel-wrap {
  display: grid;
  grid-template-rows: 0fr;
  transition: grid-template-rows .35s var(--ease), opacity .3s var(--ease);
  opacity: 0;
}
.answer-panel-wrap.open {
  grid-template-rows: 1fr;
  opacity: 1;
}
.answer-panel-inner { overflow: hidden; }
.answer-panel {
  background: var(--surface);
  border: 1px solid var(--border);
  border-left: 2px solid var(--accent);
  border-radius: var(--r-lg);
  padding: 22px 24px;
  display: flex; flex-direction: column; gap: 20px;
}
.answer-panel-label {
  font-family: var(--font-mono);
  font-size: 10px; letter-spacing: .12em;
  text-transform: uppercase; color: var(--accent);
}

/* Shared buttons */
.btn-primary {
  padding: 10px 22px; border-radius: var(--r-md); border: none;
  background: var(--accent); color: #fff;
  font-size: 14px; font-weight: 600; cursor: pointer;
  transition: all .15s var(--ease); font-family: var(--font-sans);
}
.btn-primary:hover { background: var(--accent-hover); }
.btn-ghost {
  padding: 10px 22px; border-radius: var(--r-md);
  border: 1px solid var(--border); background: transparent;
  color: var(--text-2); font-size: 14px; font-weight: 600;
  cursor: pointer; transition: all .15s var(--ease); font-family: var(--font-sans);
}
.btn-ghost:hover { color: var(--text-1); border-color: var(--text-3); }
.btn-next {
  padding: 9px 22px; border-radius: var(--r-md);
  border: 1px solid var(--border); background: var(--surface);
  color: var(--text-2); font-size: 13px; font-weight: 600;
  cursor: pointer; transition: all .18s var(--ease); font-family: var(--font-sans);
}
.btn-next:not(:disabled):hover {
  border-color: var(--accent); color: var(--accent-hover);
  background: var(--accent-dim);
}
.btn-next:disabled { opacity: .3; cursor: not-allowed; }
</style>

<!-- Markdown rendered content styles (not scoped) -->
<style>
.markdown-body { font-size: 14px; color: var(--text-2); line-height: 1.75; }
.markdown-body p  { margin-bottom: 10px; }
.markdown-body p:last-child { margin-bottom: 0; }
.markdown-body strong { color: var(--text-1); font-weight: 600; }
.markdown-body ul, .markdown-body ol { padding-left: 20px; margin-bottom: 10px; }
.markdown-body li { margin-bottom: 4px; }
.markdown-body code {
  font-family: var(--font-mono); font-size: 12px;
  background: var(--surface-raised); color: var(--accent-hover);
  padding: 1px 5px; border-radius: 4px;
}
.markdown-body .code-block {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: var(--r-md);
  padding: 14px 16px;
  overflow-x: auto;
  margin: 12px 0;
}
.markdown-body .code-block code {
  background: none; padding: 0; border-radius: 0;
  font-size: 13px; line-height: 1.65; color: var(--text-2);
}
.markdown-body h3 { font-size: 15px; font-weight: 600; color: var(--text-1); margin: 14px 0 6px; }
.markdown-body blockquote {
  border-left: 2px solid var(--border); padding-left: 12px;
  color: var(--text-3); margin: 8px 0;
}
.markdown-body hr { border: none; border-top: 1px solid var(--border-subtle); margin: 16px 0; }
</style>
