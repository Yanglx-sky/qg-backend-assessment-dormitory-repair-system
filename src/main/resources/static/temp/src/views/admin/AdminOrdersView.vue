<template>
  <div class="admin-orders">
    <h2>订单管理</h2>
    <div class="filter-container">
      <div class="form-group">
        <label for="status-filter">按状态筛选：</label>
        <select id="status-filter" v-model="selectedStatus" @change="filterOrders">
          <option value="">全部</option>
          <option value="待处理">待处理</option>
          <option value="维修中">维修中</option>
          <option value="已完成">已完成</option>
          <option value="已取消">已取消</option>
        </select>
      </div>
    </div>
    <div class="order-list">
      <div v-if="loading" class="no-data">
        加载中...
      </div>
      <div v-else-if="error" class="no-data">
        获取订单列表失败
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
          <p v-if="order.repairmanName">维修人员：{{ order.repairmanName }}</p>
          <div class="order-actions">
            <button class="btn" @click="updateOrderStatus(order.orderId)">修改状态</button>
            <button class="btn" @click="viewOrderImages(order.orderId)">查看图片</button>
            <button class="btn btn-delete" @click="deleteOrder(order.orderId)">删除</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页组件 -->
    <div v-if="total > 0" class="pagination">
      <div class="pagination-info">
        共 {{ total }} 条记录，每页 {{ pageSize }} 条，共 {{ pages }} 页
      </div>
      <div class="pagination-controls">
        <button class="btn-pagination" @click="prevPage" :disabled="currentPage === 1">
          上一页
        </button>
        <span class="page-info">
          第 {{ currentPage }} / {{ pages }} 页
        </span>
        <button class="btn-pagination" @click="nextPage" :disabled="currentPage === pages">
          下一页
        </button>
        <select v-model="pageSize" @change="changePageSize(pageSize)" class="page-size-select">
          <option value="5">5条/页</option>
          <option value="10">10条/页</option>
          <option value="20">20条/页</option>
        </select>
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
const selectedStatus = ref('')

// 分页相关状态
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const pages = ref(1)

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

const getAllOrders = async (status = '', page = 1, size = 5) => {
  try {
    loading.value = true
    error.value = false
    let url = `/repair-orders?page=${page}&size=${size}&includeUserInfo=true`
    if (status) {
      url += `&status=${encodeURIComponent(status)}`
    }
    console.log('获取订单列表，状态:', status, '页码:', page, '每页大小:', size, 'URL:', url)
    const response = await axiosInstance.get(url)
    console.log('获取订单列表响应:', response)
    if (response.success && response.data) {
      // 处理后端返回的数据，兼容分页和非分页格式
      let orderList = []
      if (response.data.records) {
        // 分页格式
        orderList = response.data.records || []
        total.value = response.data.total || 0
        pages.value = response.data.pages || 1
        currentPage.value = page
        pageSize.value = size
      } else {
        // 非分页格式（直接返回订单列表）
        orderList = response.data
        total.value = orderList.length
        pages.value = 1
        currentPage.value = 1
      }
      // 为每个订单添加newStatus字段，初始值为当前的orderStatus
      orders.value = orderList.map(order => ({
        ...order,
        newStatus: order.orderStatus
      }))
      console.log('订单列表:', orders.value)
    }
  } catch (err) {
    console.error('获取订单列表失败:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

const filterOrders = () => {
  currentPage.value = 1
  getAllOrders(selectedStatus.value, currentPage.value, pageSize.value)
}

// 分页相关方法
const changePage = (page) => {
  if (page >= 1 && page <= pages.value) {
    getAllOrders(selectedStatus.value, page, pageSize.value)
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    changePage(currentPage.value - 1)
  }
}

const nextPage = () => {
  if (currentPage.value < pages.value) {
    changePage(currentPage.value + 1)
  }
}

const changePageSize = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getAllOrders(selectedStatus.value, currentPage.value, pageSize.value)
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

// 更新订单状态
const updateOrderStatus = async (orderId) => {
  // 弹出状态选择对话框
  const newStatus = prompt('请选择新状态:', '待处理')
  if (newStatus) {
    try {
      const statusUpdate = { status: newStatus }
      const response = await axiosInstance.put(`/repair-orders/${orderId}/status`, statusUpdate)
      if (response.success) {
        alert('状态修改成功！')
        getAllOrders(selectedStatus.value, currentPage.value, pageSize.value)
      } else {
        alert('状态修改失败：' + (response.message || '未知错误'))
      }
    } catch (err) {
      alert('状态修改失败，请稍后重试')
      console.error('修改状态失败:', err)
    }
  }
}

// 删除订单
const deleteOrder = async (orderId) => {
  if (!confirm('确定要删除这个订单吗？')) {
    return
  }
  
  try {
    const response = await axiosInstance.delete(`/repair-orders/${orderId}`)
    if (response.success) {
      // 重新获取订单列表，更新页面
      getAllOrders(selectedStatus.value, currentPage.value, pageSize.value)
      alert('订单删除成功！')
    } else {
      alert('订单删除失败：' + (response.message || '未知错误'))
    }
  } catch (err) {
    console.error('删除订单失败:', err)
    alert('订单删除失败，请稍后重试')
  }
}

// 点击模态框外部关闭
const handleClickOutside = (event) => {
  const modal = document.getElementById('image-modal')
  if (modal && event.target === modal) {
    closeModal()
  }
}

onMounted(() => {
  getAllOrders(selectedStatus.value, currentPage.value, pageSize.value)
  window.addEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.admin-orders {
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

.filter-container {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.filter-container .form-group {
  margin-bottom: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-container label {
  font-weight: 500;
  color: #555;
  margin: 0;
}

.filter-container select {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  background-color: white;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
  outline: none;
}

.filter-container select:focus {
  outline: none;
  border-color: #87CEEB;
  box-shadow: 0 0 0 3px rgba(135, 206, 235, 0.1);
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

.order-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
}

.btn {
  margin-top: 0;
}

.order-status {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.status-pending {
  background-color: #ffc107;
  color: #fff;
  border: 1px solid #ffc107;
}

.status-processing {
  background-color: #17a2b8;
  color: #fff;
  border: 1px solid #17a2b8;
}

.status-completed {
  background-color: #28a745;
  color: #fff;
  border: 1px solid #28a745;
}

.status-cancelled {
  background-color: #dc3545;
  color: #fff;
  border: 1px solid #dc3545;
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

.btn-delete {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
}

.btn-delete:hover {
  box-shadow: 0 5px 15px rgba(255, 107, 107, 0.3);
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

/* 分页样式 */
.pagination {
  margin-top: 30px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.pagination-info {
  text-align: center;
  margin-bottom: 15px;
  color: #666;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.btn-pagination {
  padding: 6px 12px;
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #333;
}

.btn-pagination:hover:not(:disabled) {
  background-color: #f0f0f0;
  border-color: #87CEEB;
}

.btn-pagination:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
  min-width: 100px;
  text-align: center;
}

.page-size-select {
  padding: 6px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
  background-color: white;
  cursor: pointer;
  color: #333;
}

.page-size-select:focus {
  outline: none;
  border-color: #87CEEB;
  box-shadow: 0 0 0 3px rgba(135, 206, 235, 0.1);
}
</style>