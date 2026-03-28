<template>
  <div class="student-index">
    <div class="header">
      <h1>宿舍报修系统 - 学生中心</h1>
    </div>
    <div class="nav">
      <router-link to="/student" class="nav-item">首页</router-link>
      <router-link to="/student/dorm" class="nav-item">我的宿舍</router-link>
      <router-link to="/student/orders" class="nav-item">我的订单</router-link>
      <router-link to="/student/submit" class="nav-item">提交报修</router-link>
      <a href="javascript:void(0);" class="nav-item logout" @click="handleLogout">退出登录</a>
    </div>
    <div class="container">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.student-index {
  min-height: 100vh;
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
}

.header {
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
  color: white;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 24px;
  font-weight: 600;
}

.nav {
  background-color: white;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
}

.nav-item {
  flex: 1;
  display: block;
  color: #333;
  text-align: center;
  padding: 14px 16px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-item:hover {
  background-color: #f5f5f5;
  color: #4facfe;
}

.logout {
  background-color: #f8f9fa;
  color: #dc3545;
}

.logout:hover {
  background-color: #e9ecef;
  color: #c82333;
}

.container {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .nav {
    flex-direction: column;
  }
  
  .nav-item {
    text-align: left;
  }
  
  .container {
    padding: 20px;
  }
}
</style>