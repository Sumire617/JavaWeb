package org.sumire.studyhardprogram.service;

import org.sumire.studyhardprogram.model.Company;

import java.util.Optional;

public interface CompanyService {
    
    /**
     * 创建或更新公司信息
     * @param company 公司信息
     * @return 保存后的公司信息
     */
    Company saveCompany(Company company);
    
    /**
     * 根据ID获取公司信息
     * @param companyId 公司ID
     * @return 公司信息
     */
    Optional<Company> getCompanyById(String companyId);
    
    /**
     * 根据雇主ID获取公司信息
     * @param employerId 雇主ID
     * @return 公司信息
     */
    Optional<Company> getCompanyByEmployerId(String employerId);
    
    /**
     * 根据公司名称获取公司信息
     * @param name 公司名称
     * @return 公司信息
     */
    Optional<Company> getCompanyByName(String name);
    
    /**
     * 删除公司信息
     * @param companyId 公司ID
     */
    void deleteCompany(String companyId);
} 