package org.sumire.studyhardprogram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.sumire.studyhardprogram.model.JobPost;

import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, String>, JpaSpecificationExecutor<JobPost> {
    Optional<JobPost> findByJobTitle(String jobTitle);
    Page<JobPost> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
    
    @Query("SELECT COUNT(DISTINCT j.location) FROM JobPost j WHERE j.status = 'APPROVED'")
    long countDistinctLocations();
    
    @Query("SELECT COUNT(DISTINCT j.employerId) FROM JobPost j WHERE j.status = 'APPROVED'")
    long countDistinctEmployers();
}
