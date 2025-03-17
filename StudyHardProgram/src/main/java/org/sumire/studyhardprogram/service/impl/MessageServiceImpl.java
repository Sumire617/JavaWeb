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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Override
    @Transactional
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setContent(messageDTO.getContent());
        message.setSendTime(LocalDateTime.now());
        message.setReadStatus(false);
        message.setSenderType(messageDTO.getSenderType());
        message.setJobPostId(messageDTO.getJobPostId());
        message.setApplicationId(messageDTO.getApplicationId());
        
        Message savedMessage = messageRepository.save(message);
        return convertToDTO(savedMessage);
    }
    
    @Override
    public List<MessageDTO> getMessages(String userId) {
        return messageRepository.findBySenderIdOrReceiverIdOrderBySendTimeDesc(userId, userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MessageDTO> getMessagesByJobPost(String userId, String jobPostId) {
        return messageRepository.findMessagesByJobPost(userId, jobPostId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MessageDTO> getMessagesByApplication(String userId, String applicationId) {
        return messageRepository.findMessagesByApplication(userId, applicationId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void markAsRead(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setReadStatus(true);
        message.setReadTime(LocalDateTime.now());
        messageRepository.save(message);
    }
    
    @Override
    public long getUnreadCount(String userId) {
        return messageRepository.countByReceiverIdAndReadStatusFalse(userId);
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
    
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setSendTime(message.getSendTime());
        dto.setReadStatus(message.isReadStatus());
        dto.setReadTime(message.getReadTime());
        dto.setSenderType(message.getSenderType());
        dto.setJobPostId(message.getJobPostId());
        dto.setApplicationId(message.getApplicationId());
        return dto;
    }
} 