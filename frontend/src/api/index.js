import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

export const checkHealth     = ()                          => http.get('/health')
export const getCategories   = ()                          => http.get('/categories')
export const getQuestions    = (categories, mode)          => http.get('/questions', { params: { categories, mode } })
export const getQuestion     = (id)                        => http.get(`/questions/${id}`)
export const submitMark      = (questionId, mark)          => http.post('/records', { questionId, mark })
export const getHistory      = ()                          => http.get('/records/history')
export const getQuestionHistory = (id)                     => http.get(`/records/history/${id}`)
export const getLatestMark   = (id)                        => http.get(`/records/mark/${id}`)
export const getToReview     = ()                          => http.get('/records/to-review')
export const getStats        = ()                          => http.get('/stats')

export default http
