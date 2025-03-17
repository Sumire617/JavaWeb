package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sumire.studyhardprogram.dto.NotificationDTO;
import org.sumire.studyhardprogram.model.Notification;
import org.sumire.studyhardprogram.repository.NotificationRepository;
import org.sumire.studyhardprogram.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Override
    @Transactional
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setUserId(notificationDTO.getUserId());
        notification.setMessage(notificationDTO.getMessage());
        notification.setReadStatus(false);
        notification.setSentAt(LocalDateTime.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }
    
    @Override
    public List<NotificationDTO> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<NotificationDTO> getUnreadNotifications(String userId) {
        return notificationRepository.findUnreadNotifications(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setReadStatus(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
    
    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadNotifications(userId);
        unreadNotifications.forEach(notification -> {
            notification.setReadStatus(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unreadNotifications);
    }
    
    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadStatusFalse(userId);
    }
    
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUserId());
        dto.setMessage(notification.getMessage());
        dto.setReadStatus(notification.isReadStatus());
        dto.setSentAt(notification.getSentAt());
        dto.setReadAt(notification.getReadAt());
        return dto;
    }
} 