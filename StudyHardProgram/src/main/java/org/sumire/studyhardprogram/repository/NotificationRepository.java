package org.sumire.studyhardprogram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sumire.studyhardprogram.model.Notification;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserIdOrderBySentAtDesc(String userId);
    
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.readStatus = false ORDER BY n.sentAt DESC")
    List<Notification> findUnreadNotifications(@Param("userId") String userId);
    
    long countByUserIdAndReadStatusFalse(String userId);
} 