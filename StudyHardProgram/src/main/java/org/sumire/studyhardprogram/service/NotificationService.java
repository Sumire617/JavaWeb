package org.sumire.studyhardprogram.service;

import org.sumire.studyhardprogram.dto.NotificationDTO;
import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(NotificationDTO notificationDTO);
    
    List<NotificationDTO> getUserNotifications(String userId);
    
    List<NotificationDTO> getUnreadNotifications(String userId);
    
    void markAsRead(String notificationId);
    
    void markAllAsRead(String userId);
    
    long getUnreadCount(String userId);
} 