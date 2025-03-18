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
import java.util.HashMap;

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
     * @param category 分类
     * @param search 搜索关键字
     * @return 已审核的工作分页列表
     */
    @GetMapping("/public")
    public ResponseEntity<Page<JobPost>> getPublicJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search
    ) {
        System.out.println("获取公开岗位列表，分类: " + category + ", 搜索关键词: " + search);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("postedAt").descending());
        
        // 将category当作jobType处理，search当作关键字处理
        Page<JobPost> publicJobs;
        if (category != null && !category.equals("all")) {
            System.out.println("按分类筛选岗位: " + category);
            publicJobs = jobPostService.findJobs(search, null, category, "APPROVED", pageRequest);
        } else {
            System.out.println("获取所有分类岗位");
            publicJobs = jobPostService.findJobs(search, null, null, "APPROVED", pageRequest);
        }
        
        System.out.println("查询结果: 总数: " + publicJobs.getTotalElements() + ", 当前页条数: " + publicJobs.getContent().size());
        
        // 打印每个岗位的类型
        publicJobs.getContent().forEach(job -> {
            System.out.println("岗位ID: " + job.getJobPostId() + ", 标题: " + job.getJobTitle() + ", 类型: " + job.getJobType());
        });
        
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

    /**
     * 获取所有可用的工作类型
     * @return 工作类型列表
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllJobTypes() {
        List<String> jobTypes = jobPostService.getAllJobTypes();
        return ResponseEntity.ok(jobTypes);
    }

    /**
     * 统计各工作类型的岗位数量
     * @return 各工作类型的岗位数量
     */
    @GetMapping("/types/stats")
    public ResponseEntity<Map<String, Long>> getJobTypeStats() {
        Map<String, Long> stats = jobPostService.countJobsByType();
        return ResponseEntity.ok(stats);
    }

    /**
     * 初始化工作类型数据
     * 对所有没有工作类型的岗位，根据标题或描述设置默认工作类型
     * @return 更新结果
     */
    @GetMapping("/init-job-types")
    public ResponseEntity<Map<String, Object>> initJobTypes() {
        List<JobPost> allJobs = jobPostService.getAllJobPosts();
        int updatedCount = 0;
        
        for (JobPost job : allJobs) {
            if (job.getJobType() == null || job.getJobType().isEmpty()) {
                String jobTitle = job.getJobTitle().toLowerCase();
                String jobType = "其他"; // 默认类型
                
                // 根据标题关键词判断类型
                if (jobTitle.contains("助教") || jobTitle.contains("辅导")) {
                    jobType = "助教辅导";
                } else if (jobTitle.contains("行政") || jobTitle.contains("助理")) {
                    jobType = "行政助理";
                } else if (jobTitle.contains("图书馆")) {
                    jobType = "图书馆";
                } else if (jobTitle.contains("研究") || jobTitle.contains("科研")) {
                    jobType = "科研助理";
                } else if (jobTitle.contains("it") || jobTitle.contains("信息") || jobTitle.contains("技术")) {
                    jobType = "信息技术";
                } else if (jobTitle.contains("服务") || jobTitle.contains("物业") || jobTitle.contains("餐厅")) {
                    jobType = "校园服务";
                } else if (jobTitle.contains("校内") || jobTitle.contains("校园")) {
                    jobType = "校内岗位";
                }
                
                // 更新工作类型
                job.setJobType(jobType);
                jobPostService.saveJobPost(job);
                updatedCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalJobs", allJobs.size());
        result.put("updatedJobs", updatedCount);
        result.put("jobTypes", jobPostService.getAllJobTypes());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取搜索建议
     * @param keyword 用户输入的关键词
     * @param limit 最大返回结果数量
     * @return 搜索建议列表
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int limit
    ) {
        System.out.println("获取搜索建议，关键词: " + keyword);
        List<String> suggestions = jobPostService.getSearchSuggestions(keyword, limit);
        return ResponseEntity.ok(suggestions);
    }
}
