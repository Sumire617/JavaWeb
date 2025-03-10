package org.sumire.studyhardprogram.repository;

import org.sumire.studyhardprogram.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostRepository extends JpaRepository<JobPost, String> {
}
