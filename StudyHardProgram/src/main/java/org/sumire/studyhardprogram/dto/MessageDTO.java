package org.sumire.studyhardprogram.dto;

import lombok.Data;

/**
 * 消息数据传输对象
 */
@Data
public class MessageDTO {
    private String senderId;
    private String receiverId;
    private String senderType;
    private String jobPostId;
    private String applicationId;
    private String content;
} 