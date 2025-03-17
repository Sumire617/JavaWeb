package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sumire.studyhardprogram.model.Company;
import org.sumire.studyhardprogram.repository.CompanyRepository;
import org.sumire.studyhardprogram.service.CompanyService;

import java.time.Instant;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Company saveCompany(Company company) {
        if (company.getCreatedAt() == null) {
            company.setCreatedAt(Instant.now());
        }
        company.setUpdatedAt(Instant.now());
        return companyRepository.save(company);
    }

    @Override
    public Optional<Company> getCompanyById(String companyId) {
        return companyRepository.findById(companyId);
    }

    @Override
    public Optional<Company> getCompanyByEmployerId(String employerId) {
        return companyRepository.findByEmployerId(employerId);
    }

    @Override
    public Optional<Company> getCompanyByName(String name) {
        return companyRepository.findByName(name);
    }

    @Override
    public void deleteCompany(String companyId) {
        companyRepository.deleteById(companyId);
    }
} 