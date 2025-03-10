package org.sumire.studyhardprogram.controller;

import org.sumire.studyhardprogram.model.JobPost;
import org.sumire.studyhardprogram.service.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/job-posts")
public class JobPostController {
    @Autowired
    private JobPostService jobPostService;

    // 获取所有岗位信息
    @GetMapping("/fetchAll")
    public List<JobPost> getAllJobPosts() {
        return jobPostService.getAllJobPosts();
    }

    // 根据岗位ID获取岗位信息
    @GetMapping("/{jobPostId}")
    public Optional<JobPost> getJobPostById(@PathVariable String jobPostId) {
        return jobPostService.getJobPostById(jobPostId);
    }

    // 保存岗位信息
    @PostMapping("/save")
    public ResponseEntity<JobPost> saveJobPost(@RequestBody JobPost jobPost) {
        if (jobPost.getJobPostId() == null || jobPost.getJobPostId().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            JobPost savedJobPost = jobPostService.saveJobPost(jobPost);
            return new ResponseEntity<>(savedJobPost, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 删除岗位信息
    @DeleteMapping("/{jobPostId}")
    public void deleteJobPost(@PathVariable String jobPostId) {
        jobPostService.deleteJobPost(jobPostId);
    }
}
