package org.sumire.studyhardprogram.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息数据传输对象
 */
@Data
public class MessageDTO {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime sendTime;
    private boolean readStatus;
    private LocalDateTime readTime;
    private String senderType;
    private String jobPostId;
    private String applicationId;
    private String senderName;
    private String receiverName;
} 