<template>
  <div class="register">
    <div class="register-container">
      <h2>用户注册</h2>
      <div class="message" :class="{ 'success': messageType === 'success', 'error': messageType === 'error' }" v-if="message">
        {{ message }}
      </div>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="account">账号</label>
          <input type="text" id="account" v-model="registerForm.account" required>
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="registerForm.password" required>
        </div>
        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input type="password" id="confirmPassword" v-model="registerForm.confirmPassword" required>
        </div>
        <div class="form-group">
          <label for="userName">姓名</label>
          <input type="text" id="userName" v-model="registerForm.userName" required>
        </div>
        <button type="submit" class="btn">注册</button>
      </form>
      <div class="login-link">
        已有账号？<router-link to="/login">立即登录</router-link>
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

const registerForm = ref({
  account: '',
  password: '',
  confirmPassword: '',
  userName: ''
})

const message = ref('')
const messageType = ref('')

const handleRegister = async () => {
  // 验证密码
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    message.value = '两次输入的密码不一致'
    messageType.value = 'error'
    return
  }

  try {
    const result = await userStore.register(registerForm.value)
    if (result.success) {
      message.value = '注册成功！'
      messageType.value = 'success'
      setTimeout(() => {
        router.push('/login')
      }, 1000)
    } else {
      message.value = result.message || '注册失败'
      messageType.value = 'error'
    }
  } catch (error) {
    message.value = '注册失败，请稍后重试'
    messageType.value = 'error'
  }
}
</script>

<style scoped>
.register {
  min-height: 100vh;
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-container {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.register-container h2 {
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

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
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

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.login-link a {
  color: #4682B4;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
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