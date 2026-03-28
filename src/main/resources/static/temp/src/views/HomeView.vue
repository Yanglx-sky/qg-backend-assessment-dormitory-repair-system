<template>
  <div class="home">
    <div class="hero">
      <h1>宿舍报修系统</h1>
      <p>便捷、高效的宿舍报修服务</p>
      <div class="buttons">
        <router-link to="/login" class="btn">登录</router-link>
        <router-link to="/register" class="btn">注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  // 检查是否已登录
  const accessToken = localStorage.getItem('accessToken')
  if (accessToken) {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user) {
      switch (user.roleId) {
        case 1:
          // 学生
          router.push('/student')
          break
        case 2:
          // 管理员
          router.push('/admin')
          break
        case 3:
          // 维修人员
          router.push('/repairman/accept')
          break
      }
    }
  }
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero {
  text-align: center;
  color: white;
  max-width: 600px;
  padding: 40px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.hero h1 {
  font-size: 36px;
  margin-bottom: 20px;
  font-weight: 600;
}

.hero p {
  font-size: 18px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.btn {
  padding: 12px 24px;
  background: white;
  color: #4682B4;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s ease;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}
</style>