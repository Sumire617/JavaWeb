package org.sumire.studyhardprogram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sumire.studyhardprogram.model.Staff;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {

    /**
     * 根据公司ID查找员工列表
     * @param companyId 公司ID
     * @return 员工列表
     */
    List<Staff> findByCompanyId(String companyId);
    
    /**
     * 根据公司ID分页查询员工
     * @param companyId 公司ID
     * @param pageable 分页参数
     * @return 员工分页列表
     */
    Page<Staff> findByCompanyId(String companyId, Pageable pageable);
    
    /**
     * 根据员工姓名或工号模糊查询
     * @param companyId 公司ID
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 员工分页列表
     */
    @Query("SELECT s FROM Staff s WHERE s.companyId = :companyId AND (s.name LIKE %:query% OR s.employeeId LIKE %:query%)")
    Page<Staff> findByCompanyIdAndNameOrEmployeeIdContaining(
            @Param("companyId") String companyId, 
            @Param("query") String query, 
            Pageable pageable);
    
    /**
     * 根据员工ID和公司ID查找员工
     * @param id 员工ID
     * @param companyId 公司ID
     * @return 员工（可选）
     */
    Optional<Staff> findByIdAndCompanyId(String id, String companyId);
    
    /**
     * 根据员工手机号和公司ID查找员工
     * @param phone 手机号
     * @param companyId 公司ID
     * @return 员工（可选）
     */
    Optional<Staff> findByPhoneAndCompanyId(String phone, String companyId);
    
    /**
     * 根据员工邮箱和公司ID查找员工
     * @param email 邮箱
     * @param companyId 公司ID
     * @return 员工（可选）
     */
    Optional<Staff> findByEmailAndCompanyId(String email, String companyId);
} 