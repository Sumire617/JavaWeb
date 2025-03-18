package org.sumire.studyhardprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sumire.studyhardprogram.model.JobApplication;
import org.sumire.studyhardprogram.service.JobApplicationService;
import org.sumire.studyhardprogram.dto.JobApplicationDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin
public class JobApplicationController {
    
    @Autowired
    private JobApplicationService jobApplicationService;

    @PostMapping
    public ResponseEntity<?> createApplication(@RequestBody JobApplicationDTO dto) {
        try {
            JobApplication application = new JobApplication(
                dto.getJobPostId(),
                dto.getUserId(),
                dto.getName(),
                dto.getStudentId(),
                dto.getPhone(),
                dto.getEmail()
            );
            application.setIntroduction(dto.getIntroduction());
            
            JobApplication savedApplication = jobApplicationService.createApplication(application);
            return ResponseEntity.ok(savedApplication);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器内部错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/job/{jobPostId}")
    public ResponseEntity<Page<JobApplication>> getApplicationsByJobPostId(
            @PathVariable String jobPostId,
            Pageable pageable) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByJobPostId(jobPostId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getApplicationsByUserId(
            @PathVariable String userId,
            Pageable pageable) {
        try {
            System.out.println("Getting applications for user: " + userId);
            System.out.println("Pageable: " + pageable);
            
            Page<JobApplication> applications = jobApplicationService.getApplicationsByUserId(userId, pageable);
            
            System.out.println("Found " + applications.getTotalElements() + " applications");
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error getting applications for user " + userId + ": " + e.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("error", "获取申请列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/jobs/{jobPostId}/apply")
    public ResponseEntity<Map<String, Object>> getJobApplicationInfo(
            @PathVariable String jobPostId,
            @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(jobApplicationService.getJobApplicationInfo(jobPostId, userId));
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
    
    // 为EMPLOYER模块添加查看应聘者详细信息的API
    @GetMapping("/{applicationId}")
    public ResponseEntity<JobApplication> getApplicationById(
            @PathVariable String applicationId,
            @RequestParam(required = false) String employerId) {
        Optional<JobApplication> application = jobApplicationService.getApplicationById(applicationId);
        return application.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 为EMPLOYER模块添加按状态筛选应聘者的API
    @GetMapping("/employer/job/{jobPostId}/status/{status}")
    public ResponseEntity<Page<JobApplication>> getApplicationsByJobPostIdAndStatus(
            @PathVariable String jobPostId,
            @PathVariable String status,
            @RequestParam String employerId,
            Pageable pageable) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByJobPostIdAndStatus(jobPostId, status, pageable));
    }
} 