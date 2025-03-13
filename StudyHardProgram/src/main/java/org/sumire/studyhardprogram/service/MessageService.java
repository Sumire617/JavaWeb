package org.sumire.studyhardprogram.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sumire.studyhardprogram.dto.MessageDTO;
import org.sumire.studyhardprogram.model.Message;

public interface MessageService {
    
    // 发送消息
    Message sendMessage(MessageDTO messageDTO);
    
    // 标记消息为已读
    Message markAsRead(String messageId);
    
    // 获取两人之间的对话
    Page<Message> getConversation(String userId1, String userId2, Pageable pageable);
    
    // 获取用户收到的消息
    Page<Message> getReceivedMessages(String userId, Pageable pageable);
    
    // 获取用户发送的消息
    Page<Message> getSentMessages(String userId, Pageable pageable);
    
    // 获取用户未读消息数量
    long getUnreadCount(String userId);
    
    // 获取与特定岗位相关的消息
    Page<Message> getMessagesByJobPostId(String jobPostId, Pageable pageable);
    
    // 获取与特定申请相关的消息
    Page<Message> getMessagesByApplicationId(String applicationId, Pageable pageable);
    
    // 获取消息详情
    Message getMessageById(String messageId);
} 