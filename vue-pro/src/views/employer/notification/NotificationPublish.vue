<template>
  <div class="notification-publish">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>发布通知</span>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="notification-form"
      >
        <el-form-item label="接收用户" prop="userId">
          <el-select
            v-model="form.userId"
            placeholder="请选择接收用户"
            style="width: 100%"
          >
            <el-option
              v-for="user in users"
              :key="user.userId"
              :label="user.username"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="通知内容" prop="message">
          <el-input
            v-model="form.message"
            type="textarea"
            :rows="4"
            placeholder="请输入通知内容"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">发布通知</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'NotificationPublish',
  setup() {
    const formRef = ref(null)
    const users = ref([])
    const loading = ref(false)
    
    const form = ref({
      userId: '',
      message: ''
    })
    
    const rules = {
      userId: [
        { required: true, message: '请选择接收用户', trigger: 'change' }
      ],
      message: [
        { required: true, message: '请输入通知内容', trigger: 'blur' },
        { min: 1, max: 500, message: '长度在 1 到 500 个字符', trigger: 'blur' }
      ]
    }
    
    const fetchUsers = async () => {
      try {
        console.log('开始获取用户列表')
        const response = await axios.get('/api/users')
        console.log('获取用户列表成功:', response.data)
        users.value = response.data
      } catch (error) {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败: ' + (error.response?.data?.message || error.message))
      }
    }
    
    const submitForm = async () => {
      if (!formRef.value) return
      
      try {
        console.log('开始提交表单:', form.value)
        await formRef.value.validate()
        loading.value = true
        
        // 添加请求头
        const headers = {
          'Content-Type': 'application/json'
        }
        
        console.log('发送请求到:', '/api/notifications')
        console.log('请求数据:', form.value)
        console.log('请求头:', headers)
        
        const response = await axios.post('/api/notifications', form.value, { headers })
        console.log('发布通知成功:', response.data)
        ElMessage.success('发布通知成功')
        resetForm()
      } catch (error) {
        console.error('发布通知失败:', error)
        if (error.response) {
          console.error('错误响应:', error.response.data)
          console.error('错误状态码:', error.response.status)
          console.error('错误头:', error.response.headers)
          ElMessage.error(error.response.data.message || '发布通知失败')
        } else if (error.request) {
          console.error('请求错误:', error.request)
          ElMessage.error('网络请求失败，请检查网络连接')
        } else {
          console.error('其他错误:', error.message)
          ElMessage.error('发布通知失败: ' + error.message)
        }
      } finally {
        loading.value = false
      }
    }
    
    const resetForm = () => {
      if (formRef.value) {
        formRef.value.resetFields()
      }
    }
    
    onMounted(() => {
      fetchUsers()
    })
    
    return {
      formRef,
      form,
      rules,
      users,
      loading,
      submitForm,
      resetForm
    }
  }
}
</script>

<style scoped>
.notification-publish {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-form {
  max-width: 600px;
  margin: 0 auto;
}
</style> 