package org.sumire.studyhardprogram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sumire.studyhardprogram.model.Message;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    
    // 查询两人之间的对话
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
           "ORDER BY m.sendTime DESC")
    Page<Message> findConversation(
            @Param("userId1") String userId1,
            @Param("userId2") String userId2,
            Pageable pageable);
    
    // 查询用户收到的消息
    Page<Message> findByReceiverIdOrderBySendTimeDesc(String receiverId, Pageable pageable);
    
    // 查询用户发送的消息
    Page<Message> findBySenderIdOrderBySendTimeDesc(String senderId, Pageable pageable);
    
    // 查询用户未读消息数量
    long countByReceiverIdAndReadStatus(String receiverId, boolean readStatus);
    
    // 查询与特定岗位相关的消息
    Page<Message> findByJobPostIdOrderBySendTimeDesc(String jobPostId, Pageable pageable);
    
    // 查询与特定申请相关的消息
    Page<Message> findByApplicationIdOrderBySendTimeDesc(String applicationId, Pageable pageable);

    List<Message> findBySenderIdOrReceiverIdOrderBySendTimeDesc(String senderId, String receiverId);
    
    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId OR m.receiverId = :userId) AND m.jobPostId = :jobPostId ORDER BY m.sendTime DESC")
    List<Message> findMessagesByJobPost(@Param("userId") String userId, @Param("jobPostId") String jobPostId);
    
    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId OR m.receiverId = :userId) AND m.applicationId = :applicationId ORDER BY m.sendTime DESC")
    List<Message> findMessagesByApplication(@Param("userId") String userId, @Param("applicationId") String applicationId);
    
    long countByReceiverIdAndReadStatusFalse(String receiverId);
} 