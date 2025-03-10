package org.sumire.studyhardprogram.service;

import org.sumire.studyhardprogram.model.JobAudit;
import java.util.List;

public interface JobAuditService {
    List<JobAudit> getAuditsByJobPostId(String jobPostId);
    JobAudit saveAudit(JobAudit jobAudit);
    JobAudit getLatestAuditByJobPostId(String jobPostId);
} 