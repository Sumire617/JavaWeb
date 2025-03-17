package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String notificationId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "read_status", nullable = false)
    private boolean readStatus;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;
} 