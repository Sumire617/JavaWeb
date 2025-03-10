package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sumire.studyhardprogram.model.JobAudit;
import org.sumire.studyhardprogram.repository.JobAuditRepository;
import org.sumire.studyhardprogram.service.JobAuditService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class JobAuditServiceImpl implements JobAuditService {

    @Autowired
    private JobAuditRepository jobAuditRepository;

    @Override
    public List<JobAudit> getAuditsByJobPostId(String jobPostId) {
        return jobAuditRepository.findByJobPost_JobPostIdOrderByAuditTimeDesc(jobPostId);
    }

    @Override
    @Transactional
    public JobAudit saveAudit(JobAudit jobAudit) {
        // 设置审核ID
        jobAudit.setAuditId(UUID.randomUUID().toString());
        // 设置审核时间
        if (jobAudit.getAuditTime() == null) {
            jobAudit.setAuditTime(LocalDateTime.now());
        }
        return jobAuditRepository.save(jobAudit);
    }

    @Override
    public JobAudit getLatestAuditByJobPostId(String jobPostId) {
        return jobAuditRepository.findFirstByJobPost_JobPostIdOrderByAuditTimeDesc(jobPostId);
    }
} 