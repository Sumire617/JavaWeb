package org.sumire.studyhardprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sumire.studyhardprogram.dto.MessageDTO;
import org.sumire.studyhardprogram.model.Message;
import org.sumire.studyhardprogram.service.MessageService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.sendMessage(messageDTO));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageDTO>> getUserMessages(@PathVariable String userId) {
        return ResponseEntity.ok(messageService.getMessages(userId));
    }
    
    @GetMapping("/job-post/{jobPostId}/user/{userId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByJobPost(
            @PathVariable String jobPostId,
            @PathVariable String userId) {
        return ResponseEntity.ok(messageService.getMessagesByJobPost(userId, jobPostId));
    }
    
    @GetMapping("/application/{applicationId}/user/{userId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByApplication(
            @PathVariable String applicationId,
            @PathVariable String userId) {
        return ResponseEntity.ok(messageService.getMessagesByApplication(userId, applicationId));
    }
    
    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        return ResponseEntity.ok(messageService.getUnreadCount(userId));
    }
    
    @GetMapping("/conversation")
    public ResponseEntity<Page<Message>> getConversation(
            @RequestParam String userId1,
            @RequestParam String userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sendTime").descending());
        return ResponseEntity.ok(messageService.getConversation(userId1, userId2, pageable));
    }
    
    @GetMapping("/received/{userId}")
    public ResponseEntity<Page<Message>> getReceivedMessages(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sendTime").descending());
        return ResponseEntity.ok(messageService.getReceivedMessages(userId, pageable));
    }
    
    @GetMapping("/sent/{userId}")
    public ResponseEntity<Page<Message>> getSentMessages(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sendTime").descending());
        return ResponseEntity.ok(messageService.getSentMessages(userId, pageable));
    }
    
    @GetMapping("/job/{jobPostId}")
    public ResponseEntity<Page<Message>> getMessagesByJobPostId(
            @PathVariable String jobPostId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sendTime").descending());
        return ResponseEntity.ok(messageService.getMessagesByJobPostId(jobPostId, pageable));
    }
    
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<Page<Message>> getMessagesByApplicationId(
            @PathVariable String applicationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sendTime").descending());
        return ResponseEntity.ok(messageService.getMessagesByApplicationId(applicationId, pageable));
    }
    
    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable String messageId) {
        return ResponseEntity.ok(messageService.getMessageById(messageId));
    }
} 