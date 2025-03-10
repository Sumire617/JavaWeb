package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// 评价记录实体类
@Entity
@Table(name = "job_evaluations")
public class JobEvaluation {
    @Id
    @Column(name = "evaluation_id", length = 36)
    private String evaluationId;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "job_post_id", length = 36)
    private String jobPostId;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    @Column(name = "evaluation_time")
    private LocalDateTime evaluationTime;

    @Column(name = "evaluation_content", length = 255)
    private String evaluationContent;

    //mappedBy 属性用于指定双向关联关系的反向端。
    @ManyToOne
    private JobPost jobPost;

    // 无参构造方法
    public JobEvaluation() {
    }

    // 全参构造方法
    public JobEvaluation(String evaluationId, String userId, String jobPostId, Integer rating, String comment, LocalDateTime evaluatedAt, LocalDateTime evaluationTime, String evaluationContent) {
        this.evaluationId = evaluationId;
        this.userId = userId;
        this.jobPostId = jobPostId;
        this.rating = rating;
        this.comment = comment;
        this.evaluatedAt = evaluatedAt;
        this.evaluationTime = evaluationTime;
        this.evaluationContent = evaluationContent;
    }

    // getter 和 setter 方法
    public String getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(String jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public LocalDateTime getEvaluationTime() {
        return evaluationTime;
    }

    public void setEvaluationTime(LocalDateTime evaluationTime) {
        this.evaluationTime = evaluationTime;
    }

    public String getEvaluationContent() {
        return evaluationContent;
    }

    public void setEvaluationContent(String evaluationContent) {
        this.evaluationContent = evaluationContent;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }
}
