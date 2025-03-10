package org.sumire.studyhardprogram.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sumire.studyhardprogram.model.JobApplication;

public interface JobApplicationService {
    JobApplication createApplication(JobApplication application);
    
    Page<JobApplication> getApplicationsByJobPostId(String jobPostId, Pageable pageable);
    
    Page<JobApplication> getApplicationsByUserId(String userId, Pageable pageable);
    
    JobApplication updateApplicationStatus(String applicationId, String status, String reviewComment);
    
    boolean canUserReview(String jobPostId, String userId);
} 