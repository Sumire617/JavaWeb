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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                predicates.add(cb.equal(root.get("jobType"), jobType));
            }

            if (status != null && !status.isEmpty() && !status.equals("all")) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return jobPostRepository.findAll(spec, pageable);
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
} 