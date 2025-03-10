package org.sumire.studyhardprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sumire.studyhardprogram.model.JobEvaluation;
import org.sumire.studyhardprogram.service.JobEvaluationService;

import java.util.Map;

@RestController
@RequestMapping("/api/job-evaluations")
@CrossOrigin
public class JobEvaluationController {
    
    @Autowired
    private JobEvaluationService jobEvaluationService;

    @PostMapping
    public ResponseEntity<JobEvaluation> createEvaluation(@RequestBody JobEvaluation evaluation) {
        return ResponseEntity.ok(jobEvaluationService.createEvaluation(evaluation));
    }

    @GetMapping("/{jobPostId}")
    public ResponseEntity<Page<JobEvaluation>> getEvaluationsByJobPostId(
            @PathVariable String jobPostId,
            Pageable pageable) {
        return ResponseEntity.ok(jobEvaluationService.getEvaluationsByJobPostId(jobPostId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<JobEvaluation>> getEvaluationsByUserId(
            @PathVariable String userId,
            Pageable pageable) {
        return ResponseEntity.ok(jobEvaluationService.getEvaluationsByUserId(userId, pageable));
    }

    @GetMapping("/stats/{jobPostId}")
    public ResponseEntity<Map<String, Object>> getEvaluationStats(@PathVariable String jobPostId) {
        return ResponseEntity.ok(jobEvaluationService.getEvaluationStats(jobPostId));
    }
} 