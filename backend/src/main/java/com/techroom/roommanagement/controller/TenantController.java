package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.RegisterRequest;
import com.techroom.roommanagement.model.Tenant;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.TenantRepository;
import com.techroom.roommanagement.repository.UserRepository;
import com.techroom.roommanagement.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // Lấy tất cả tenant
    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    // Lấy tenant theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable int id) {
        Optional<Tenant> tenant = tenantService.getTenantById(id);
        return tenant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lấy tenant theo userId (code mới từ develop)
    @GetMapping("/user/{userId}")
    public ResponseEntity<Tenant> getTenantByUserId(@PathVariable int userId) {
        Optional<Tenant> tenant = tenantRepository.findByUserId(userId);
        return tenant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    // Xóa tenant — GIỮ NGUYÊN LOGIC CỦA BẠN
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
}
