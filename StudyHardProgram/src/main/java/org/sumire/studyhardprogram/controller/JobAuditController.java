package org.sumire.studyhardprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sumire.studyhardprogram.model.JobAudit;
import org.sumire.studyhardprogram.service.JobAuditService;

import java.util.List;

@RestController
@RequestMapping("/api/job-audits")
@CrossOrigin
public class JobAuditController {
    
    @Autowired
    private JobAuditService jobAuditService;

    @GetMapping("/{jobPostId}")
    public ResponseEntity<List<JobAudit>> getAuditsByJobPostId(@PathVariable String jobPostId) {
        List<JobAudit> audits = jobAuditService.getAuditsByJobPostId(jobPostId);
        return ResponseEntity.ok(audits);
    }

    @PostMapping
    public ResponseEntity<JobAudit> createAudit(@RequestBody JobAudit jobAudit) {
        JobAudit savedAudit = jobAuditService.saveAudit(jobAudit);
        return ResponseEntity.ok(savedAudit);
    }

    @GetMapping("/latest/{jobPostId}")
    public ResponseEntity<JobAudit> getLatestAudit(@PathVariable String jobPostId) {
        JobAudit latestAudit = jobAuditService.getLatestAuditByJobPostId(jobPostId);
        return ResponseEntity.ok(latestAudit);
    }
} 