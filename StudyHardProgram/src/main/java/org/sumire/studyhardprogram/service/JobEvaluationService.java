package org.sumire.studyhardprogram.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sumire.studyhardprogram.model.JobEvaluation;

import java.util.Map;

public interface JobEvaluationService {
    JobEvaluation createEvaluation(JobEvaluation evaluation);
    
    Page<JobEvaluation> getEvaluationsByJobPostId(String jobPostId, Pageable pageable);
    
    Page<JobEvaluation> getEvaluationsByUserId(String userId, Pageable pageable);
    
    Map<String, Object> getEvaluationStats(String jobPostId);
} 