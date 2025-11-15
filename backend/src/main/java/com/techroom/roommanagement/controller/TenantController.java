package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.RegisterRequest;
import com.techroom.roommanagement.model.Tenant;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.TenantRepository;
import com.techroom.roommanagement.repository.UserRepository;
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

    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable int id) {
        Optional<Tenant> tenant = tenantRepository.findById(id);
        return tenant.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

   @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody RegisterRequest request) {
        try {
            // 🔹 Kiểm tra trùng username (số điện thoại)
            if (userRepository.findByUsername(request.getPhone()).isPresent()) {
                return ResponseEntity.badRequest().body("Số điện thoại đã được sử dụng!");
            }

            // 🔹 Sinh mật khẩu ngẫu nhiên
            String rawPassword = generateRandomPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);

            // 🔹 Tạo tài khoản User trước
            User user = new User();
            user.setUsername(request.getPhone());           // username là số điện thoại
            user.setPassword(encodedPassword);              // mật khẩu mã hóa
          // gán tên mặc định nếu fullName null hoặc rỗng
    String fullName = request.getFullName();
    if ((fullName == null || fullName.isBlank()) && request.getPhone() != null) {
        fullName = "Khách thuê " + request.getPhone(); // mặc định
    }
    user.setFullName(fullName);

            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setRole(2);                                // 2 = Tenant
            user.setStatus(User.Status.ACTIVE);
            user.setCreatedAt(java.time.LocalDateTime.now());

            User savedUser = userRepository.save(user);

            // 🔹 Tạo Tenant gắn với User
            Tenant tenant = new Tenant();
            tenant.setUser(savedUser);                      // liên kết 1-1
            tenant.setCccd(request.getCccd());
            tenant.setDateOfBirth(request.getDateOfBirth());
            tenant.setAddress(request.getAddress());

            tenantRepository.save(tenant);

            // 🔹 Giả lập gửi mật khẩu (hoặc sau này có thể dùng Twilio)
            sendPasswordViaSMS(request.getPhone(), rawPassword);

            // 🔹 Trả về thông tin kết quả
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

    
    @PutMapping("/{id}")
    public ResponseEntity<Tenant> updateTenant(@PathVariable int id, @RequestBody Tenant tenantDetails) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(id);
        if (tenantOptional.isPresent()) {
            Tenant tenant = tenantOptional.get();
            tenant.setCccd(tenantDetails.getCccd());
            tenant.setDateOfBirth(tenantDetails.getDateOfBirth());
            tenant.setAddress(tenantDetails.getAddress());
            Tenant updated = tenantRepository.save(tenant);
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenant(@PathVariable int id) {
        Optional<Tenant> tenantOpt = tenantRepository.findById(id);
        if (tenantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tenant tenant = tenantOpt.get();
        User user = tenant.getUser();

        // ❗ Nếu đang thuê (ACTIVE) → không cho phép xóa
        if (user.getStatus() == User.Status.ACTIVE) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Khách đang thuê, không thể xóa!")
            );
        }

        // ❗ Nếu trạng thái khác (PENDING hoặc INACTIVE) → cho phép
        tenantRepository.delete(tenant);

        return ResponseEntity.ok(Map.of(
                "message", "Xóa khách thuê thành công!"
        ));
    }


    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendPasswordViaSMS(String phone, String password) {
        System.out.println("=== GỬI SMS ===");
        System.out.println("SĐT: " + phone);
        System.out.println("Mật khẩu: " + password);
        System.out.println("Nội dung: Chào bạn! Tài khoản của bạn đã được tạo. Mật khẩu: " + password);
    }
}