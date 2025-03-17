package org.sumire.studyhardprogram.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sumire.studyhardprogram.model.Staff;

import java.util.List;
import java.util.Optional;

public interface StaffService {

    /**
     * 根据公司ID获取员工列表
     * @param companyId 公司ID
     * @return 员工列表
     */
    List<Staff> getStaffByCompanyId(String companyId);
    
    /**
     * 根据公司ID分页获取员工列表
     * @param companyId 公司ID
     * @param pageable 分页参数
     * @param query 搜索关键词（可选）
     * @return 员工分页列表
     */
    Page<Staff> getStaffByCompanyId(String companyId, Pageable pageable, String query);
    
    /**
     * 根据ID获取员工
     * @param id 员工ID
     * @param companyId 公司ID
     * @return 员工（可选）
     */
    Optional<Staff> getStaffById(String id, String companyId);
    
    /**
     * 创建员工
     * @param staff 员工信息
     * @return 创建的员工
     */
    Staff createStaff(Staff staff);
    
    /**
     * 更新员工
     * @param id 员工ID
     * @param staff 员工信息
     * @param companyId 公司ID
     * @return 更新后的员工
     */
    Optional<Staff> updateStaff(String id, Staff staff, String companyId);
    
    /**
     * 删除员工
     * @param id 员工ID
     * @param companyId 公司ID
     * @return 是否删除成功
     */
    boolean deleteStaff(String id, String companyId);
    
    /**
     * 检查员工手机号是否已存在
     * @param phone 手机号
     * @param companyId 公司ID
     * @return 是否存在
     */
    boolean isPhoneExist(String phone, String companyId);
    
    /**
     * 检查员工邮箱是否已存在
     * @param email 邮箱
     * @param companyId 公司ID
     * @return 是否存在
     */
    boolean isEmailExist(String email, String companyId);
} 