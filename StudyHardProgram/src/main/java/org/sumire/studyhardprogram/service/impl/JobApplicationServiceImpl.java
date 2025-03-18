package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sumire.studyhardprogram.model.JobApplication;
import org.sumire.studyhardprogram.model.JobPost;
import org.sumire.studyhardprogram.model.User;
import org.sumire.studyhardprogram.repository.JobApplicationRepository;
import org.sumire.studyhardprogram.repository.JobPostRepository;
import org.sumire.studyhardprogram.repository.UserRepository;
import org.sumire.studyhardprogram.service.JobApplicationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public JobApplication createApplication(JobApplication application) {
        try {
            // 检查是否已经申请过
            boolean hasApplied = jobApplicationRepository.existsByJobPost_JobPostIdAndUser_UserId(
                    application.getJobPost().getJobPostId(),
                    application.getUser().getUserId());
            if (hasApplied) {
                throw new RuntimeException("您已经申请过这个职位了");
            }

            // 设置申请ID
            application.setApplicationId(UUID.randomUUID().toString());
            
            // 查找并设置JobPost
            String jobPostId = application.getJobPost().getJobPostId();
            JobPost jobPost = jobPostRepository.findById(jobPostId)
                    .orElseThrow(() -> new RuntimeException("职位不存在"));
            application.setJobPost(jobPost);
            
            // 查找并设置User
            String userId = application.getUser().getUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            application.setUser(user);
            
            // 设置申请时间和初始状态
            application.setApplyTime(LocalDateTime.now());
            application.setStatus(JobApplication.STATUS_PENDING);
            
            return jobApplicationRepository.save(application);
        } catch (Exception e) {
            throw new RuntimeException("创建申请失败: " + e.getMessage());
        }
    }

    @Override
    public Page<JobApplication> getApplicationsByJobPostId(String jobPostId, Pageable pageable) {
        return jobApplicationRepository.findByJobPost_JobPostId(jobPostId, pageable);
    }

    @Override
    public Page<JobApplication> getApplicationsByUserId(String userId, Pageable pageable) {
        try {
            System.out.println("Service - Getting applications for user: " + userId);
            
            // 验证用户是否存在
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + userId));
            
            Page<JobApplication> applications = jobApplicationRepository.findByUser_UserId(userId, pageable);
            System.out.println("Service - Found " + applications.getTotalElements() + " applications");
            
            return applications;
        } catch (Exception e) {
            System.err.println("Service - Error getting applications for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("获取用户申请列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public JobApplication updateApplicationStatus(String applicationId, String status, String reviewComment) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        application.setReviewInfo(status, reviewComment, "ADMIN"); // TODO: 从安全上下文获取实际的管理员ID
        return jobApplicationRepository.save(application);
    }

    @Override
    public boolean canUserReview(String jobPostId, String userId) {
        return jobApplicationRepository.existsByJobPostAndUserAndStatus(
                jobPostId, userId, JobApplication.STATUS_APPROVED);
    }
    
    @Override
    public Optional<JobApplication> getApplicationById(String applicationId) {
        return jobApplicationRepository.findById(applicationId);
    }
    
    @Override
    public Page<JobApplication> getApplicationsByJobPostIdAndStatus(String jobPostId, String status, Pageable pageable) {
        return jobApplicationRepository.findByJobPostIdAndStatus(jobPostId, status, pageable);
    }

    @Override
    public Map<String, Object> getJobApplicationInfo(String jobPostId, String userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查职位是否存在
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new RuntimeException("Job post not found"));
        
        // 如果提供了用户ID，检查用户是否已经申请过
        if (userId != null) {
            boolean hasApplied = jobApplicationRepository.existsByJobPost_JobPostIdAndUser_UserId(jobPostId, userId);
            result.put("hasApplied", hasApplied);
            
            if (hasApplied) {
                Optional<JobApplication> application = jobApplicationRepository.findByJobPost_JobPostIdAndUser_UserId(jobPostId, userId);
                application.ifPresent(app -> {
                    result.put("applicationStatus", app.getStatus());
                    result.put("applicationId", app.getApplicationId());
                });
            }
        }
        
        // 获取申请统计信息
        long totalApplications = jobApplicationRepository.countByJobPost_JobPostId(jobPostId);
        long pendingApplications = jobApplicationRepository.countByJobPost_JobPostIdAndStatus(jobPostId, JobApplication.STATUS_PENDING);
        long approvedApplications = jobApplicationRepository.countByJobPost_JobPostIdAndStatus(jobPostId, JobApplication.STATUS_APPROVED);
        long rejectedApplications = jobApplicationRepository.countByJobPost_JobPostIdAndStatus(jobPostId, JobApplication.STATUS_REJECTED);
        
        result.put("totalApplications", totalApplications);
        result.put("pendingApplications", pendingApplications);
        result.put("approvedApplications", approvedApplications);
        result.put("rejectedApplications", rejectedApplications);
        
        return result;
    }
} 