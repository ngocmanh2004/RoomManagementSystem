package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.ApiResponse;
import com.techroom.roommanagement.dto.RegisterRequest;
import com.techroom.roommanagement.model.Landlord;
import com.techroom.roommanagement.model.Tenant;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.LandlordRepository;
import com.techroom.roommanagement.repository.TenantRepository;
import com.techroom.roommanagement.repository.UserRepository;
import com.techroom.roommanagement.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TenantController {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private LandlordRepository landlordRepository;

    // Lấy tất cả tenant
    @GetMapping
    public List<Tenant> getAllTenants() {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId != null) {
            return tenantService.getTenantsByLandlord(landlordId);
        }
        else {
            return tenantService.getAllTenants();
        }
    }
    // Lấy tenant theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable int id) {
        Optional<Tenant> tenant = tenantService.getTenantById(id);
        return tenant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lấy tenant theo userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTenantByUserId(@PathVariable int userId) {
        System.out.println("🔍 getTenantByUserId - userId: " + userId);

        Optional<Tenant> tenantOpt = tenantRepository.findByUserId(userId);

        if (tenantOpt.isPresent()) {
            Tenant tenant = tenantOpt.get();
            System.out.println("✅ Tenant found: " + tenant);
            System.out.println("  - id: " + tenant.getId());
            System.out.println("  - cccd: " + tenant.getCccd());
            System.out.println("  - address: " + tenant.getAddress());
            System.out.println("  - user.phone: " + (tenant.getUser() != null ? tenant.getUser().getPhone() : "null"));

            return ResponseEntity.ok(tenant);
        }

        // ❌ Nếu không tìm thấy Tenant → TẠO MỚI
        System.out.println("⚠️ Tenant not found for userId: " + userId);

        // Lấy User
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            System.out.println("❌ User not found for userId: " + userId);
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        System.out.println("✅ User found: " + user.getFullName());

        // ✅ Tạo Tenant mới tự động
        Tenant newTenant = new Tenant();
        newTenant.setUser(user);
        newTenant.setCccd("");
        newTenant.setAddress("");
        newTenant.setDateOfBirth(null);

        Tenant savedTenant = tenantRepository.save(newTenant);
        System.out.println("✅ Created new Tenant for userId: " + userId);

        return ResponseEntity.ok(savedTenant);
    }

    // Tạo tenant
    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody RegisterRequest request) {
        try {
            // Kiểm tra trùng số điện thoại
            if (userRepository.findByUsername(request.getPhone()).isPresent()) {
                return ResponseEntity.badRequest().body("Số điện thoại đã được sử dụng!");
            }

            // Sinh mật khẩu ngẫu nhiên
            String rawPassword = generateRandomPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);

            // Tạo User
            User user = new User();
            user.setUsername(request.getPhone());
            user.setPassword(encodedPassword);

            // Gán fullName mặc định nếu không có
            String fullName = request.getFullName();
            if (fullName == null || fullName.isBlank()) {
                fullName = "Khách thuê " + request.getPhone();
            }
            user.setFullName(fullName);

            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setRole(2); // Tenant
            user.setStatus(User.Status.ACTIVE);
            user.setCreatedAt(java.time.LocalDateTime.now());

            User savedUser = userRepository.save(user);

            // Tạo Tenant
            Tenant tenant = new Tenant();
            tenant.setUser(savedUser);
            tenant.setCccd(request.getCccd());
            tenant.setDateOfBirth(request.getDateOfBirth());
            tenant.setAddress(request.getAddress());

            tenantRepository.save(tenant);

            // Giả lập gửi mật khẩu
            sendPasswordViaSMS(request.getPhone(), rawPassword);

            return ResponseEntity.ok(Map.of(
                    "message", "Thêm khách thuê thành công!",
                    "username", request.getPhone(),
                    "password", rawPassword
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi thêm khách thuê: " + e.getMessage());
        }
    }

    // Cập nhật tenant
    @PutMapping("/{id}")
    public ResponseEntity<Tenant> updateTenant(@PathVariable int id, @RequestBody Tenant tenantDetails) {
        try {
            Tenant updatedTenant = tenantService.updateTenant(id, tenantDetails);
            return ResponseEntity.ok(updatedTenant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa tenant
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenant(@PathVariable int id) {
        Optional<Tenant> tenantOpt = tenantRepository.findById(id);
        if (tenantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tenant tenant = tenantOpt.get();
        User user = tenant.getUser();

        //  KHÔNG ĐƯỢC PHÉP XÓA KHI KHÁCH ĐANG Ở TRẠNG THÁI ĐANG THUÊ
        if (user.getStatus() == User.Status.ACTIVE) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Khách đang thuê, không thể xóa!")
            );
        }

        // Nếu không ACTIVE → cho phép xóa
        tenantRepository.delete(tenant);

        return ResponseEntity.ok(Map.of(
                "message", "Xóa khách thuê thành công!"
        ));
    }

    // Sinh mật khẩu
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Giả lập gửi SMS
    private void sendPasswordViaSMS(String phone, String password) {
        System.out.println("=== GỬI SMS ===");
        System.out.println("SĐT: " + phone);
        System.out.println("Mật khẩu: " + password);
        System.out.println("Nội dung: Chào bạn! Mật khẩu của bạn: " + password);
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableTenants() {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized", null));
        }

        try {
            // Lấy tenant không có hợp đồng ACTIVE
            List<Tenant> tenants = tenantRepository.findAvailableTenantsByLandlord(landlordId);

            return ResponseEntity.ok(new ApiResponse(true, "Lấy danh sách khách thuê thành công", tenants));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Method để lấy ID của landlord hiện tại
    private Integer getCurrentLandlordId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null || user.getRole() != 1) {
            return null;
        }

        return landlordRepository.findByUserId(user.getId())
                .map(Landlord::getId)   // ✅ landlords.id
                .orElse(null);
    }
}