package org.sumire.studyhardprogram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sumire.studyhardprogram.model.JobApplication;

import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {
    Page<JobApplication> findByJobPost_JobPostId(String jobPostId, Pageable pageable);
    
    @Query("SELECT a FROM JobApplication a WHERE a.user.userId = :userId")
    Page<JobApplication> findByUser_UserId(@Param("userId") String userId, Pageable pageable);
    
    @Query("SELECT COUNT(a) > 0 FROM JobApplication a WHERE a.jobPost.jobPostId = :jobPostId AND a.user.userId = :userId AND a.status = :status")
    boolean existsByJobPostAndUserAndStatus(@Param("jobPostId") String jobPostId, 
                                          @Param("userId") String userId, 
                                          @Param("status") String status);

    // 添加更多便利查询方法
    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.jobPost.jobPostId = :jobPostId AND a.status = :status")
    long countByJobPostIdAndStatus(@Param("jobPostId") String jobPostId, @Param("status") String status);

    @Query("SELECT a FROM JobApplication a WHERE a.jobPost.jobPostId = :jobPostId AND a.status = :status")
    Page<JobApplication> findByJobPostIdAndStatus(@Param("jobPostId") String jobPostId, 
                                                @Param("status") String status, 
                                                Pageable pageable);

    boolean existsByJobPost_JobPostIdAndUser_UserId(String jobPostId, String userId);
    
    Optional<JobApplication> findByJobPost_JobPostIdAndUser_UserId(String jobPostId, String userId);
    
    long countByJobPost_JobPostId(String jobPostId);
    
    long countByJobPost_JobPostIdAndStatus(String jobPostId, String status);
} 