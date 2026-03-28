<template>
  <div class="student-dorm">
    <h2 id="dorm-title">{{ isEditing ? '修改宿舍' : '绑定宿舍' }}</h2>
    <div class="message" :class="{ 'success': messageType === 'success', 'error': messageType === 'error' }" v-if="message">
      {{ message }}
    </div>
    <form @submit.prevent="bindDorm">
      <div class="form-group">
        <label for="building">楼栋</label>
        <input type="text" id="building" v-model="dormForm.building" required placeholder="如：1栋、东区5栋">
      </div>
      <div class="form-group">
        <label for="roomNum">房间号</label>
        <input type="text" id="roomNum" v-model="dormForm.roomNum" required placeholder="如：502、301-2">
      </div>
      <button type="submit" id="bind-btn">{{ isEditing ? '修改宿舍' : '绑定宿舍' }}</button>
    </form>
    <router-link to="/student" class="back-link">返回首页</router-link>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { axiosInstance } from '../../stores/user'

const dormForm = ref({
  building: '',
  roomNum: ''
})

const isEditing = ref(false)
const message = ref('')
const messageType = ref('')

const getBoundDormitory = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user) {
      const response = await axiosInstance.get(`/dormitories?userId=${user.userId}`)
      if (response.success && response.data) {
        // 显示已绑定的宿舍
        dormForm.value.building = response.data.building
        dormForm.value.roomNum = response.data.roomNum
        isEditing.value = true
      } else {
        // 恢复为绑定状态
        isEditing.value = false
      }
    }
  } catch (error) {
    console.error('获取宿舍信息失败:', error)
  }
}

const bindDorm = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user'))
    if (!user) {
      throw new Error('用户未登录')
    }
    
    const dormitory = {
      userId: user.userId,
      building: dormForm.value.building,
      roomNum: dormForm.value.roomNum
    }
    
    const response = await axiosInstance.post('/dormitories', dormitory)
    if (response.success) {
      message.value = isEditing.value ? '宿舍修改成功！' : '宿舍绑定成功！'
      messageType.value = 'success'
      // 刷新已绑定的宿舍信息
      getBoundDormitory()
    } else {
      message.value = '操作失败：' + (response.message || '未知错误')
      messageType.value = 'error'
    }
  } catch (error) {
    message.value = '操作失败，请稍后重试'
    messageType.value = 'error'
    console.error('操作失败:', error)
  } finally {
    // 3秒后隐藏消息
    setTimeout(() => {
      message.value = ''
    }, 3000)
  }
}

onMounted(() => {
  getBoundDormitory()
})
</script>

<style scoped>
.student-dorm {
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
  border-bottom: 2px solid #87CEEB;
  padding-bottom: 10px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

input:focus {
  outline: none;
  border-color: #87CEEB;
  box-shadow: 0 0 0 3px rgba(135, 206, 235, 0.1);
}

button {
  padding: 12px 24px;
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(135, 206, 235, 0.3);
}

button:active {
  transform: translateY(0);
}

.message {
  padding: 12px;
  margin-bottom: 20px;
  border-radius: 8px;
  font-size: 14px;
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

.back-link {
  display: inline-block;
  margin-top: 20px;
  color: #4facfe;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.back-link:hover {
  color: #00f2fe;
  text-decoration: underline;
}
</style>