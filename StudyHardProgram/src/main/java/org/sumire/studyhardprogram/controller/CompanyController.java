package org.sumire.studyhardprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sumire.studyhardprogram.model.Company;
import org.sumire.studyhardprogram.service.CompanyService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employer")
@CrossOrigin
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    /**
     * 获取雇主的公司信息
     * @param employerId 雇主ID (可选)
     * @return 公司信息
     */
    @GetMapping("/company")
    public ResponseEntity<?> getCompanyInfo(@RequestParam(required = false) String employerId) {
        if (employerId == null || employerId.isEmpty()) {
            // 返回空的公司对象模板
            Map<String, Object> emptyCompany = createEmptyCompanyTemplate();
            return ResponseEntity.ok(emptyCompany);
        }
        
        Optional<Company> companyOpt = companyService.getCompanyByEmployerId(employerId);
        if (companyOpt.isPresent()) {
            return ResponseEntity.ok(companyOpt.get());
        } else {
            // 返回空的公司对象模板
            Map<String, Object> emptyCompany = createEmptyCompanyTemplate();
            return ResponseEntity.ok(emptyCompany);
        }
    }
    
    private Map<String, Object> createEmptyCompanyTemplate() {
        Map<String, Object> emptyCompany = new HashMap<>();
        emptyCompany.put("name", "");
        emptyCompany.put("industry", "");
        emptyCompany.put("size", "");
        emptyCompany.put("location", "");
        emptyCompany.put("description", "");
        emptyCompany.put("contactName", "");
        emptyCompany.put("contactPhone", "");
        emptyCompany.put("contactEmail", "");
        return emptyCompany;
    }

    /**
     * 保存或更新公司信息
     * @param company 公司信息
     * @param employerId 雇主ID
     * @return 保存后的公司信息
     */
    @PutMapping("/company")
    public ResponseEntity<Company> saveCompanyInfo(@RequestBody Company company, @RequestParam(required = false) String employerId) {
        if (employerId == null || employerId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // 首先查找是否已有该雇主的公司信息
        Optional<Company> existingCompany = companyService.getCompanyByEmployerId(employerId);
        
        if (existingCompany.isPresent()) {
            // 更新现有公司信息
            Company companyToUpdate = existingCompany.get();
            companyToUpdate.setName(company.getName());
            companyToUpdate.setIndustry(company.getIndustry());
            companyToUpdate.setSize(company.getSize());
            companyToUpdate.setLocation(company.getLocation());
            companyToUpdate.setDescription(company.getDescription());
            companyToUpdate.setContactName(company.getContactName());
            companyToUpdate.setContactPhone(company.getContactPhone());
            companyToUpdate.setContactEmail(company.getContactEmail());
            
            return ResponseEntity.ok(companyService.saveCompany(companyToUpdate));
        } else {
            // 创建新的公司信息
            company.setEmployerId(employerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(companyService.saveCompany(company));
        }
    }

    /**
     * 删除公司信息
     * @param employerId 雇主ID
     * @return 成功信息
     */
    @DeleteMapping("/company")
    public ResponseEntity<Map<String, String>> deleteCompanyInfo(@RequestParam String employerId) {
        Optional<Company> companyOpt = companyService.getCompanyByEmployerId(employerId);
        if (companyOpt.isPresent()) {
            companyService.deleteCompany(companyOpt.get().getCompanyId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "公司信息删除成功");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 