package org.sumire.studyhardprogram.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sumire.studyhardprogram.model.JobApplication;

import java.util.Map;
import java.util.Optional;

public interface JobApplicationService {
    JobApplication createApplication(JobApplication application);
    
    Page<JobApplication> getApplicationsByJobPostId(String jobPostId, Pageable pageable);
    
    Page<JobApplication> getApplicationsByUserId(String userId, Pageable pageable);
    
    JobApplication updateApplicationStatus(String applicationId, String status, String reviewComment);
    
    boolean canUserReview(String jobPostId, String userId);
    
    // 新增方法 - 通过ID获取单个申请详情
    Optional<JobApplication> getApplicationById(String applicationId);
    
    // 新增方法 - 按岗位ID和状态查询申请
    Page<JobApplication> getApplicationsByJobPostIdAndStatus(String jobPostId, String status, Pageable pageable);
    
    // 新增方法 - 获取职位申请信息
    Map<String, Object> getJobApplicationInfo(String jobPostId, String userId);
} 