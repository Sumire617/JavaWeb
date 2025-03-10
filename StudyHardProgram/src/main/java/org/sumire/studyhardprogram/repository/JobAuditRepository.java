package org.sumire.studyhardprogram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sumire.studyhardprogram.model.JobAudit;

import java.util.List;

public interface JobAuditRepository extends JpaRepository<JobAudit, String> {
    List<JobAudit> findByJobPost_JobPostIdOrderByAuditTimeDesc(String jobPostId);
    JobAudit findFirstByJobPost_JobPostIdOrderByAuditTimeDesc(String jobPostId);
} 