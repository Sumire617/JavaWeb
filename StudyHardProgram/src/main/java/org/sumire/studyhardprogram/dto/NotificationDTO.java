package org.sumire.studyhardprogram.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private String notificationId;
    private String userId;
    private String message;
    private boolean readStatus;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
} 