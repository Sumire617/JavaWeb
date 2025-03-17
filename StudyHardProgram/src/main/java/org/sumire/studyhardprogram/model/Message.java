package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息实体类，用于保存用户与雇主间的消息交流
 */
@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String messageId;
    
    @Column(name = "sender_id", nullable = false)
    private String senderId;
    
    @Column(name = "receiver_id", nullable = false)
    private String receiverId;
    
    @Column(name = "content", nullable = false)
    private String content;
    
    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;
    
    @Column(name = "read_status", nullable = false)
    private boolean readStatus;
    
    @Column(name = "read_time")
    private LocalDateTime readTime;
    
    @Column(name = "sender_type", nullable = false)
    private String senderType; // 发送者类型: 'USER', 'EMPLOYER'
    
    @Column(name = "job_post_id")
    private String jobPostId; // 相关岗位ID，可选
    
    @Column(name = "application_id")
    private String applicationId; // 相关申请ID，可选
} 