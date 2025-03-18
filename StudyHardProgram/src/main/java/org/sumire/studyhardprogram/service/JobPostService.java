package org.sumire.studyhardprogram.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sumire.studyhardprogram.model.JobPost;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JobPostService {
    List<JobPost> getAllJobPosts();
    
    Optional<JobPost> getJobPostById(String jobPostId);
    
    Optional<JobPost> getJobPostByTitle(String jobPostTitle);
    
    JobPost saveJobPost(JobPost jobPost);
    
    void deleteJobPost(String jobPostId);
    
    JobPost editJobPost(JobPost jobPost);
    
    Page<JobPost> findJobs(String keyword, String location, String jobType, String status, Pageable pageable);
    
    List<JobPost> findLatestJobs();
    
    Map<String, Object> getJobStats();
    
    JobPost getJobById(String jobId);
    
    JobPost createJob(JobPost jobPost);
    
    JobPost updateJob(String jobId, JobPost jobPost);
    
    void deleteJob(String jobId);
    
    /**
     * 获取待审核的岗位列表
     * @param pageable 分页参数
     * @return 待审核岗位分页列表
     */
    Page<JobPost> findPendingJobs(Pageable pageable);
    
    /**
     * 获取所有可用的工作类型
     * @return 工作类型列表
     */
    List<String> getAllJobTypes();
    
    /**
     * 统计各工作类型的岗位数量
     * @return 工作类型和对应的岗位数量
     */
    Map<String, Long> countJobsByType();
    
    /**
     * 获取搜索建议
     * @param keyword 关键词
     * @param limit 限制返回数量
     * @return 搜索建议列表
     */
    List<String> getSearchSuggestions(String keyword, int limit);
}
