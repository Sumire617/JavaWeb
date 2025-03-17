package org.sumire.studyhardprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sumire.studyhardprogram.model.Company;
import org.sumire.studyhardprogram.model.Staff;
import org.sumire.studyhardprogram.service.CompanyService;
import org.sumire.studyhardprogram.service.StaffService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employer/staff")
@CrossOrigin
public class StaffController {

    @Autowired
    private StaffService staffService;
    
    @Autowired
    private CompanyService companyService;

    /**
     * 获取员工列表（分页）
     * @param employerId 雇主ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param query 搜索关键词（可选）
     * @return 员工分页列表
     */
    @GetMapping
    public ResponseEntity<?> getStaffList(
            @RequestParam String employerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        
        // 根据雇主ID获取公司ID
        Optional<Company> companyOpt = companyService.getCompanyByEmployerId(employerId);
        if (companyOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("content", List.of());
            response.put("totalElements", 0);
            response.put("totalPages", 0);
            response.put("number", page);
            response.put("size", size);
            return ResponseEntity.ok(response);
        }
        
        String companyId = companyOpt.get().getCompanyId();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Staff> staffPage = staffService.getStaffByCompanyId(companyId, pageRequest, query);
        
        return ResponseEntity.ok(staffPage);
    }
    
    /**
     * 根据ID获取员工详情
     * @param id 员工ID
     * @param employerId 雇主ID
     * @return 员工详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable String id, @RequestParam String employerId) {
        // 根据雇主ID获取公司ID
        Optional<Company> companyOpt = companyService.getCompanyByEmployerId(employerId);
        if (companyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        String companyId = companyOpt.get().getCompanyId();
        Optional<Staff> staffOpt = staffService.getStaffById(id, companyId);
        
        return staffOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 创建新员工
     * @param staff 员工信息
     * @param employerId 雇主ID
     * @return 创建的员工信息
     */
    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody Staff staff, @RequestParam String employerId) {
        // 根据雇主ID获取公司ID
        Optional<Company> companyOpt = companyService.getCompanyByEmployerId(employerId);
        if (companyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("公司信息不存在，请先完善公司信息");
        }
        
        String companyId = companyOpt.get().getCompanyId();
        
        // 检查手机号和邮箱是否已存在
        if (staffService.isPhoneExist(staff.getPhone(), companyId)) {
            return ResponseEntity.badRequest().body("手机号已存在");
        }
        
        if (staffService.isEmailExist(staff.getEmail(), companyId)) {
            return ResponseEntity.badRequest().body("邮箱已存在");
        }
        
        // 设置公司ID
        staff.setCompanyId(companyId);
        
        Staff createdStaff = staffService.createStaff(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStaff);
    }
    
    /**
     * 更新员工信息
     * @param id 员工ID
     * @param staff 员工信息
     * @param employerId 雇主ID
     * @return 更新后的员工信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable String id,
            @RequestBody Staff staff,
            @RequestParam String employerId) {
        
        // 根据雇主ID获取公司ID
        Optional<Company> companyOpt = companyService.getCompanyByEmployerId(employerId);
        if (companyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("公司信息不存在");
        }
        
        String companyId = companyOpt.get().getCompanyId();
        
        // 获取原员工信息
        Optional<Staff> existingStaffOpt = staffService.getStaffById(id, companyId);
        if (existingStaffOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Staff existingStaff = existingStaffOpt.get();
        
        // 检查手机号是否已被其他员工使用
        if (!existingStaff.getPhone().equals(staff.getPhone()) && 
            staffService.isPhoneExist(staff.getPhone(), companyId)) {
            return ResponseEntity.badRequest().body("手机号已存在");
        }
        
        // 检查邮箱是否已被其他员工使用
        if (!existingStaff.getEmail().equals(staff.getEmail()) && 
            staffService.isEmailExist(staff.getEmail(), companyId)) {
            return ResponseEntity.badRequest().body("邮箱已存在");
        }
        
        // 更新员工信息
        return staffService.updateStaff(id, staff, companyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除员工
     * @param id 员工ID
     * @param employerId 雇主ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable String id, @RequestParam String employerId) {
        // 根据雇主ID获取公司ID
        Optional<Company> companyOpt = companyService.getCompanyByEmployerId(employerId);
        if (companyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("公司信息不存在");
        }
        
        String companyId = companyOpt.get().getCompanyId();
        
        // 删除员工
        boolean deleted = staffService.deleteStaff(id, companyId);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 