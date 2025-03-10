package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_posts")
public class JobPost {
    @Id
    @Column(name = "job_post_id")
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
    private LocalDateTime postedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 关联审核记录
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobAudit> audits;

    // 关联评价记录
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobEvaluation> evaluations;

    // 无参构造方法
    public JobPost() {
    }

    // 全参构造方法
    public JobPost(String jobPostId, String employerId, String jobTitle, String jobDescription, String requirements, String salaryRange, String location, String status, LocalDateTime postedAt, LocalDateTime updatedAt, List<JobAudit> audits, List<JobEvaluation> evaluations) {
        this.jobPostId = jobPostId;
        this.employerId = employerId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.requirements = requirements;
        this.salaryRange = salaryRange;
        this.location = location;
        this.status = status;
        this.postedAt = postedAt;
        this.updatedAt = updatedAt;
        this.audits = audits;
        this.evaluations = evaluations;
    }

    // getter 和 setter 方法
    public String getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(String jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<JobAudit> getAudits() {
        return audits;
    }

    public void setAudits(List<JobAudit> audits) {
        this.audits = audits;
    }

    public List<JobEvaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<JobEvaluation> evaluations) {
        this.evaluations = evaluations;
    }
}
