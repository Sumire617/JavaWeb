package org.sumire.studyhardprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sumire.studyhardprogram.model.JobApplication;
import org.sumire.studyhardprogram.service.JobApplicationService;

import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin
public class JobApplicationController {
    
    @Autowired
    private JobApplicationService jobApplicationService;

    @PostMapping
    public ResponseEntity<JobApplication> createApplication(@RequestBody JobApplication application) {
        return ResponseEntity.ok(jobApplicationService.createApplication(application));
    }

    @GetMapping("/job/{jobPostId}")
    public ResponseEntity<Page<JobApplication>> getApplicationsByJobPostId(
            @PathVariable String jobPostId,
            Pageable pageable) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByJobPostId(jobPostId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<JobApplication>> getApplicationsByUserId(
            @PathVariable String userId,
            Pageable pageable) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByUserId(userId, pageable));
    }

    @PatchMapping("/{applicationId}")
    public ResponseEntity<JobApplication> updateApplicationStatus(
            @PathVariable String applicationId,
            @RequestBody Map<String, String> statusUpdate) {
        return ResponseEntity.ok(jobApplicationService.updateApplicationStatus(
                applicationId,
                statusUpdate.get("status"),
                statusUpdate.get("reviewComment")));
    }

    @GetMapping("/check-review/{jobPostId}")
    public ResponseEntity<Map<String, Boolean>> checkCanReview(
            @PathVariable String jobPostId,
            @RequestParam String userId) {
        boolean canReview = jobApplicationService.canUserReview(jobPostId, userId);
        return ResponseEntity.ok(Map.of("canReview", canReview));
    }
} 