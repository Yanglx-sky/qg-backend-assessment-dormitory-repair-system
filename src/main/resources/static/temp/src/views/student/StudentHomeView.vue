<template>
  <div class="student-home">
    <h2>我的宿舍</h2>
    <div id="user-info" class="welcome-container">
      <h1 class="welcome-title">
        <span class="welcome-text">欢迎，</span>
        <span class="user-name">{{ currentUserName }}</span>
      </h1>
    </div>
    <div id="dorm-info">
      <div class="dorm-info" v-if="dormInfo">
        <h3>已绑定宿舍</h3>
        <p><strong>楼栋：</strong>{{ dormInfo.building }}</p>
        <p><strong>房间号：</strong>{{ dormInfo.roomNum }}</p>
        <router-link to="/student/dorm" class="btn">修改宿舍</router-link>
      </div>
      <div class="dorm-info" v-else-if="!loading">
        <h3>未绑定宿舍</h3>
        <p>您还没有绑定宿舍，请先绑定宿舍后再提交报修。</p>
        <router-link to="/student/dorm" class="btn">绑定宿舍</router-link>
      </div>
      <div class="dorm-info" v-if="loading">
        <h3>加载中...</h3>
        <p>正在获取宿舍信息，请稍候。</p>
      </div>
      <div class="dorm-info" v-if="error">
        <h3>获取宿舍信息失败</h3>
        <p>无法获取宿舍信息，请稍后重试。</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { axiosInstance } from '../../stores/user'

const currentUserName = ref('学生')
const dormInfo = ref(null)
const loading = ref(true)
const error = ref(false)

const getBoundDormitory = async () => {
  try {
    loading.value = true
    error.value = false
    const user = JSON.parse(localStorage.getItem('user'))
    if (user) {
      currentUserName.value = user.userName || '学生'
      const response = await axiosInstance.get(`/dormitories?userId=${user.userId}`)
      if (response.success && response.data) {
        dormInfo.value = response.data
      }
    }
  } catch (err) {
    console.error('获取宿舍信息失败:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getBoundDormitory()
})
</script>

<style scoped>
.student-home {
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.welcome-container {
  margin-bottom: 30px;
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.1) 0%, rgba(0, 242, 254, 0.1) 100%);
  border-radius: 12px;
  border: 1px solid rgba(79, 172, 254, 0.2);
  box-shadow: 0 4px 20px rgba(79, 172, 254, 0.1);
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  color: #333;
  animation: welcomeGlow 2s ease-in-out infinite alternate;
}

@keyframes welcomeGlow {
  from {
    text-shadow: 0 0 10px rgba(79, 172, 254, 0.5), 0 0 20px rgba(79, 172, 254, 0.3);
  }
  to {
    text-shadow: 0 0 20px rgba(79, 172, 254, 0.8), 0 0 30px rgba(79, 172, 254, 0.5);
  }
}

.welcome-text {
  display: inline-block;
  animation: slideInLeft 0.8s ease-out;
}

.user-name {
  display: inline-block;
  animation: slideInRight 0.8s ease-out 0.2s both;
  font-weight: 800;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

h2 {
  color: #333;
  margin-bottom: 25px;
  font-size: 20px;
  font-weight: 600;
  border-bottom: 2px solid #4facfe;
  padding-bottom: 10px;
}

.dorm-info {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  border-left: 4px solid #4facfe;
}

.dorm-info h3 {
  color: #333;
  margin-bottom: 10px;
  font-size: 18px;
}

.dorm-info p {
  color: #666;
  margin: 5px 0;
}

.btn {
  display: inline-block;
  padding: 8px 16px;
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  margin-top: 10px;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(135, 206, 235, 0.3);
}
</style>