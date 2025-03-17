<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElLoading } from 'element-plus';
import axios from 'axios';
import { Plus, Document, Upload } from '@element-plus/icons-vue';

// 使用reactive而不是ref，确保对象属性变更能被正确跟踪
const companyForm = reactive({
  name: '',
  industry: '',
  size: '',
  location: '',
  description: '',
  contactName: '',
  contactPhone: '',
  contactEmail: ''
});

const rules = {
  name: [
    { required: true, message: '请输入单位名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  industry: [
    { required: true, message: '请选择所属行业', trigger: 'change' }
  ],
  size: [
    { required: true, message: '请选择单位规模', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入单位地址', trigger: 'blur' }
  ],
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
};

const formRef = ref(null);
const loading = ref(false);

const industryOptions = [
  '互联网/IT',
  '金融',
  '教育',
  '医疗健康',
  '房地产',
  '服务业',
  '其他'
];

const sizeOptions = [
  '0-20人',
  '20-99人',
  '100-499人',
  '500人以上'
];

// 获取当前登录用户ID
const getCurrentUserId = () => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      return user.userId;
    } catch (e) {
      console.error('解析用户信息失败:', e);
      return null;
    }
  }
  return null;
};

onMounted(async () => {
  await fetchCompanyInfo();
});

const fetchCompanyInfo = async () => {
  const userId = getCurrentUserId();
  if (!userId) {
    ElMessage.error('未获取到用户信息，请重新登录');
    return;
  }
  
  const loadingInstance = ElLoading.service({
    target: '.company-info',
    text: '加载中...'
  });
  
  try {
    loading.value = true;
    const response = await axios.get(`http://localhost:8080/api/employer/company`, {
      params: { employerId: userId },
      headers: { 'Accept': 'application/json' }
    });
    
    // 检查响应是否为JSON对象
    if (response.data && typeof response.data === 'object') {
      // 更新表单数据
      Object.keys(companyForm).forEach(key => {
        if (response.data[key] !== undefined) {
          companyForm[key] = response.data[key];
        }
      });
    } else {
      throw new Error('服务器返回了非预期的响应格式');
    }
  } catch (error) {
    console.error('获取单位信息失败:', error);
    ElMessage.error('获取单位信息失败: ' + (error.response?.data?.message || error.message || '未知错误'));
    
    // 重置表单数据
    Object.keys(companyForm).forEach(key => {
      companyForm[key] = '';
    });
  } finally {
    loading.value = false;
    loadingInstance.close();
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  const userId = getCurrentUserId();
  if (!userId) {
    ElMessage.error('未获取到用户信息，请重新登录');
    return;
  }
  
  try {
    // 表单验证
    await formRef.value.validate();
    
    // 显示加载状态
    const loadingInstance = ElLoading.service({
      target: '.company-info',
      text: '保存中...'
    });
    
    loading.value = true;
    
    // 发送请求
    await axios.put(`http://localhost:8080/api/employer/company`, companyForm, {
      params: { employerId: userId },
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });
    
    ElMessage.success('保存成功');
  } catch (error) {
    if (error.name === 'ValidationError') {
      return;
    }
    console.error('保存单位信息失败:', error);
    ElMessage.error('保存失败: ' + (error.response?.data?.message || error.message || '未知错误'));
  } finally {
    loading.value = false;
    if (window.loadingInstance) {
      window.loadingInstance.close();
    }
  }
};
</script>

<template>
  <div class="company-info">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <h2>单位信息管理</h2>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="companyForm"
        :rules="rules"
        label-width="120px"
        v-loading="loading"
      >
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位名称" prop="name">
              <el-input v-model="companyForm.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属行业" prop="industry">
              <el-select v-model="companyForm.industry" placeholder="请选择行业" style="width: 100%;">
                <el-option
                  v-for="item in industryOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位规模" prop="size">
              <el-select v-model="companyForm.size" placeholder="请选择规模" style="width: 100%;">
                <el-option
                  v-for="item in sizeOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位地址" prop="location">
              <el-input v-model="companyForm.location" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="单位简介" prop="description">
          <el-input
            v-model="companyForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入单位简介"
          />
        </el-form-item>

        <!-- 联系信息 -->
        <el-divider content-position="left">联系信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="companyForm.contactName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="companyForm.contactPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="companyForm.contactEmail" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存修改</el-button>
          <el-button @click="fetchCompanyInfo">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.company-info {
  padding: 20px;
  min-height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-select .el-input) {
  width: 100%;
}

:deep(.el-select__popper) {
  z-index: 9999 !important;
}

:deep(.el-divider__text) {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}
</style>