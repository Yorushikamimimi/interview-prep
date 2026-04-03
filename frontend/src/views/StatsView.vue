<template>
  <div class="stats-view">
    <div class="stats-inner">

      <!-- Header -->
      <div class="stats-header">
        <button class="btn-back" @click="$router.push('/')">←</button>
        <div>
          <div class="page-eyebrow">答题统计</div>
          <h1 class="page-title">掌握情况</h1>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="state-text">加载中...</div>
      <div v-else-if="error" class="state-error">{{ error }}</div>

      <template v-else>
        <!-- Empty state -->
        <div v-if="history.length === 0" class="empty-state">
          <div class="empty-icon">📋</div>
          <p class="empty-text">还没有答题记录</p>
          <button class="btn-primary" @click="$router.push('/')">开始练习</button>
        </div>

        <template v-else>
          <!-- Overview row -->
          <div class="overview-row">

            <!-- Ring chart -->
            <div class="ring-card">
              <svg viewBox="0 0 100 100" class="ring-svg">
                <circle class="ring-track" cx="50" cy="50" r="38"/>
                <circle
                  class="ring-fill"
                  cx="50" cy="50" r="38"
                  :style="{ strokeDashoffset: ringOffset }"
                />
                <text x="50" y="46" class="svg-pct" text-anchor="middle" dominant-baseline="middle">
                  {{ masteredPct }}%
                </text>
                <text x="50" y="60" class="svg-label" text-anchor="middle">已掌握</text>
              </svg>
              <div class="ring-caption">
                共答 {{ totalAnswered }} 题 · 掌握 {{ totalMastered }}
              </div>
            </div>

            <!-- Category bars -->
            <div class="cat-card">
              <div class="card-label">分类掌握率</div>
              <div v-if="stats.length === 0" class="state-text" style="font-size:13px">暂无数据</div>
              <div v-else class="cat-list">
                <div v-for="s in stats" :key="s.category" class="cat-item">
                  <div class="cat-header">
                    <span class="cat-name">{{ s.category }}</span>
                    <span class="cat-pct" :style="{ color: pctColor(s.mastered / s.total) }">
                      {{ Math.round(s.mastered / s.total * 100) }}%
                    </span>
                  </div>
                  <!-- Stacked bar -->
                  <div class="bar-track">
                    <div
                      class="bar-seg bar-mastered"
                      :style="{ width: (s.mastered / s.total * 100) + '%' }"
                    ></div>
                    <div
                      class="bar-seg bar-unfamiliar"
                      :style="{ width: (s.unfamiliar / s.total * 100) + '%' }"
                    ></div>
                    <div
                      class="bar-seg bar-review"
                      :style="{ width: (s.toReview / s.total * 100) + '%' }"
                    ></div>
                  </div>
                  <div class="cat-counts">
                    <span class="count-green">✓ {{ s.mastered }}</span>
                    <span class="count-yellow">⚠ {{ s.unfamiliar }}</span>
                    <span class="count-blue">↺ {{ s.toReview }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- History list -->
          <div class="history-section">
            <div class="card-label">答题历史</div>
            <div class="history-table">
              <div class="history-thead">
                <span>题目</span>
                <span>分类</span>
                <span>标记</span>
                <span>时间</span>
              </div>
              <div
                v-for="rec in history"
                :key="rec.id"
                class="history-row"
                @click="toggleExpand(rec.id)"
              >
                <div class="history-row-main">
                  <span class="h-title">{{ rec.questionTitle }}</span>
                  <span class="h-cat">{{ rec.category }}</span>
                  <span class="h-mark">
                    <span class="badge" :class="markClass(rec.mark)">{{ markLabel(rec.mark) }}</span>
                  </span>
                  <span class="h-time">{{ formatTime(rec.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>

        </template>
      </template>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStats, getHistory } from '../api/index.js'

const stats   = ref([])
const history = ref([])
const loading = ref(true)
const error   = ref('')
const expanded = ref(null)

onMounted(async () => {
  try {
    const [statsRes, histRes] = await Promise.all([getStats(), getHistory()])
    stats.value   = statsRes.data
    history.value = histRes.data
  } catch (e) {
    error.value = '加载失败，请确认后端已启动'
  } finally {
    loading.value = false
  }
})

// ── Ring chart ──
const circumference = 2 * Math.PI * 38  // r=38

const totalAnswered = computed(() =>
  stats.value.reduce((s, c) => s + c.total, 0)
)
const totalMastered = computed(() =>
  stats.value.reduce((s, c) => s + c.mastered, 0)
)
const masteredPct = computed(() =>
  totalAnswered.value ? Math.round(totalMastered.value / totalAnswered.value * 100) : 0
)
const ringOffset = computed(() =>
  circumference - (masteredPct.value / 100) * circumference
)

// ── Helpers ──
function pctColor(ratio) {
  if (ratio >= 0.7) return 'var(--green)'
  if (ratio >= 0.4) return 'var(--yellow)'
  return 'var(--text-3)'
}

function markLabel(mark) {
  return { MASTERED: '已掌握', UNFAMILIAR: '不熟', TO_REVIEW: '待复习' }[mark] ?? mark
}
function markClass(mark) {
  return { MASTERED: 'badge-green', UNFAMILIAR: 'badge-yellow', TO_REVIEW: 'badge-blue' }[mark] ?? ''
}

function formatTime(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  const MM = String(d.getMonth() + 1).padStart(2, '0')
  const DD = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${MM}-${DD} ${hh}:${mm}`
}

function toggleExpand(id) {
  expanded.value = expanded.value === id ? null : id
}
</script>

<style scoped>
.stats-view {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  padding: 0 24px 80px;
}
.stats-inner {
  width: 100%;
  max-width: 860px;
  display: flex;
  flex-direction: column;
  gap: 28px;
  padding-top: 28px;
}

/* Header */
.stats-header { display: flex; align-items: center; gap: 16px; }
.btn-back {
  background: none; border: none; color: var(--text-3);
  font-size: 18px; padding: 4px 8px; cursor: pointer;
  transition: color .15s; flex-shrink: 0;
}
.btn-back:hover { color: var(--text-1); }
.page-eyebrow {
  font-family: var(--font-mono); font-size: 10px;
  letter-spacing: .14em; text-transform: uppercase;
  color: var(--accent); margin-bottom: 4px;
}
.page-title { font-size: 24px; font-weight: 700; letter-spacing: -.02em; }

/* States */
.state-text  { font-size: 13px; color: var(--text-3); padding: 8px 0; }
.state-error {
  font-size: 13px; color: var(--yellow);
  background: var(--yellow-dim);
  border: 1px solid rgba(245,158,11,.25);
  padding: 12px 16px; border-radius: var(--r-md);
}
.empty-state {
  display: flex; flex-direction: column;
  align-items: center; gap: 14px; padding: 64px 0;
}
.empty-icon { font-size: 40px; opacity: .4; }
.empty-text { color: var(--text-3); font-size: 14px; }

/* Overview row */
.overview-row {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 16px;
  align-items: start;
}

/* Ring card */
.ring-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--r-lg);
  padding: 24px 20px;
  display: flex; flex-direction: column;
  align-items: center; gap: 12px;
}
.ring-svg { width: 120px; height: 120px; }
.ring-track { fill: none; stroke: var(--border); stroke-width: 9; }
.ring-fill {
  fill: none; stroke: var(--accent); stroke-width: 9;
  stroke-linecap: round;
  stroke-dasharray: v-bind(circumference);
  transform: rotate(-90deg); transform-origin: 50% 50%;
  transition: stroke-dashoffset .8s cubic-bezier(.4,0,.2,1);
}
.svg-pct {
  font-family: var(--font-mono); font-size: 18px;
  font-weight: 600; fill: var(--text-1);
}
.svg-label { font-size: 9px; fill: var(--text-3); font-family: sans-serif; }
.ring-caption { font-size: 11px; color: var(--text-3); text-align: center; }

/* Category card */
.cat-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--r-lg);
  padding: 20px 22px;
  display: flex; flex-direction: column; gap: 16px;
}
.card-label {
  font-size: 12px; font-weight: 600; color: var(--text-3);
  text-transform: uppercase; letter-spacing: .08em;
}
.cat-list { display: flex; flex-direction: column; gap: 14px; }
.cat-item { display: flex; flex-direction: column; gap: 5px; }
.cat-header {
  display: flex; justify-content: space-between;
  align-items: center;
}
.cat-name { font-size: 13px; font-weight: 500; color: var(--text-2); }
.cat-pct  { font-family: var(--font-mono); font-size: 12px; font-weight: 600; }

.bar-track {
  height: 4px; background: var(--border);
  border-radius: 99px; overflow: hidden;
  display: flex;
}
.bar-seg { height: 100%; transition: width .6s var(--ease); }
.bar-mastered  { background: var(--green); }
.bar-unfamiliar { background: var(--yellow); }
.bar-review    { background: var(--blue); }

.cat-counts {
  display: flex; gap: 12px;
}
.cat-counts span { font-size: 11px; font-family: var(--font-mono); }
.count-green  { color: var(--green); }
.count-yellow { color: var(--yellow); }
.count-blue   { color: var(--blue); }

/* History */
.history-section { display: flex; flex-direction: column; gap: 12px; }
.history-table {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--r-lg);
  overflow: hidden;
}
.history-thead {
  display: grid;
  grid-template-columns: 1fr 80px 80px 90px;
  padding: 10px 16px;
  border-bottom: 1px solid var(--border);
  font-size: 11px; font-weight: 600;
  color: var(--text-3); text-transform: uppercase;
  letter-spacing: .08em; font-family: var(--font-mono);
}
.history-row { border-bottom: 1px solid var(--border-subtle); cursor: pointer; }
.history-row:last-child { border-bottom: none; }
.history-row:hover .history-row-main { background: var(--surface-raised); }
.history-row-main {
  display: grid;
  grid-template-columns: 1fr 80px 80px 90px;
  align-items: center;
  padding: 11px 16px;
  transition: background .1s;
}
.h-title { font-size: 13px; color: var(--text-1); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; padding-right: 12px; }
.h-cat   { font-size: 12px; color: var(--text-3); }
.h-time  { font-family: var(--font-mono); font-size: 11px; color: var(--text-3); }

/* Badges */
.badge {
  display: inline-flex; align-items: center;
  padding: 2px 8px; border-radius: 999px;
  font-size: 11px; font-weight: 600;
}
.badge-green  { background: var(--green-dim);  color: var(--green);  }
.badge-yellow { background: var(--yellow-dim); color: var(--yellow); }
.badge-blue   { background: var(--blue-dim);   color: var(--blue);   }

/* Button */
.btn-primary {
  padding: 10px 22px; border-radius: var(--r-md); border: none;
  background: var(--accent); color: #fff; font-size: 14px;
  font-weight: 600; cursor: pointer; font-family: var(--font-sans);
  transition: background .15s;
}
.btn-primary:hover { background: var(--accent-hover); }
</style>
