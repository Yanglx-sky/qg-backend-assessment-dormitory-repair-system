<template>
  <div class="login">
    <div class="login-container">
      <h2>用户登录</h2>
      <div class="message" :class="{ 'success': messageType === 'success', 'error': messageType === 'error' }" v-if="message">
        {{ message }}
      </div>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="account">账号</label>
          <input type="text" id="account" v-model="loginForm.account" required>
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="loginForm.password" required>
        </div>
        <button type="submit" class="btn">登录</button>
      </form>
      <div class="register-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  account: '',
  password: ''
})

const message = ref('')
const messageType = ref('')

const handleLogin = async () => {
  try {
    const result = await userStore.login(loginForm.value)
    if (result.success) {
      message.value = '登录成功！'
      messageType.value = 'success'
      
      // 根据角色跳转到不同页面
      setTimeout(() => {
        const user = userStore.user
        console.log('登录成功，用户信息：', user)
        if (user) {
          console.log('用户角色ID：', user.roleId)
          switch (user.roleId) {
            case 1:
              // 学生
              console.log('跳转到学生页面')
              router.push('/student')
              break
            case 2:
              // 管理员
              console.log('跳转到管理员页面')
              router.push('/admin')
              break
            case 3:
              // 维修人员
              console.log('跳转到维修人员页面')
              router.push('/repairman')
              break
            default:
              console.log('未知角色，跳转到首页')
              router.push('/')
          }
        } else {
          console.log('用户信息为空，跳转到首页')
          router.push('/')
        }
      }, 1000)
    } else {
      message.value = result.message || '登录失败'
      messageType.value = 'error'
    }
  } catch (error) {
    message.value = '登录失败，请稍后重试'
    messageType.value = 'error'
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-container h2 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #87CEEB;
  box-shadow: 0 0 0 3px rgba(135, 206, 235, 0.1);
}

.btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(135, 206, 235, 0.3);
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.register-link a {
  color: #4682B4;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

.message {
  padding: 12px;
  margin-bottom: 20px;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
}

.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}
</style>