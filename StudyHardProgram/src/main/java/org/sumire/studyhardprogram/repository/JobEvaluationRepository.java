package org.sumire.studyhardprogram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.sumire.studyhardprogram.model.JobEvaluation;

public interface JobEvaluationRepository extends JpaRepository<JobEvaluation, String> {
    Page<JobEvaluation> findByJobPost_JobPostId(String jobPostId, Pageable pageable);
    Page<JobEvaluation> findByUser_UserId(String userId, Pageable pageable);
    
    @Query("SELECT AVG(e.rating) FROM JobEvaluation e WHERE e.jobPost.jobPostId = ?1")
    Double getAverageRatingByJobPostId(String jobPostId);
    
    @Query("SELECT COUNT(e) FROM JobEvaluation e WHERE e.jobPost.jobPostId = ?1")
    Long getEvaluationCountByJobPostId(String jobPostId);
} 