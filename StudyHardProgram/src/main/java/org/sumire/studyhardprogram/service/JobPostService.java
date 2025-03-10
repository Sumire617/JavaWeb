package org.sumire.studyhardprogram.service;

import org.sumire.studyhardprogram.model.JobPost;
import org.sumire.studyhardprogram.repository.JobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobPostService {
    @Autowired
    private JobPostRepository jobPostRepository;

    // 获取所有岗位信息
    public List<JobPost> getAllJobPosts() {
        return jobPostRepository.findAll();
    }

    // 根据岗位ID获取岗位信息
    public Optional<JobPost> getJobPostById(String jobPostId) {
        return jobPostRepository.findById(jobPostId);
    }

    // 保存岗位信息
    public JobPost saveJobPost(JobPost jobPost) {
        return jobPostRepository.save(jobPost);
    }

    // 删除岗位信息
    public void deleteJobPost(String jobPostId) {
        jobPostRepository.deleteById(jobPostId);
    }
}
