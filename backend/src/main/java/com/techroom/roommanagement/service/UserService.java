package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.model.Tenant;
import com.techroom.roommanagement.repository.UserRepository;
import com.techroom.roommanagement.repository.TenantRepository;
import com.techroom.roommanagement.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User register(RegisterRequest request) {
        // Kiểm tra username đã tồn tại
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }

        if (request.getEmail() != null &&
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        // Tạo User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(2);
        user.setStatus(User.Status.ACTIVE);

        User savedUser = userRepository.save(user);

        Tenant tenant = new Tenant();
        tenant.setUser(savedUser);
        tenant.setAddress(request.getAddress());
        tenant.setCccd(request.getCccd());
        tenant.setDateOfBirth(request.getDateOfBirth());
        tenantRepository.save(tenant);

        return savedUser;
    }

    @Transactional
    public User createUser(RegisterRequest request) {
        // Kiểm tra username đã tồn tại
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }

        if (request.getEmail() != null &&
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        // Tạo User với role được chỉ định
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : 2);
        user.setStatus(User.Status.ACTIVE);

        User savedUser = userRepository.save(user);

        // ✅ Chỉ tạo Tenant record nếu role = 2
        if (savedUser.getRole() == 2) {
            Tenant tenant = new Tenant();
            tenant.setUser(savedUser);
            tenant.setAddress(request.getAddress());
            tenant.setCccd(request.getCccd());
            tenant.setDateOfBirth(request.getDateOfBirth());
            tenantRepository.save(tenant);
        }

        return savedUser;
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        if (user.getStatus() == User.Status.BANNED) {
            throw new RuntimeException("Account is banned");
        }

        return user;
    }
}