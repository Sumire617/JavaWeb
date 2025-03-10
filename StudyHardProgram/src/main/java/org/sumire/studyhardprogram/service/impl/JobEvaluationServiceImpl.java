package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sumire.studyhardprogram.model.JobEvaluation;
import org.sumire.studyhardprogram.repository.JobEvaluationRepository;
import org.sumire.studyhardprogram.service.JobEvaluationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class JobEvaluationServiceImpl implements JobEvaluationService {

    @Autowired
    private JobEvaluationRepository jobEvaluationRepository;

    @Override
    @Transactional
    public JobEvaluation createEvaluation(JobEvaluation evaluation) {
        evaluation.setEvaluationTime(LocalDateTime.now());
        return jobEvaluationRepository.save(evaluation);
    }

    @Override
    public Page<JobEvaluation> getEvaluationsByJobPostId(String jobPostId, Pageable pageable) {
        return jobEvaluationRepository.findByJobPost_JobPostId(jobPostId, pageable);
    }

    @Override
    public Page<JobEvaluation> getEvaluationsByUserId(String userId, Pageable pageable) {
        return jobEvaluationRepository.findByUser_UserId(userId, pageable);
    }

    @Override
    public Map<String, Object> getEvaluationStats(String jobPostId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", jobEvaluationRepository.getAverageRatingByJobPostId(jobPostId));
        stats.put("totalCount", jobEvaluationRepository.getEvaluationCountByJobPostId(jobPostId));
        return stats;
    }
} 