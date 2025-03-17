package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "job_posts")
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "job_post_id", nullable = false, length = 36)
    private String jobPostId;

    @Column(name = "employer_id")
    private String employerId;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "requirements")
    private String requirements;

    @Column(name = "salary_range")
    private String salaryRange;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "posted_at")
    private Instant postedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public void setJobPostId(String jobPostId) {
        this.jobPostId = jobPostId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPostedAt(Instant postedAt) {
        this.postedAt = postedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}