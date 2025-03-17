package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    
    @Id
    @Column(length = 36)
    private String id;
    
    @Column(name = "company_id", nullable = false, length = 36)
    private String companyId;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(name = "employee_id", length = 50)
    private String employeeId;
    
    @Column(length = 50)
    private String department;
    
    @Column(length = 50)
    private String position;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 20)
    private String status = "在职";
    
    @Column(name = "join_date")
    private LocalDate joinDate;
    
    @Column(length = 20)
    private String role = "STAFF";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 