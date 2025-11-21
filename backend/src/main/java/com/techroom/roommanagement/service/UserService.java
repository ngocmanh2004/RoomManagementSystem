package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.RegisterRequest;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.ContractRepository;
import com.techroom.roommanagement.repository.LandlordRepository;
import com.techroom.roommanagement.repository.RefreshTokenRepository;
import com.techroom.roommanagement.repository.UserRepository;
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
    private LandlordRepository landlordRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    // Fix lỗi danh sách ngược: Thêm Sort.by("id").descending()
    public Page<User> getAllUsers(String keyword, Integer role, User.Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsers(keyword, role, status, pageable);
    }

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
        user.setRole(2);
        user.setStatus(User.Status.ACTIVE);
        return userRepository.save(user);
    }

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
        user.setRole(request.getRole() != null ? request.getRole() : 2);
        user.setStatus(User.Status.ACTIVE);
        return userRepository.save(user);
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
        user.setRole(userDetails.getRole());
        user.setStatus(userDetails.getStatus());
        return userRepository.save(user);
    }

    @Transactional
    public void updateUserStatus(int id, User.Status status) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Logic 1: Nếu định KHÓA tài khoản (BANNED)
        if (status == User.Status.BANNED) {
            // Lấy username của người đang đăng nhập hiện tại
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            // Ràng buộc 1: Không được tự khóa chính mình
            if (targetUser.getUsername().equals(currentUsername)) {
                throw new RuntimeException("Bạn không thể tự khóa tài khoản của chính mình!");
            }

            // Ràng buộc 2: Không được khóa Admin khác
            // (Giả sử Role 0 là Admin như quy ước)
            if (targetUser.getRole() == 0) {
                throw new RuntimeException("Không thể khóa tài khoản của Quản trị viên (Admin)!");
            }
        }

        targetUser.setStatus(status);
        userRepository.save(targetUser);

        // Nếu khóa thì xóa token để logout ngay lập tức
        if (status == User.Status.BANNED) {
            refreshTokenRepository.deleteByUserId(id);
        }
    }

    @Transactional
    public void deleteUser(int id) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Bảo mật: Không xóa chính mình và Admin khác
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (targetUser.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Bạn không thể tự xóa tài khoản của chính mình!");
        }
        if (targetUser.getRole() == 0) {
            throw new RuntimeException("Không thể xóa tài khoản Quản trị viên!");
        }

        // Kiểm tra ràng buộc dữ liệu (Contract / Landlord)
        boolean hasContracts = contractRepository.existsByTenantUserId(id);
        boolean isLandlord = landlordRepository.existsByUserId(id);

        if (hasContracts || isLandlord) {
            throw new RuntimeException("Không thể xóa người dùng này vì đang có dữ liệu liên quan (Hợp đồng/Nhà trọ). Vui lòng KHÓA tài khoản thay thế.");
        }

        // Xóa refresh token luôn
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