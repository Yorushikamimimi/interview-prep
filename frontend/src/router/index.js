import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AnswerView from '../views/AnswerView.vue'
import StatsView from '../views/StatsView.vue'

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/answer', name: 'answer', component: AnswerView },
  { path: '/stats', name: 'stats', component: StatsView },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
