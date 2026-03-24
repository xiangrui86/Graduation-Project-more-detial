import axios from 'axios'

// axios 实例：统一基础地址与超时配置
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截：自动附带本地 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  res => res.data,
  err => {
    const status = err.response && err.response.status
    if (status === 401 || status === 403) {
      // 鉴权失败时清理本地会话并跳转登录页
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      const msg = status === 403 ? '无权限或登录已失效，请重新登录' : '登录已失效，请重新登录'
      try {
        // eslint-disable-next-line no-alert
        alert(msg)
      } catch (e) {}
      window.location.href = '/#/login'
    }
    return Promise.reject(err)
  }
)

export default request
