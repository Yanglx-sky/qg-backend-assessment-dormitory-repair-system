<template>
  <div class="repairman-home">
    <h2>维修人员控制台</h2>
    <div class="welcome-container">
      <h1 class="welcome-title">
        <span class="welcome-text">欢迎，</span>
        <span class="user-name">{{ currentUserName }}</span>
      </h1>
    </div>
    <div class="dashboard-cards">
      <div class="dashboard-card">
        <h3>待接取订单</h3>
        <p>查看并接取新的报修订单</p>
        <router-link to="/repairman/accept">接取订单</router-link>
      </div>
      <div class="dashboard-card">
        <h3>我的订单</h3>
        <p>查看并处理已接取的订单</p>
        <router-link to="/repairman/orders">我的订单</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const currentUserName = ref('维修人员')

const getCurrentUser = () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (user) {
    currentUserName.value = user.userName || '维修人员'
  }
}

onMounted(() => {
  getCurrentUser()
})
</script>

<style scoped>
.repairman-home {
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

.dashboard-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.dashboard-card {
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  text-align: center;
}

.dashboard-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.dashboard-card h3 {
  font-size: 18px;
  margin-bottom: 10px;
  font-weight: 600;
}

.dashboard-card p {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 20px;
}

.dashboard-card a {
  display: inline-block;
  background-color: white;
  color: #4682B4;
  padding: 10px 20px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.dashboard-card a:hover {
  background-color: #f5f5f5;
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .dashboard-cards {
    grid-template-columns: 1fr;
  }
}
</style>