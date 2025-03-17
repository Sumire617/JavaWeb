package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sumire.studyhardprogram.model.Staff;
import org.sumire.studyhardprogram.repository.StaffRepository;
import org.sumire.studyhardprogram.service.StaffService;

import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public List<Staff> getStaffByCompanyId(String companyId) {
        return staffRepository.findByCompanyId(companyId);
    }

    @Override
    public Page<Staff> getStaffByCompanyId(String companyId, Pageable pageable, String query) {
        if (query != null && !query.isEmpty()) {
            return staffRepository.findByCompanyIdAndNameOrEmployeeIdContaining(companyId, query, pageable);
        }
        return staffRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Optional<Staff> getStaffById(String id, String companyId) {
        return staffRepository.findByIdAndCompanyId(id, companyId);
    }

    @Override
    @Transactional
    public Staff createStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    @Override
    @Transactional
    public Optional<Staff> updateStaff(String id, Staff staff, String companyId) {
        return staffRepository.findByIdAndCompanyId(id, companyId)
                .map(existingStaff -> {
                    staff.setId(existingStaff.getId());
                    staff.setCompanyId(existingStaff.getCompanyId());
                    return staffRepository.save(staff);
                });
    }

    @Override
    @Transactional
    public boolean deleteStaff(String id, String companyId) {
        return staffRepository.findByIdAndCompanyId(id, companyId)
                .map(staff -> {
                    staffRepository.delete(staff);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean isPhoneExist(String phone, String companyId) {
        return staffRepository.findByPhoneAndCompanyId(phone, companyId).isPresent();
    }

    @Override
    public boolean isEmailExist(String email, String companyId) {
        return staffRepository.findByEmailAndCompanyId(email, companyId).isPresent();
    }
} 