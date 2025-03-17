package org.sumire.studyhardprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sumire.studyhardprogram.model.JobPost;
import org.sumire.studyhardprogram.service.JobPostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin
public class JobPostController {

    @Autowired
    private JobPostService jobPostService;

    @GetMapping
    public ResponseEntity<Page<JobPost>> getJobList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String status
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("postedAt").descending());
        Page<JobPost> jobs = jobPostService.findJobs(keyword, location, jobType, status, pageRequest);
        return ResponseEntity
                .ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(jobs);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<JobPost>> getLatestJobs() {
        List<JobPost> latestJobs = jobPostService.findLatestJobs();
        return ResponseEntity.ok(latestJobs);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getJobStats() {
        Map<String, Object> stats = jobPostService.getJobStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/{jobId}")
    public ResponseEntity<JobPost> getJobById(@PathVariable String jobId) {
        JobPost job = jobPostService.getJobById(jobId);
        return ResponseEntity.ok(job);
    }
    
    @PostMapping
    public ResponseEntity<JobPost> createJob(@RequestBody JobPost jobPost) {
        JobPost createdJob = jobPostService.createJob(jobPost);
        return ResponseEntity.ok(createdJob);
    }
    
    @PutMapping("/{jobId}")
    public ResponseEntity<JobPost> updateJob(@PathVariable String jobId, @RequestBody JobPost jobPost) {
        JobPost updatedJob = jobPostService.updateJob(jobId, jobPost);
        return ResponseEntity.ok(updatedJob);
    }
    
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable String jobId) {
        jobPostService.deleteJob(jobId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取待审核的岗位列表
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 待审核岗位分页列表
     */
    @GetMapping("/pending")
    public ResponseEntity<Page<JobPost>> getPendingJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("postedAt").descending());
        Page<JobPost> pendingJobs = jobPostService.findPendingJobs(pageRequest);
        return ResponseEntity.ok(pendingJobs);
    }

    /**
     * 获取已审核的公开工作列表
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 已审核的工作分页列表
     */
    @GetMapping("/public")
    public ResponseEntity<Page<JobPost>> getPublicJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("postedAt").descending());
        Page<JobPost> publicJobs = jobPostService.findJobs(null, null, null, "APPROVED", pageRequest);
        return ResponseEntity.ok(publicJobs);
    }
    
    /**
     * 关闭岗位
     * @param jobId 岗位ID
     * @return 操作结果
     */
    @PatchMapping("/{jobId}/close")
    public ResponseEntity<JobPost> closeJob(@PathVariable String jobId) {
        JobPost job = jobPostService.getJobById(jobId);
        job.setStatus("CLOSED");
        job.setUpdatedAt(java.time.Instant.now());
        JobPost updatedJob = jobPostService.updateJob(jobId, job);
        return ResponseEntity.ok(updatedJob);
    }
    
    /**
     * 为雇主创建关闭岗位端点
     */
    @PatchMapping("/employer/jobs/{jobId}/close")
    public ResponseEntity<JobPost> employerCloseJob(@PathVariable String jobId) {
        return closeJob(jobId);
    }
    
    /**
     * 为雇主创建删除岗位端点
     */
    @DeleteMapping("/employer/jobs/{jobId}")
    public ResponseEntity<Void> employerDeleteJob(@PathVariable String jobId) {
        jobPostService.deleteJob(jobId);
        return ResponseEntity.ok().build();
    }
}
