<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const notificationForm = ref({
  title: '',
  content: '',
  type: 'GENERAL',
  priority: 'NORMAL',
  targetGroups: [],
  attachments: [],
  validUntil: ''
});

const formRules = {
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' },
    { min: 10, message: '内容不能少于10个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择通知类型', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  targetGroups: [
    { required: true, message: '请选择目标群体', trigger: 'change' }
  ],
  validUntil: [
    { required: true, message: '请选择有效期', trigger: 'change' }
  ]
};

const formRef = ref(null);
const loading = ref(false);
const richTextEditor = ref(null);

// 通知类型选项
const notificationTypes = [
  { value: 'GENERAL', label: '普通通知' },
  { value: 'IMPORTANT', label: '重要通知' },
  { value: 'URGENT', label: '紧急通知' },
  { value: 'SYSTEM', label: '系统通知' }
];

// 优先级选项
const priorityOptions = [
  { value: 'LOW', label: '低' },
  { value: 'NORMAL', label: '中' },
  { value: 'HIGH', label: '高' }
];

// 目标群体选项
const targetGroupOptions = [
  { value: 'ALL', label: '全体员工' },
  { value: 'DEPARTMENT', label: '指定部门' },
  { value: 'POSITION', label: '指定职位' },
  { value: 'INDIVIDUAL', label: '指定个人' }
];

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    
    const formData = new FormData();
    Object.keys(notificationForm.value).forEach(key => {
      if (key === 'attachments') {
        notificationForm.value[key].forEach(file => {
          formData.append('attachments', file.raw);
        });
      } else if (key === 'targetGroups') {
        formData.append(key, JSON.stringify(notificationForm.value[key]));
      } else {
        formData.append(key, notificationForm.value[key]);
      }
    });
    
    await axios.post('/api/notifications', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    
    ElMessage.success('通知发布成功');
    formRef.value.resetFields();
    notificationForm.value.attachments = [];
  } catch (error) {
    if (error.name === 'ValidationError') return;
    ElMessage.error('通知发布失败');
    console.error('通知发布失败:', error);
  } finally {
    loading.value = false;
  }
};

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields();
  notificationForm.value.attachments = [];
};
</script>

<template>
  <div class="publish-notification">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <h2>发布通知</h2>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="notificationForm"
        :rules="formRules"
        label-width="100px"
        v-loading="loading"
      >
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="notificationForm.title" placeholder="请输入通知标题" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="通知类型" prop="type">
              <el-select v-model="notificationForm.type" placeholder="请选择通知类型">
                <el-option
                  v-for="type in notificationTypes"
                  :key="type.value"
                  :label="type.label"
                  :value="type.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="notificationForm.priority" placeholder="请选择优先级">
                <el-option
                  v-for="priority in priorityOptions"
                  :key="priority.value"
                  :label="priority.label"
                  :value="priority.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="有效期" prop="validUntil">
              <el-date-picker
                v-model="notificationForm.validUntil"
                type="datetime"
                placeholder="选择有效期"
                value-format="YYYY-MM-DD HH:mm:ss"
                :disabled-date="(time) => time.getTime() < Date.now()"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="目标群体" prop="targetGroups">
          <el-select
            v-model="notificationForm.targetGroups"
            multiple
            placeholder="请选择目标群体"
          >
            <el-option
              v-for="group in targetGroupOptions"
              :key="group.value"
              :label="group.label"
              :value="group.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="通知内容" prop="content">
          <el-input
            v-model="notificationForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入通知内容"
          />
        </el-form-item>


        <el-form-item>
          <el-button type="primary" @click="handleSubmit">发布通知</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.publish-notification {
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

.upload-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
}

:deep(.el-upload-list) {
  margin-top: 10px;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-date-picker) {
  width: 100%;
}
</style> 