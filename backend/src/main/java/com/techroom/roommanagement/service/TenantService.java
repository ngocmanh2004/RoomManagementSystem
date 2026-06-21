package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Tenant;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.TenantRepository;
import com.techroom.roommanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private com.techroom.roommanagement.repository.ContractRepository contractRepository;

    public List<Tenant> getAllTenants() {
        List<Tenant> tenants = tenantRepository.findAll();
        // Populate rental status for each tenant
        for (Tenant tenant : tenants) {
            boolean hasActiveContract = contractRepository.existsByTenantIdAndStatus(
                tenant.getId(), 
                com.techroom.roommanagement.model.ContractStatus.ACTIVE
            );
            tenant.setRentalStatus(hasActiveContract ? "Đã thuê" : "Chưa thuê");
        }
        return tenants;
    }

    // Lấy tất cả tenant thuộc landlord
    public List<Tenant> getTenantsByLandlord(Integer landlordId) {
        return tenantRepository.findAllByLandlordId(landlordId);
    }

    public Optional<Tenant> getTenantById(int id) {
        return tenantRepository.findById(id);
    }

    @Transactional
    public Tenant addTenant(Tenant tenant) {
        User user = tenant.getUser();
        if (user != null) {
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDateTime.now());
            }
            userRepository.save(user);
        }
        tenant.setUser(user);
        return tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant updateTenant(int tenantId, Tenant updatedTenant) {
        Tenant existing = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        existing.setCccd(updatedTenant.getCccd());
        existing.setAddress(updatedTenant.getAddress());
        existing.setDateOfBirth(updatedTenant.getDateOfBirth());
        existing.setProvinceCode(updatedTenant.getProvinceCode());
        existing.setDistrictCode(updatedTenant.getDistrictCode());

        User existingUser = existing.getUser();
        User updatedUser = updatedTenant.getUser();
        if (existingUser != null && updatedUser != null) {
            existingUser.setFullName(updatedUser.getFullName());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setStatus(updatedUser.getStatus()); // cập nhật trạng thái
            userRepository.save(existingUser);
        }

        return tenantRepository.save(existing);
    }

    @Transactional
    public void deleteTenant(int tenantId) {
        tenantRepository.deleteById(tenantId);
    }

    public long countNewTenantsThisMonth() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        return tenantRepository.findAll().stream()
                .filter(t -> {
                    LocalDateTime createdAt = t.getUser().getCreatedAt();
                    if (createdAt == null) return false;
                    LocalDate createdDate = createdAt.toLocalDate();
                    return createdDate.getMonthValue() == currentMonth && createdDate.getYear() == currentYear;
                })
                .count();
    }
}
