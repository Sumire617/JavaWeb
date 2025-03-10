<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

const staffList = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const isEdit = ref(false);

const staffForm = ref({
  id: '',
  name: '',
  position: '',
  department: '',
  employeeId: '',
  phone: '',
  email: '',
  status: '在职',
  joinDate: '',
  role: 'STAFF'
});

const formRules = {
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入部门', trigger: 'blur' }
  ],
  employeeId: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  joinDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ]
};

const formRef = ref(null);
const searchQuery = ref('');
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 获取员工列表
const fetchStaffList = async () => {
  try {
    loading.value = true;
    const { data } = await axios.get('/api/employer/staff', {
      params: {
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize,
        query: searchQuery.value
      }
    });
    staffList.value = data.content;
    pagination.value.total = data.totalElements;
  } catch (error) {
    ElMessage.error('获取员工列表失败');
    console.error('获取员工列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 添加/编辑员工
const handleAddOrEdit = (row) => {
  if (row) {
    isEdit.value = true;
    dialogTitle.value = '编辑员工';
    staffForm.value = { ...row };
  } else {
    isEdit.value = false;
    dialogTitle.value = '添加员工';
    staffForm.value = {
      name: '',
      position: '',
      department: '',
      employeeId: '',
      phone: '',
      email: '',
      status: '在职',
      joinDate: '',
      role: 'STAFF'
    };
  }
  dialogVisible.value = true;
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    
    if (isEdit.value) {
      await axios.put(`/api/employer/staff/${staffForm.value.id}`, staffForm.value);
      ElMessage.success('更新成功');
    } else {
      await axios.post('/api/employer/staff', staffForm.value);
      ElMessage.success('添加成功');
    }
    
    dialogVisible.value = false;
    fetchStaffList();
  } catch (error) {
    if (error.name === 'ValidationError') return;
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败');
    console.error('提交员工信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 删除员工
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该员工吗？', '提示', {
      type: 'warning'
    });
    
    await axios.delete(`/api/employer/staff/${id}`);
    ElMessage.success('删除成功');
    fetchStaffList();
  } catch (error) {
    if (error === 'cancel') return;
    ElMessage.error('删除失败');
    console.error('删除员工失败:', error);
  }
};

// 搜索
const handleSearch = () => {
  pagination.value.currentPage = 1;
  fetchStaffList();
};

// 重置搜索
const handleReset = () => {
  searchQuery.value = '';
  pagination.value.currentPage = 1;
  fetchStaffList();
};

// 页码改变
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page;
  fetchStaffList();
};

// 每页条数改变
const handleSizeChange = (size) => {
  pagination.value.pageSize = size;
  pagination.value.currentPage = 1;
  fetchStaffList();
};

onMounted(() => {
  fetchStaffList();
});
</script>

<template>
  <div class="staff-management">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <h2>员工管理</h2>
          <el-button type="primary" @click="handleAddOrEdit()">
            <el-icon><Plus /></el-icon>添加员工
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索员工姓名/工号"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 员工列表 -->
      <el-table
        :data="staffList"
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="employeeId" label="工号" width="100" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '在职' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinDate" label="入职日期" width="120" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleAddOrEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="staffForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="staffForm.name" />
        </el-form-item>
        <el-form-item label="工号" prop="employeeId">
          <el-input v-model="staffForm.employeeId" />
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="staffForm.department" />
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="staffForm.position" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="staffForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="staffForm.email" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="staffForm.status">
            <el-option label="在职" value="在职" />
            <el-option label="离职" value="离职" />
            <el-option label="实习" value="实习" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期" prop="joinDate">
          <el-date-picker
            v-model="staffForm.joinDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="staffForm.role">
            <el-option label="普通员工" value="STAFF" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.staff-management {
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

.search-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-dialog__body) {
  padding-top: 10px;
}
</style> 