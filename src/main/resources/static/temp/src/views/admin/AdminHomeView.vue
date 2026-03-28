<template>
  <div class="admin-home">
    <h2>管理员控制台</h2>
    <div class="dashboard-cards">
      <div class="dashboard-card">
        <h3>所有订单</h3>
        <p>管理所有报修订单</p>
        <router-link to="/admin/orders">查看订单</router-link>
      </div>
      <div class="dashboard-card">
        <h3>所有学生</h3>
        <p>管理学生用户</p>
        <router-link to="/admin/students">查看学生</router-link>
      </div>
      <div class="dashboard-card">
        <h3>所有维修人员</h3>
        <p>管理维修人员用户</p>
        <router-link to="/admin/repairmen">查看维修人员</router-link>
      </div>
      <div class="dashboard-card">
        <h3>所有宿舍</h3>
        <p>管理宿舍信息</p>
        <router-link to="/admin/dormitories">查看宿舍</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
// AdminHomeView.vue - 管理员首页
</script>

<style scoped>
.admin-home {
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