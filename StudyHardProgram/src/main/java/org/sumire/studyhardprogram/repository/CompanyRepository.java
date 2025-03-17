package org.sumire.studyhardprogram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sumire.studyhardprogram.model.Company;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, String> {
    
    /**
     * 根据雇主ID查找公司信息
     * @param employerId 雇主ID
     * @return 公司信息
     */
    Optional<Company> findByEmployerId(String employerId);
    
    /**
     * 根据公司名称查找公司信息
     * @param name 公司名称
     * @return 公司信息
     */
    Optional<Company> findByName(String name);
} 