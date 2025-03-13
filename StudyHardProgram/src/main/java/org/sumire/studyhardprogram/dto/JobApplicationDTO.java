package org.sumire.studyhardprogram.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位申请数据传输对象
 */
@Data
public class JobApplicationDTO {
    private String userId;
    private String jobPostId;
    private String employerId;
    private String name;
    private String studentId;
    private String phone;
    private String email;
    private String introduction;
    private String status;
    private LocalDateTime applyTime;
} 