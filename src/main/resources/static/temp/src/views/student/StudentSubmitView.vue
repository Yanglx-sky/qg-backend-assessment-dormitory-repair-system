<template>
  <div class="student-submit">
    <h2>提交报修</h2>
    <div class="message" :class="{ 'success': messageType === 'success', 'error': messageType === 'error' }" v-if="message">
      {{ message }}
    </div>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="deviceType">设备类型</label>
        <input type="text" id="deviceType" v-model="form.deviceType" placeholder="如：灯、水龙头、门" required>
      </div>
      <div class="form-group">
        <label for="problemDesc">问题描述</label>
        <textarea id="problemDesc" v-model="form.problemDesc" rows="3" placeholder="请详细描述问题，以便维修人员快速了解情况" required></textarea>
      </div>
      <div class="form-group">
        <label>上传图片（最多3张，可选）</label>
        <div style="display: flex; gap: 10px; margin-bottom: 10px;">
          <input type="file" id="file1" accept="image/*" @change="previewImage($event, 'preview1')" style="flex: 1;">
          <input type="file" id="file2" accept="image/*" @change="previewImage($event, 'preview2')" style="flex: 1;">
          <input type="file" id="file3" accept="image/*" @change="previewImage($event, 'preview3')" style="flex: 1;">
        </div>
        <div id="preview-container" style="display: flex; gap: 10px; margin-top: 10px;">
          <div v-for="(preview, index) in previews" :key="index" :id="preview.id" style="width: 100px; height: 100px; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;">
            <img :src="preview.src" style="width: 100%; height: 100%; object-fit: cover;">
          </div>
        </div>
      </div>
      <button type="submit" class="btn" :disabled="loading">提交报修</button>
    </form>
    <router-link to="/student" class="back-link">返回首页</router-link>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { axiosInstance } from '../../stores/user'

const form = ref({
  deviceType: '',
  problemDesc: ''
})

const previews = ref([])
const files = ref([])
const message = ref('')
const messageType = ref('')
const loading = ref(false)

const previewImage = (event, previewId) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = function(e) {
      const existingPreview = previews.value.find(p => p.id === previewId)
      if (existingPreview) {
        existingPreview.src = e.target.result
      } else {
        previews.value.push({ id: previewId, src: e.target.result })
      }
      
      // 保存文件
      const fileIndex = files.value.findIndex(f => f.id === previewId)
      if (fileIndex > -1) {
        files.value[fileIndex].file = file
      } else {
        files.value.push({ id: previewId, file: file })
      }
    }
    reader.readAsDataURL(file)
  } else {
    // 移除预览
    previews.value = previews.value.filter(p => p.id !== previewId)
    files.value = files.value.filter(f => f.id !== previewId)
  }
}

const uploadImages = async (orderId) => {
  const uploadPromises = files.value.map(item => {
    const formData = new FormData()
    formData.append('file', item.file)
    
    return axiosInstance.post(`/repair-orders/${orderId}/images`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  })
  
  return Promise.all(uploadPromises)
}

const handleSubmit = async () => {
  try {
    loading.value = true
    message.value = '正在提交报修...'
    messageType.value = 'success'
    
    const user = JSON.parse(localStorage.getItem('user'))
    if (!user) {
      throw new Error('用户未登录')
    }
    
    const order = {
      userId: user.userId,
      deviceType: form.value.deviceType,
      problemDesc: form.value.problemDesc
    }
    
    const response = await axiosInstance.post('/repair-orders', order)
    if (response.success) {
      // 获取订单ID
      const orderId = response.data.orderId
      
      // 上传图片
      if (files.value.length > 0) {
        try {
          const uploadResults = []
          for (const item of files.value) {
            const formData = new FormData()
            formData.append('file', item.file)
            
            try {
              await axiosInstance.post(`/repair-orders/${orderId}/images`, formData, {
                headers: {
                  'Content-Type': 'multipart/form-data'
                }
              })
              uploadResults.push({ success: true })
            } catch (uploadError) {
              console.error('图片上传失败:', uploadError)
              uploadResults.push({ success: false, error: uploadError })
            }
          }
          
          const failedCount = uploadResults.filter(r => !r.success).length
          if (failedCount > 0) {
            message.value = `报修提交成功，但有${failedCount}张图片上传失败，请稍后重试`
            messageType.value = 'success'
          } else {
            message.value = '报修提交成功！'
            messageType.value = 'success'
          }
        } catch (uploadError) {
          console.error('图片上传失败:', uploadError)
          message.value = '报修提交成功，但图片上传失败，请稍后重试上传图片'
          messageType.value = 'success'
        }
      } else {
        message.value = '报修提交成功！'
        messageType.value = 'success'
      }
      
      // 清空表单
      form.value = {
        deviceType: '',
        problemDesc: ''
      }
      previews.value = []
      files.value = []
      
      // 重置文件输入
      document.getElementById('file1').value = ''
      document.getElementById('file2').value = ''
      document.getElementById('file3').value = ''
    } else {
      throw new Error(response.message || '提交失败')
    }
  } catch (error) {
    message.value = '报修提交失败：' + error.message
    messageType.value = 'error'
  } finally {
    loading.value = false
    
    // 3秒后隐藏消息
    setTimeout(() => {
      message.value = ''
    }, 3000)
  }
}
</script>

<style scoped>
.student-submit {
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

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

input, textarea {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

input:focus, textarea:focus {
  outline: none;
  border-color: #87CEEB;
  box-shadow: 0 0 0 3px rgba(135, 206, 235, 0.1);
}

textarea {
  resize: vertical;
  min-height: 100px;
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

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
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