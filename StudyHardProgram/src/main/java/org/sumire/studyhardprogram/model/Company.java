package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id", nullable = false, length = 36)
    private String companyId;

    @Column(name = "employer_id", nullable = false, length = 36)
    private String employerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "industry", length = 50)
    private String industry;

    @Column(name = "size", length = 50)
    private String size;

    @Column(name = "location", length = 255)
    private String location;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "contact_name", length = 50)
    private String contactName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // Getters and Setters
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
} 