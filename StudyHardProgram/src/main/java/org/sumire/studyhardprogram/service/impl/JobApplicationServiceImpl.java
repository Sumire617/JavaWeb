package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sumire.studyhardprogram.model.JobApplication;
import org.sumire.studyhardprogram.repository.JobApplicationRepository;
import org.sumire.studyhardprogram.service.JobApplicationService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Override
    @Transactional
    public JobApplication createApplication(JobApplication application) {
        // 设置申请ID
        application.setApplicationId(UUID.randomUUID().toString());
        // 设置申请时间和初始状态
        application.setInitialApplicationState();
        return jobApplicationRepository.save(application);
    }

    @Override
    public Page<JobApplication> getApplicationsByJobPostId(String jobPostId, Pageable pageable) {
        return jobApplicationRepository.findByJobPost_JobPostId(jobPostId, pageable);
    }

    @Override
    public Page<JobApplication> getApplicationsByUserId(String userId, Pageable pageable) {
        return jobApplicationRepository.findByUser_UserId(userId, pageable);
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
} 