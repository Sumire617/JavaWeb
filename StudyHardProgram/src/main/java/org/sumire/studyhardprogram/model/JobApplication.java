package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id
    @Column(name = "application_id", nullable = false, length = 36)
    private String applicationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "student_id", nullable = false, length = 20)
    private String studentId;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Lob
    @Column(name = "introduction")
    private String introduction;

    @Column(name = "resume")
    private String resume;

    @Column(name = "apply_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime applyTime;

    @ColumnDefault("'PENDING'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Lob
    @Column(name = "review_comment")
    private String reviewComment;

    @Column(name = "review_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime reviewTime;

    @Column(name = "reviewer_id", length = 36)
    private String reviewerId;

    // 添加构造函数用于接收前端数据
    public JobApplication(String jobPostId, String userId, String name, String studentId, 
                         String phone, String email) {
        // 创建并设置JobPost
        JobPost jobPost = new JobPost();
        jobPost.setJobPostId(jobPostId);
        this.jobPost = jobPost;
        
        // 创建并设置User
        User user = new User();
        user.setUserId(userId);
        this.user = user;
        
        // 设置其他字段
        this.name = name;
        this.studentId = studentId;
        this.phone = phone;
        this.email = email;
        this.introduction = ""; // 设置默认值
        this.applyTime = LocalDateTime.now();
        this.status = STATUS_PENDING;
    }

    // 添加状态常量
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    // 添加便利方法
    public void setInitialApplicationState() {
        this.applyTime = LocalDateTime.now();
        this.status = STATUS_PENDING;
    }

    public void setReviewInfo(String status, String reviewComment, String reviewerId) {
        this.status = status;
        this.reviewComment = reviewComment;
        this.reviewerId = reviewerId;
        this.reviewTime = LocalDateTime.now();
    }
}