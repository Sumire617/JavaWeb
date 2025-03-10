<template>
  <div class="job-list-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索职位名称、描述或要求"
        prefix-icon="el-icon-search"
        @input="handleSearch"
        class="search-input"
      />
      <el-select
        v-model="selectedLocation"
        placeholder="选择地区"
        clearable
        @change="handleSearch"
        class="location-select"
      >
        <el-option
          v-for="location in locations"
          :key="location"
          :label="location"
          :value="location"
        />
      </el-select>
      <el-select
        v-model="selectedJobType"
        placeholder="工作类型"
        clearable
        @change="handleSearch"
        class="job-type-select"
      >
        <el-option label="全职" value="全职" />
        <el-option label="实习" value="实习" />
        <el-option label="兼职" value="兼职" />
      </el-select>
    </div>

    <!-- 统计信息 -->
    <div class="stats-bar">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stat-item">
              <i class="el-icon-suitcase"></i>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalJobs }}</div>
                <div class="stat-label">在招职位</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stat-item">
              <i class="el-icon-location"></i>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalLocations }}</div>
                <div class="stat-label">覆盖城市</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stat-item">
              <i class="el-icon-office-building"></i>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalCompanies }}</div>
                <div class="stat-label">合作企业</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 岗位列表 -->
    <div class="job-cards">
      <el-card v-for="job in jobs" :key="job.jobPostId" class="job-card" shadow="hover">
        <div class="job-header">
          <h3 class="job-title">{{ job.jobTitle }}</h3>
          <div class="job-salary">{{ job.salaryRange }}</div>
        </div>
        <div class="job-info">
          <span class="job-location">
            <i class="el-icon-location"></i>
            {{ job.location }}
          </span>
          <span class="job-type">
            <i class="el-icon-time"></i>
            {{ job.jobType }}
          </span>
          <span class="job-department">
            <i class="el-icon-office-building"></i>
            {{ job.department }}
          </span>
        </div>
        <div class="job-description">{{ job.jobDescription }}</div>
        <div class="job-footer">
          <el-button type="primary" size="small" @click="viewJobDetails(job)">查看详情</el-button>
          <el-button type="success" size="small" @click="applyJob(job)">立即申请</el-button>
        </div>
      </el-card>
    </div>

    <!-- 分页器 -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page.sync="currentPage"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'JobList',
  data() {
    return {
      jobs: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchKeyword: '',
      selectedLocation: '',
      selectedJobType: '',
      locations: [],
      stats: {
        totalJobs: 0,
        totalLocations: 0,
        totalCompanies: 0
      }
    };
  },
  created() {
    this.fetchJobs();
    this.fetchStats();
  },
  methods: {
    async fetchJobs() {
      try {
        const response = await axios.get('/api/jobs', {
          params: {
            page: this.currentPage - 1,
            size: this.pageSize,
            keyword: this.searchKeyword,
            location: this.selectedLocation,
            jobType: this.selectedJobType
          }
        });
        this.jobs = response.data.content;
        this.total = response.data.totalElements;
      } catch (error) {
        console.error('获取岗位列表失败:', error);
        this.$message.error('获取岗位列表失败');
      }
    },
    async fetchStats() {
      try {
        const response = await axios.get('/api/jobs/stats');
        this.stats = response.data;
      } catch (error) {
        console.error('获取统计数据失败:', error);
      }
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchJobs();
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.fetchJobs();
    },
    viewJobDetails(job) {
      this.$router.push(`/jobs/${job.jobPostId}`);
    },
    applyJob(job) {
      // 检查用户是否登录
      if (!this.$store.state.user) {
        this.$message.warning('请先登录后再申请职位');
        this.$router.push('/login');
        return;
      }
      this.$router.push(`/jobs/${job.jobPostId}/apply`);
    }
  }
};
</script>

<style scoped>
.job-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.search-input {
  flex: 2;
}

.location-select,
.job-type-select {
  flex: 1;
}

.stats-bar {
  margin-bottom: 30px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-item i {
  font-size: 40px;
  color: #409EFF;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.job-cards {
  display: grid;
  gap: 20px;
  margin-bottom: 30px;
}

.job-card {
  transition: all 0.3s ease;
}

.job-card:hover {
  transform: translateY(-5px);
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.job-title {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.job-salary {
  color: #F56C6C;
  font-weight: bold;
}

.job-info {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
}

.job-info i {
  margin-right: 5px;
}

.job-description {
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.job-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style> 