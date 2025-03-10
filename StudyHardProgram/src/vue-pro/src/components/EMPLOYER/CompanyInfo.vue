<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const companyForm = ref({
  name: '',
  industry: '',
  size: '',
  location: '',
  description: '',
  website: '',
  contact: {
    name: '',
    phone: '',
    email: ''
  },
  logo: '',
  license: ''
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
  'contact.name': [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  'contact.phone': [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  'contact.email': [
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
  '制造业',
  '服务业',
  '其他'
];

const sizeOptions = [
  '0-20人',
  '20-99人',
  '100-499人',
  '500-999人',
  '1000-9999人',
  '10000人以上'
];

onMounted(async () => {
  await fetchCompanyInfo();
});

const fetchCompanyInfo = async () => {
  try {
    loading.value = true;
    const response = await axios.get('/api/employer/company');
    companyForm.value = response.data;
  } catch (error) {
    ElMessage.error('获取单位信息失败');
    console.error('获取单位信息失败:', error);
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    
    await axios.put('/api/employer/company', companyForm.value);
    ElMessage.success('保存成功');
  } catch (error) {
    if (error.name === 'ValidationError') {
      return;
    }
    ElMessage.error('保存失败');
    console.error('保存单位信息失败:', error);
  } finally {
    loading.value = false;
  }
};

const handleLogoUpload = (file) => {
  // 处理logo上传
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('上传头像图片只能是图片格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
    return false;
  }
  return true;
};

const handleLicenseUpload = (file) => {
  // 处理营业执照上传
  const isValidFormat = ['image/jpeg', 'image/png', 'application/pdf'].includes(file.type);
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isValidFormat) {
    ElMessage.error('上传营业执照只能是 JPG/PNG/PDF 格式!');
    return false;
  }
  if (!isLt5M) {
    ElMessage.error('上传营业执照大小不能超过 5MB!');
    return false;
  }
  return true;
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
        <el-divider>基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位名称" prop="name">
              <el-input v-model="companyForm.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属行业" prop="industry">
              <el-select v-model="companyForm.industry" placeholder="请选择行业">
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
              <el-select v-model="companyForm.size" placeholder="请选择规模">
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

        <el-form-item label="官方网站" prop="website">
          <el-input v-model="companyForm.website" placeholder="http://" />
        </el-form-item>

        <!-- 联系信息 -->
        <el-divider>联系信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="联系人" prop="contact.name">
              <el-input v-model="companyForm.contact.name" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contact.phone">
              <el-input v-model="companyForm.contact.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系邮箱" prop="contact.email">
              <el-input v-model="companyForm.contact.email" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 资质文件 -->
        <el-divider>资质文件</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位Logo" prop="logo">
              <el-upload
                class="avatar-uploader"
                action="/api/upload/logo"
                :show-file-list="false"
                :before-upload="handleLogoUpload"
              >
                <img v-if="companyForm.logo" :src="companyForm.logo" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div class="upload-tip">建议尺寸: 200x200px, 支持jpg、png格式</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="营业执照" prop="license">
              <el-upload
                class="license-uploader"
                action="/api/upload/license"
                :show-file-list="false"
                :before-upload="handleLicenseUpload"
              >
                <div v-if="companyForm.license" class="license-preview">
                  <el-icon class="preview-icon"><Document /></el-icon>
                  <span>查看营业执照</span>
                </div>
                <el-button v-else type="primary">
                  <el-icon><Upload /></el-icon>
                  <span>上传营业执照</span>
                </el-button>
              </el-upload>
              <div class="upload-tip">支持jpg、png、pdf格式，大小不超过5MB</div>
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

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.license-uploader {
  text-align: center;
}

.license-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
}

.license-preview:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}

.preview-icon {
  margin-right: 8px;
}

.upload-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
}

:deep(.el-divider__text) {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}
</style> 