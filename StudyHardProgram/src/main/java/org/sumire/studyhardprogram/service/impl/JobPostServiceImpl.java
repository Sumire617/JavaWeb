package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.sumire.studyhardprogram.model.JobPost;
import org.sumire.studyhardprogram.repository.JobPostRepository;
import org.sumire.studyhardprogram.service.JobPostService;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobPostServiceImpl implements JobPostService {

    @Autowired
    private JobPostRepository jobPostRepository;

    @Override
    public List<JobPost> getAllJobPosts() {
        return jobPostRepository.findAll();
    }

    @Override
    public Optional<JobPost> getJobPostById(String jobPostId) {
        return jobPostRepository.findById(jobPostId);
    }

    @Override
    public Optional<JobPost> getJobPostByTitle(String jobPostTitle) {
        return jobPostRepository.findByJobTitle(jobPostTitle);
    }

    @Override
    public JobPost saveJobPost(JobPost jobPost) {
        return jobPostRepository.save(jobPost);
    }

    @Override
    public void deleteJobPost(String jobPostId) {
        jobPostRepository.deleteById(jobPostId);
    }

    @Override
    public JobPost editJobPost(JobPost jobPost) {
        return jobPostRepository.save(jobPost);
    }

    @Override
    public Page<JobPost> findJobs(String keyword, String location, String jobType, String status, Pageable pageable) {
        System.out.println("JobPostService - findJobs - 参数: keyword=" + keyword + ", location=" + location 
                + ", jobType=" + jobType + ", status=" + status);
        
        // 如果只需要按工作类型和状态筛选，使用专用方法
        if (jobType != null && !jobType.isEmpty() && keyword == null && location == null) {
            System.out.println("JobPostService - 使用findByJobTypeAndStatus方法");
            return jobPostRepository.findByJobTypeAndStatus(jobType, status, pageable);
        }
        
        // 更复杂的查询使用Specification
        Specification<JobPost> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                    cb.like(root.get("jobTitle"), "%" + keyword + "%"),
                    cb.like(root.get("jobDescription"), "%" + keyword + "%"),
                    cb.like(root.get("requirements"), "%" + keyword + "%")
                ));
            }
            
            if (location != null && !location.isEmpty()) {
                predicates.add(cb.like(root.get("location"), "%" + location + "%"));
            }
            
            if (jobType != null && !jobType.isEmpty()) {
                System.out.println("JobPostService - 添加jobType筛选条件: " + jobType);
                predicates.add(cb.equal(root.get("jobType"), jobType));
            }

            if (status != null && !status.isEmpty() && !status.equals("all")) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            System.out.println("JobPostService - 构建了 " + predicates.size() + " 个筛选条件");
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<JobPost> result = jobPostRepository.findAll(spec, pageable);
        System.out.println("JobPostService - 查询结果: 总数=" + result.getTotalElements());
        return result;
    }

    @Override
    public List<JobPost> findLatestJobs() {
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("postedAt").descending());
        Page<JobPost> latestJobs = jobPostRepository.findByStatus("APPROVED", pageRequest);
        return latestJobs.getContent();
    }

    @Override
    public Map<String, Object> getJobStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", jobPostRepository.countByStatus("APPROVED"));
        stats.put("totalLocations", jobPostRepository.countDistinctLocations());
        stats.put("totalCompanies", jobPostRepository.countDistinctEmployers());
        return stats;
    }

    @Override
    public JobPost getJobById(String jobId) {
        return jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    @Override
    public JobPost createJob(JobPost jobPost) {
        jobPost.setStatus("PENDING");
        
        if (jobPost.getJobType() == null || jobPost.getJobType().isEmpty()) {
            jobPost.setJobType("其他");
        }
        
        return jobPostRepository.save(jobPost);
    }

    @Override
    public JobPost updateJob(String jobId, JobPost jobPost) {
        JobPost existingJob = getJobById(jobId);
        existingJob.setEmployerId(jobPost.getEmployerId());
        existingJob.setJobTitle(jobPost.getJobTitle());
        existingJob.setJobDescription(jobPost.getJobDescription());
        existingJob.setRequirements(jobPost.getRequirements());
        existingJob.setSalaryRange(jobPost.getSalaryRange());
        existingJob.setLocation(jobPost.getLocation());
        
        String jobType = jobPost.getJobType();
        if (jobType != null && !jobType.isEmpty()) {
            existingJob.setJobType(jobType);
        } else if (existingJob.getJobType() == null || existingJob.getJobType().isEmpty()) {
            existingJob.setJobType("其他");
        }
        
        existingJob.setStatus(jobPost.getStatus());
        existingJob.setUpdatedAt(java.time.Instant.now());
        return jobPostRepository.save(existingJob);
    }

    @Override
    public void deleteJob(String jobId) {
        jobPostRepository.deleteById(jobId);
    }

    @Override
    public Page<JobPost> findPendingJobs(Pageable pageable) {
        return jobPostRepository.findByStatus("PENDING", pageable);
    }

    @Override
    public List<String> getAllJobTypes() {
        return jobPostRepository.findAllJobTypes();
    }

    @Override
    public Map<String, Long> countJobsByType() {
        List<Object[]> results = jobPostRepository.countJobsByType();
        Map<String, Long> jobTypeCount = new HashMap<>();
        
        for (Object[] result : results) {
            String jobType = (String) result[0];
            if (jobType == null || jobType.isEmpty()) {
                jobType = "其他";
            }
            Long count = ((Number) result[1]).longValue();
            jobTypeCount.put(jobType, count);
        }
        
        return jobTypeCount;
    }

    @Override
    public List<String> getSearchSuggestions(String keyword, int limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        keyword = keyword.trim();
        
        // 获取各种类型的建议
        List<String> titleSuggestions = jobPostRepository.findJobTitleSuggestions(keyword);
        List<String> locationSuggestions = jobPostRepository.findLocationSuggestions(keyword);
        List<String> typeSuggestions = jobPostRepository.findJobTypeSuggestions(keyword);
        
        // 合并所有建议
        Set<String> uniqueSuggestions = new LinkedHashSet<>();
        uniqueSuggestions.addAll(titleSuggestions);
        uniqueSuggestions.addAll(locationSuggestions);
        uniqueSuggestions.addAll(typeSuggestions);
        
        // 转换为List并限制数量
        return uniqueSuggestions.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
} 