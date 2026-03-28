<template>
  <div class="repair-orders">
    <h2>我的订单</h2>
    <div class="order-list">
      <div v-if="loading" class="no-data">
        加载中...
      </div>
      <div v-else-if="error" class="no-data">
        获取我的订单失败
      </div>
      <div v-else-if="orders.length === 0" class="no-data">
        暂无订单
      </div>
      <div v-else>
        <div v-for="order in orders" :key="order.orderId" class="order-item">
          <h3>报修单 #{{ order.orderId }}</h3>
          <p>宿舍：{{ order.building || '-' }}-{{ order.roomNum || '-' }}</p>
          <p>设备类型：{{ order.deviceType }}</p>
          <p>问题描述：{{ order.problemDesc }}</p>
          <p>状态：<span :class="['order-status', getStatusClass(order.orderStatus)]">{{ order.orderStatus }}</span></p>
          <p>提交时间：{{ order.createTime || new Date().toLocaleString() }}</p>
          <button class="btn" @click="updateOrderStatus(order.orderId)">修改状态</button>
          <button class="btn" @click="viewOrderImages(order.orderId)">查看图片</button>
        </div>
      </div>
    </div>
    
    <!-- 图片查看模态框 -->
    <div id="image-modal" class="modal" v-if="showModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>订单图片</h3>
          <span class="close" @click="closeModal">&times;</span>
        </div>
        <div class="image-gallery">
          <div v-if="loadingImages" class="no-images">
            加载中...
          </div>
          <div v-else-if="errorImages" class="no-images">
            获取图片失败
          </div>
          <div v-else-if="orderImages.length === 0" class="no-images">
            该订单没有上传图片
          </div>
          <div v-else>
            <div v-for="(image, index) in orderImages" :key="index" class="gallery-image">
              <img :src="image.imageUrl" alt="订单图片">
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { axiosInstance } from '../../stores/user'

const orders = ref([])
const loading = ref(true)
const error = ref(false)

const showModal = ref(false)
const orderImages = ref([])
const loadingImages = ref(false)
const errorImages = ref(false)

const getStatusClass = (status) => {
  switch (status) {
    case '待处理':
      return 'status-pending'
    case '维修中':
      return 'status-processing'
    case '已完成':
      return 'status-completed'
    case '已取消':
      return 'status-cancelled'
    default:
      return 'status-pending'
  }
}

const getMyOrders = async () => {
  try {
    loading.value = true
    error.value = false
    const user = JSON.parse(localStorage.getItem('user'))
    if (user) {
      const response = await axiosInstance.get(`/repair-orders?repairmanId=${user.userId}`)
      if (response.success && response.data) {
        // 处理后端返回的分页数据
        orders.value = response.data.records || response.data || []
      }
    }
  } catch (err) {
    console.error('获取我的订单失败:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

const updateOrderStatus = async (orderId) => {
  // 弹出状态选择对话框
  const newStatus = prompt('请选择新状态:', '维修中')
  if (newStatus) {
    try {
      const statusUpdate = { status: newStatus }
      const response = await axiosInstance.put(`/repair-orders/${orderId}/status`, statusUpdate)
      if (response.success) {
        alert('状态修改成功！')
        getMyOrders()
      } else {
        alert('状态修改失败：' + (response.message || '未知错误'))
      }
    } catch (err) {
      alert('状态修改失败，请稍后重试')
      console.error('修改状态失败:', err)
    }
  }
}

const viewOrderImages = async (orderId) => {
  showModal.value = true
  loadingImages.value = true
  errorImages.value = false
  
  try {
    const response = await axiosInstance.get(`/repair-orders/${orderId}/images`)
    if (response.success && response.data) {
      orderImages.value = response.data
    } else {
      orderImages.value = []
    }
  } catch (err) {
    console.error('获取订单图片失败:', err)
    errorImages.value = true
  } finally {
    loadingImages.value = false
  }
}

const closeModal = () => {
  showModal.value = false
  orderImages.value = []
}

// 点击模态框外部关闭
const handleClickOutside = (event) => {
  const modal = document.getElementById('image-modal')
  if (modal && event.target === modal) {
    closeModal()
  }
}

onMounted(() => {
  getMyOrders()
  window.addEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.repair-orders {
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

.order-list {
  margin-top: 20px;
}

.order-item {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.order-item:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.order-item h3 {
  margin-top: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
}

.order-item p {
  margin: 8px 0;
  color: #666;
}

.order-status {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.status-pending {
  background-color: #fff3cd;
  color: #856404;
  border: 1px solid #ffeeba;
}

.status-processing {
  background-color: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

.status-completed {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status-cancelled {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
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
  margin-right: 10px;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(135, 206, 235, 0.3);
}

.no-data {
  text-align: center;
  color: #666;
  padding: 40px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

/* 图片查看模态框 */
.modal {
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.9);
}

.modal-content {
  margin: 10% auto;
  padding: 20px;
  width: 80%;
  max-width: 800px;
  background-color: white;
  border-radius: 12px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h3 {
  margin: 0;
}

.close {
  color: #aaa;
  font-size: 28px;
  font-weight: bold;
  cursor: pointer;
}

.close:hover {
  color: black;
}

.image-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.gallery-image {
  flex: 1 1 30%;
  min-width: 200px;
  max-width: 300px;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
}

.gallery-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.no-images {
  text-align: center;
  color: #666;
  padding: 40px;
  width: 100%;
}
</style>