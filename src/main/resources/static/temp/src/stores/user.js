import { defineStore } from 'pinia'
import axios from 'axios'

// 创建axios实例
const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
axiosInstance.interceptors.request.use(
  config => {
    const accessToken = localStorage.getItem('accessToken')
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
axiosInstance.interceptors.response.use(
  response => response.data,
  error => {
    // 检查当前是否在登录页面
    const isLoginPage = window.location.pathname === '/login'
    if (error.response?.status === 401 && !isLoginPage) {
      return refreshAccessToken().then(newAccessToken => {
        error.config.headers.Authorization = `Bearer ${newAccessToken}`
        return axiosInstance(error.config)
      }).catch(() => {
        localStorage.removeItem('user')
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        window.location.href = '/login'
        return Promise.reject(error)
      })
    }
    return Promise.reject(error.response?.data || { success: false, message: '请求失败' })
  }
)

// 刷新access token
function refreshAccessToken() {
  const refreshToken = localStorage.getItem('refreshToken')
  if (!refreshToken) {
    return Promise.reject('No refresh token found')
  }
  return axiosInstance.post('/users/refresh-token', { refreshToken })
    .then(data => {
      if (data.success) {
        localStorage.setItem('accessToken', data.accessToken)
        return data.accessToken
      } else {
        return Promise.reject('Access token refresh failed')
      }
    })
}

export const useUserStore = defineStore('user', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null
  }),
  getters: {
    isLoggedIn: (state) => !!state.accessToken,
    userRole: (state) => state.user?.role || ''
  },
  actions: {
    async login(loginForm) {
      try {
        console.log('开始登录，表单数据：', loginForm)
        const response = await axiosInstance.post('/users/login', loginForm)
        console.log('登录响应：', response)
        if (response.success) {
          console.log('登录成功，用户信息：', response.user)
          this.user = response.user
          this.accessToken = response.accessToken
          this.refreshToken = response.refreshToken
          localStorage.setItem('user', JSON.stringify(response.user))
          localStorage.setItem('accessToken', response.accessToken)
          localStorage.setItem('refreshToken', response.refreshToken)
          console.log('用户信息已存储，userStore.user：', this.user)
        }
        return response
      } catch (error) {
        console.error('登录错误：', error)
        return { success: false, message: error.message || '登录失败' }
      }
    },
    async register(registerForm) {
      try {
        const registerData = {
          ...registerForm,
          roleId: 1 // 默认注册为学生角色
        }
        // 移除不需要的字段
        delete registerData.confirmPassword
        const response = await axiosInstance.post('/users', registerData)
        return response
      } catch (error) {
        return { success: false, message: error.message || '注册失败' }
      }
    },
    logout() {
      this.user = null
      this.accessToken = null
      this.refreshToken = null
      localStorage.removeItem('user')
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
    }
  }
})

// 导出axios实例
export { axiosInstance }