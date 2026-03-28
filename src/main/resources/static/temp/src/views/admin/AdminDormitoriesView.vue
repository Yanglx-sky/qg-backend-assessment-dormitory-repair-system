<template>
  <div class="admin-dormitories">
    <h2>宿舍管理</h2>
    <div class="dorm-list">
      <div v-if="loading" class="no-data">
        加载中...
      </div>
      <div v-else-if="error" class="no-data">
        获取宿舍列表失败
      </div>
      <div v-else-if="dormitories.length === 0" class="no-data">
        暂无宿舍
      </div>
      <div v-else>
        <div v-for="dorm in dormitories" :key="dorm.dormId" class="dorm-item">
          <h3>宿舍 #{{ dorm.dormId }}</h3>
          <p>楼栋：{{ dorm.building }}</p>
          <p>房间号：{{ dorm.roomNum }}</p>
          <button class="btn btn-delete" @click="deleteDorm(dorm.dormId)">删除</button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { axiosInstance } from '../../stores/user'

const dormitories = ref([])
const loading = ref(true)
const error = ref(false)

// 分页相关状态
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const pages = ref(1)

const getAllDormitories = async (page = 1, size = 5) => {
  try {
    loading.value = true
    error.value = false
    const response = await axiosInstance.get(`/dormitories?page=${page}&size=${size}`)
    if (response.success && response.data) {
      // 处理后端返回的数据，兼容分页和非分页格式
      if (response.data.records) {
        // 分页格式
        dormitories.value = response.data.records || []
        total.value = response.data.total || 0
        pages.value = response.data.pages || 1
        currentPage.value = page
        pageSize.value = size
      } else {
        // 非分页格式（直接返回宿舍列表）
        dormitories.value = response.data
        total.value = dormitories.value.length
        pages.value = 1
        currentPage.value = 1
      }
    }
  } catch (err) {
    console.error('获取宿舍列表失败:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

const deleteDorm = async (dormId) => {
  if (confirm('确定要删除这个宿舍吗？')) {
    try {
      const response = await axiosInstance.delete(`/dormitories/${dormId}`)
      if (response.success) {
        alert('宿舍删除成功！')
        getAllDormitories(currentPage.value, pageSize.value)
      } else {
        alert('宿舍删除失败：' + (response.message || '未知错误'))
      }
    } catch (err) {
      alert('宿舍删除失败，请稍后重试')
      console.error('删除宿舍失败:', err)
    }
  }
}

// 分页相关方法
const changePage = (page) => {
  if (page >= 1 && page <= pages.value) {
    getAllDormitories(page, pageSize.value)
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
  getAllDormitories(currentPage.value, pageSize.value)
}

onMounted(() => {
  getAllDormitories(currentPage.value, pageSize.value)
})
</script>

<style scoped>
.admin-dormitories {
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

h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: 600;
}

.dorm-list {
  margin-top: 20px;
}

.dorm-item {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.dorm-item:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.dorm-item h3 {
  margin-top: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
}

.dorm-item p {
  margin: 8px 0;
  color: #666;
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

.add-dorm {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #f8f9fa;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #87CEEB;
  box-shadow: 0 0 0 3px rgba(135, 206, 235, 0.1);
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