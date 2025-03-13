package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sumire.studyhardprogram.dto.MessageDTO;
import org.sumire.studyhardprogram.model.Message;
import org.sumire.studyhardprogram.repository.MessageRepository;
import org.sumire.studyhardprogram.service.MessageService;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Override
    @Transactional
    public Message sendMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setSenderType(messageDTO.getSenderType());
        message.setJobPostId(messageDTO.getJobPostId());
        message.setApplicationId(messageDTO.getApplicationId());
        message.setContent(messageDTO.getContent());
        message.setSendTime(LocalDateTime.now());
        message.setReadStatus(false); // 默认未读
        
        return messageRepository.save(message);
    }
    
    @Override
    @Transactional
    public Message markAsRead(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        if (!message.isReadStatus()) {
            message.setReadStatus(true);
            message.setReadTime(LocalDateTime.now());
            return messageRepository.save(message);
        }
        
        return message;
    }
    
    @Override
    public Page<Message> getConversation(String userId1, String userId2, Pageable pageable) {
        return messageRepository.findConversation(userId1, userId2, pageable);
    }
    
    @Override
    public Page<Message> getReceivedMessages(String userId, Pageable pageable) {
        return messageRepository.findByReceiverIdOrderBySendTimeDesc(userId, pageable);
    }
    
    @Override
    public Page<Message> getSentMessages(String userId, Pageable pageable) {
        return messageRepository.findBySenderIdOrderBySendTimeDesc(userId, pageable);
    }
    
    @Override
    public long getUnreadCount(String userId) {
        return messageRepository.countByReceiverIdAndReadStatus(userId, false);
    }
    
    @Override
    public Page<Message> getMessagesByJobPostId(String jobPostId, Pageable pageable) {
        return messageRepository.findByJobPostIdOrderBySendTimeDesc(jobPostId, pageable);
    }
    
    @Override
    public Page<Message> getMessagesByApplicationId(String applicationId, Pageable pageable) {
        return messageRepository.findByApplicationIdOrderBySendTimeDesc(applicationId, pageable);
    }
    
    @Override
    public Message getMessageById(String messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
    }
} 