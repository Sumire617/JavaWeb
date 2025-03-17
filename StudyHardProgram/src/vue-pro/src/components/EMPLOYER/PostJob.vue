<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useRouter } from 'vue-router';

// 接收props
const props = defineProps({
  isEditing: {
    type: Boolean,
    default: false
  },
  jobId: {
    type: String,
    default: ''
  }
});

const router = useRouter();

const jobForm = ref({
  jobTitle: '',
  jobType: '',
  department: '',
  location: '',
  salaryRange: '',
  experienceRequirement: '',
  educationRequirement: '',
  jobDescription: '',
  requirements: '',
  benefits: '',
  recruitmentCount: 1,
  deadline: '',
  workSchedule: '',
  contactPerson: '',
  contactEmail: '',
  contactPhone: ''
});

const formRules = {
  jobTitle: [
    { required: true, message: '请输入岗位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  jobType: [
    { required: true, message: '请选择岗位类型', trigger: 'change' }
  ],
  department: [
    { required: true, message: '请输入所属部门', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入工作地点', trigger: 'blur' }
  ],
  salaryRange: [
    { required: true, message: '请选择薪资范围', trigger: 'change' }
  ],
  experienceRequirement: [
    { required: true, message: '请选择经验要求', trigger: 'change' }
  ],
  educationRequirement: [
    { required: true, message: '请选择学历要求', trigger: 'change' }
  ],
  jobDescription: [
    { required: true, message: '请输入岗位描述', trigger: 'blur' },
    { min: 10, message: '岗位描述不能少于10个字符', trigger: 'blur' }
  ],
  requirements: [
    { required: true, message: '请输入任职要求', trigger: 'blur' }
  ],
  recruitmentCount: [
    { required: true, message: '请输入招聘人数', trigger: 'blur' },
    { type: 'number', message: '招聘人数必须为数字', trigger: 'blur' }
  ],
  deadline: [
    { required: true, message: '请选择截止日期', trigger: 'change' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
};

const formRef = ref(null);
const loading = ref(false);
const pageTitle = ref(props.isEditing ? '编辑岗位' : '发布岗位');

// 岗位类型选项
const jobTypes = [
  '全职',
  '兼职',
  '实习',
  '项目制',
  '临时工'
];

// 薪资范围选项
const salaryRanges = [
  '3k-5k',
  '5k-8k',
  '8k-12k',
  '12k-15k',
  '15k-20k',
  '20k-30k',
  '30k以上',
  '面议'
];

// 经验要求选项
const experienceRequirements = [
  '应届生',
  '1-3年',
  '3-5年',
  '5-10年',
  '10年以上',
  '不限'
];

// 学历要求选项
const educationRequirements = [
  '博士',
  '硕士',
  '本科',
  '大专',
  '高中',
  '不限'
];

// 工作时间选项
const workSchedules = [
  '朝九晚五',
  '双休',
  '大小周',
  '排班制',
  '弹性工作'
];

// 加载岗位数据（编辑模式）
const loadJobData = async () => {
  if (!props.isEditing || !props.jobId) return;
  
  try {
    loading.value = true;
    const response = await axios.get(`/api/jobs/${props.jobId}`);
    const jobData = response.data;
    
    // 填充表单数据
    jobForm.value.jobTitle = jobData.jobTitle || '';
    jobForm.value.jobType = jobData.jobType || '';
    jobForm.value.location = jobData.location || '';
    jobForm.value.salaryRange = jobData.salaryRange || '';
    jobForm.value.jobDescription = jobData.jobDescription || '';
    jobForm.value.requirements = jobData.requirements || '';
    
    // 其他字段如果API返回了就填充
    if (jobData.department) jobForm.value.department = jobData.department;
    if (jobData.experienceRequirement) jobForm.value.experienceRequirement = jobData.experienceRequirement;
    if (jobData.educationRequirement) jobForm.value.educationRequirement = jobData.educationRequirement;
    if (jobData.benefits) jobForm.value.benefits = jobData.benefits;
    if (jobData.recruitmentCount) jobForm.value.recruitmentCount = jobData.recruitmentCount;
    if (jobData.deadline) jobForm.value.deadline = jobData.deadline;
    if (jobData.workSchedule) jobForm.value.workSchedule = jobData.workSchedule;
    if (jobData.contactPerson) jobForm.value.contactPerson = jobData.contactPerson;
    if (jobData.contactEmail) jobForm.value.contactEmail = jobData.contactEmail;
    if (jobData.contactPhone) jobForm.value.contactPhone = jobData.contactPhone;
    
  } catch (error) {
    ElMessage.error('加载岗位数据失败: ' + (error.response?.data?.message || error.message));
    console.error('加载岗位数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 组件加载时执行
onMounted(() => {
  if (props.isEditing) {
    loadJobData();
  }
});

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    
    // 转换数据格式以匹配后端API
    const jobData = {
      jobTitle: jobForm.value.jobTitle,
      jobType: jobForm.value.jobType,
      jobDescription: jobForm.value.jobDescription,
      requirements: jobForm.value.requirements,
      salaryRange: jobForm.value.salaryRange,
      location: jobForm.value.location,
      updatedAt: new Date().toISOString()
    };
    
    // 根据是否编辑模式决定API调用
    let response;
    if (props.isEditing && props.jobId) {
      // 编辑模式 - PUT请求
      response = await axios.put(`/api/jobs/${props.jobId}`, jobData);
      if (response.status === 200) {
        ElMessage.success('岗位更新成功');
        // 返回岗位管理页面
        router.push('/employer/jobs/manage');
      }
    } else {
      // 新增模式 - POST请求
      jobData.status = 'PENDING';
      jobData.postedAt = new Date().toISOString();
      response = await axios.post('/api/jobs', jobData);
      if (response.status === 200 || response.status === 201) {
        ElMessage.success('岗位发布成功');
        formRef.value.resetFields();
      }
    }
  } catch (error) {
    if (error.name === 'ValidationError') return;
    ElMessage.error('操作失败：' + (error.response?.data?.message || error.message));
    console.error('操作失败:', error);
  } finally {
    loading.value = false;
  }
};

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields();
};
</script>

<template>
  <div class="post-job">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <h2>{{ pageTitle }}</h2>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="jobForm"
        :rules="formRules"
        label-width="120px"
        v-loading="loading"
      >
        <!-- 基本信息 -->
        <el-divider>基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="岗位名称" prop="jobTitle">
              <el-input v-model="jobForm.jobTitle" placeholder="请输入岗位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位类型" prop="jobType">
              <el-select v-model="jobForm.jobType" placeholder="请选择岗位类型">
                <el-option
                  v-for="type in jobTypes"
                  :key="type"
                  :label="type"
                  :value="type"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="department">
              <el-input v-model="jobForm.department" placeholder="请输入所属部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作地点" prop="location">
              <el-input v-model="jobForm.location" placeholder="请输入工作地点" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 要求信息 -->
        <el-divider>要求信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="薪资范围" prop="salaryRange">
              <el-select v-model="jobForm.salaryRange" placeholder="请选择薪资范围">
                <el-option
                  v-for="range in salaryRanges"
                  :key="range"
                  :label="range"
                  :value="range"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="经验要求" prop="experienceRequirement">
              <el-select v-model="jobForm.experienceRequirement" placeholder="请选择经验要求">
                <el-option
                  v-for="exp in experienceRequirements"
                  :key="exp"
                  :label="exp"
                  :value="exp"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学历要求" prop="educationRequirement">
              <el-select v-model="jobForm.educationRequirement" placeholder="请选择学历要求">
                <el-option
                  v-for="edu in educationRequirements"
                  :key="edu"
                  :label="edu"
                  :value="edu"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="岗位描述" prop="jobDescription">
          <el-input
            v-model="jobForm.jobDescription"
            type="textarea"
            :rows="4"
            placeholder="请详细描述岗位职责和工作内容"
          />
        </el-form-item>

        <el-form-item label="任职要求" prop="requirements">
          <el-input
            v-model="jobForm.requirements"
            type="textarea"
            :rows="4"
            placeholder="请详细描述岗位要求和技能需求"
          />
        </el-form-item>

        <el-form-item label="福利待遇" prop="benefits">
          <el-input
            v-model="jobForm.benefits"
            type="textarea"
            :rows="3"
            placeholder="请描述岗位福利待遇（选填）"
          />
        </el-form-item>

        <!-- 其他信息 -->
        <el-divider>其他信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="招聘人数" prop="recruitmentCount">
              <el-input-number
                v-model="jobForm.recruitmentCount"
                :min="1"
                :max="999"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="截止日期" prop="deadline">
              <el-date-picker
                v-model="jobForm.deadline"
                type="date"
                placeholder="选择截止日期"
                value-format="YYYY-MM-DD"
                :disabled-date="(time) => time.getTime() < Date.now()"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="工作时间" prop="workSchedule">
              <el-select v-model="jobForm.workSchedule" placeholder="请选择工作时间">
                <el-option
                  v-for="schedule in workSchedules"
                  :key="schedule"
                  :label="schedule"
                  :value="schedule"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 联系方式 -->
        <el-divider>联系方式</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="jobForm.contactPerson" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="jobForm.contactEmail" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="jobForm.contactPhone" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm">发布岗位</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.post-job {
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

:deep(.el-divider__text) {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

:deep(.el-input-number) {
  width: 100%;
}
</style> 