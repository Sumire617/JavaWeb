package org.sumire.studyhardprogram.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.sumire.studyhardprogram.dto.NotificationDTO;
import org.sumire.studyhardprogram.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO) {
        return ResponseEntity.ok(notificationService.createNotification(notificationDTO));
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NotificationDTO> createNotificationFormData(HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, String[]> parameterMap = request.getParameterMap();
            
            // 记录所有接收到的参数名
            logger.info("收到表单数据，参数名：{}", parameterMap.keySet());
            
            // 从前端接收的参数中提取所需信息
            String title = getParameterValue(parameterMap, "title");
            String content = getParameterValue(parameterMap, "content");
            String type = getParameterValue(parameterMap, "type");
            String priority = getParameterValue(parameterMap, "priority");
            String targetGroups = getParameterValue(parameterMap, "targetGroups");
            String validUntil = getParameterValue(parameterMap, "validUntil");
            
            if (title == null || content == null) {
                logger.error("缺少必要参数：title={}, content={}", title, content);
                return ResponseEntity.badRequest().build();
            }
            
            logger.info("创建通知：title={}, content={}, type={}, priority={}, targetGroups={}, validUntil={}", 
                     title, content, type, priority, targetGroups, validUntil);
            
            // 创建通知对象
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserId("SYSTEM"); // 由系统发送
            notificationDTO.setMessage(content);
            
            return ResponseEntity.ok(notificationService.createNotification(notificationDTO));
        } catch (Exception e) {
            logger.error("处理表单数据时发生错误", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    private String getParameterValue(Map<String, String[]> parameterMap, String name) {
        String[] values = parameterMap.get(name);
        if (values != null && values.length > 0 && values[0] != null && !values[0].isEmpty()) {
            return values[0];
        }
        return null;
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
    
    @GetMapping("/unread/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }
    
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }
    
    /**
     * 获取雇主发布的通知列表
     */
    @GetMapping("/employer/notifications")
    public ResponseEntity<Map<String, Object>> getEmployerNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String query) {
            
        logger.info("获取雇主通知列表：page={}, size={}, type={}, query={}", page, size, type, query);
        
        // 构造分页结果
        Map<String, Object> result = new HashMap<>();
        List<NotificationDTO> notifications = new ArrayList<>();
        // 模拟数据，实际应从数据库获取
        for (int i = 0; i < size; i++) {
            NotificationDTO dto = new NotificationDTO();
            dto.setNotificationId(UUID.randomUUID().toString());
            dto.setUserId("EMPLOYER");
            dto.setMessage(query != null ? "包含'" + query + "'的通知 #" + (i + 1 + page * size) : "通知 #" + (i + 1 + page * size));
            dto.setReadStatus(false);
            dto.setSentAt(LocalDateTime.now().minusDays(i));
            notifications.add(dto);
        }
        
        result.put("content", notifications);
        result.put("totalElements", 100);
        result.put("totalPages", 10);
        result.put("size", size);
        result.put("number", page);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除雇主通知
     */
    @DeleteMapping("/employer/notifications/{notificationId}")
    public ResponseEntity<Void> deleteEmployerNotification(@PathVariable String notificationId) {
        logger.info("删除通知：id={}", notificationId);
        // 实际应调用service删除通知
        return ResponseEntity.ok().build();
    }
} 