package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

//审核实体类
@Entity
@Table(name = "job_audits")
public class JobAudit {
    @Id
    @Column(name = "audit_id") // 与数据库表的主键字段名保持一致
    private String auditId;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column(name = "audit_result")
    private String auditResult;

    @Column(name = "admin_id") // 添加管理员ID字段
    private String adminId;

    @Column(name = "is_approved") // 添加审核通过标志字段
    private Integer isApproved;

    @Column(name = "audit_comment") // 添加审核意见字段
    private String auditComment;


    //使用了 @ManyToOne 注解来关联 JobPost 类
    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    // 无参构造方法
    public JobAudit() {
    }

    // 全参构造方法
    public JobAudit(String auditId, LocalDateTime auditTime, String auditResult, String adminId, Integer isApproved, String auditComment, JobPost jobPost) {
        this.auditId = auditId;
        this.auditTime = auditTime;
        this.auditResult = auditResult;
        this.adminId = adminId;
        this.isApproved = isApproved;
        this.auditComment = auditComment;
        this.jobPost = jobPost;
    }

    // getter 和 setter 方法
    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public Integer getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Integer isApproved) {
        this.isApproved = isApproved;
    }

    public String getAuditComment() {
        return auditComment;
    }

    public void setAuditComment(String auditComment) {
        this.auditComment = auditComment;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }
}
