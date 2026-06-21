package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.RegisterRequest;
import com.techroom.roommanagement.model.Landlord; // <-- Import Landlord
import com.techroom.roommanagement.model.Tenant;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.*; // Import các Repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private LandlordRepository landlordRepository; // <-- Cần cái này để lưu Chủ trọ

    @Autowired
    private com.techroom.roommanagement.repository.TenantRepository tenantRepository; // <-- Cần cái này để lưu Khách thuê

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Page<User> getAllUsers(String keyword, Integer role, User.Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsers(keyword, role, status, pageable);
    }

    // Đăng ký (Mặc định là Khách thuê - Role 2)
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(2); // Mặc định Tenant
        user.setStatus(User.Status.ACTIVE);

        User savedUser = userRepository.save(user);

        // Tạo bảng Tenant
        Tenant tenant = new Tenant();
        tenant.setUser(savedUser);
        tenant.setAddress(request.getAddress());
        tenant.setCccd(request.getCccd());
        tenant.setDateOfBirth(request.getDateOfBirth());
        tenantRepository.save(tenant);

        return savedUser;
    }

    // Admin tạo User (Có thể là Role 0, 1, 2)
    @Transactional
    public User createUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // Lấy role từ request (hoặc mặc định là 2)
        int role = request.getRole() != null ? request.getRole() : 2;
        user.setRole(role);
        user.setStatus(User.Status.ACTIVE);

        User savedUser = userRepository.save(user);

        // === LOGIC QUAN TRỌNG ĐÃ SỬA ===

        // 1. Nếu là Khách thuê (Role 2) -> Tạo Tenant
        if (role == 2) {
            Tenant tenant = new Tenant();
            tenant.setUser(savedUser);
            tenant.setAddress(request.getAddress());
            tenant.setCccd(request.getCccd());
            tenant.setDateOfBirth(request.getDateOfBirth());
            tenantRepository.save(tenant);
        }
        // 2. Nếu là Chủ trọ (Role 1) -> Tạo Landlord
        else if (role == 1) {
            Landlord landlord = new Landlord();
            landlord.setUser(savedUser);
            // Admin tạo nên mặc định APPROVED
            landlord.setApproved(Landlord.ApprovalStatus.APPROVED);
            landlord.setUtilityMode(Landlord.UtilityMode.LANDLORD_INPUT);
            // Các thông tin khác (giấy phép, địa chỉ...) có thể cập nhật sau
            landlordRepository.save(landlord);
        }

        // Role 0 (Admin) thì không cần tạo bảng phụ

        return savedUser;
    }

    @Transactional
    public User updateUser(int id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác");
            }
            user.setEmail(userDetails.getEmail());
        }
        user.setFullName(userDetails.getFullName());
        user.setPhone(userDetails.getPhone());

        // Lưu ý: Việc đổi Role ở đây có thể phức tạp (cần xóa Tenant cũ tạo Landlord mới...)
        // Tạm thời chỉ update Role field, logic chuyển đổi dữ liệu nên làm riêng nếu cần
        user.setRole(userDetails.getRole());

        user.setStatus(userDetails.getStatus());
        return userRepository.save(user);
    }

    @Transactional
    public void updateUserStatus(int id, User.Status status) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (status == User.Status.BANNED) {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            if (targetUser.getUsername().equals(currentUsername)) {
                throw new RuntimeException("Bạn không thể tự khóa tài khoản của chính mình!");
            }

            if (targetUser.getRole() == 0) {
                throw new RuntimeException("Không thể khóa tài khoản của Quản trị viên (Admin)!");
            }
        }

        targetUser.setStatus(status);
        userRepository.save(targetUser);

        if (status == User.Status.BANNED) {
            refreshTokenRepository.deleteByUserId(id);
        }
    }

    @Transactional
    public void deleteUser(int id) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (targetUser.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Bạn không thể tự xóa tài khoản của chính mình!");
        }
        if (targetUser.getRole() == 0) {
            throw new RuntimeException("Không thể xóa tài khoản Quản trị viên!");
        }

        // ✅ FIX: Use correct method name
        boolean hasContracts = contractRepository.existsByTenantUserId(id);
        boolean isLandlord = landlordRepository.existsByUserId(id);

        if (hasContracts || isLandlord) {
            throw new RuntimeException("Không thể xóa người dùng này vì đang có dữ liệu liên quan (Hợp đồng/Nhà trọ). Vui lòng KHÓA tài khoản thay thế.");
        }

        refreshTokenRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        if (user.getStatus() == User.Status.BANNED) {
            throw new RuntimeException("Tài khoản này đã bị khóa, không thể đăng nhập");
        }
        return user;
    }
}