package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.UserRepository;
import com.techroom.roommanagement.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("========== LOAD USER BY USERNAME ==========");
        System.out.println("Username: " + username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        System.out.println("✅ User found - ID: " + user.getId() + ", Role: " + user.getRole());

        String role = switch (user.getRole()) {
            case 0 -> "ADMIN";
            case 1 -> "LANDLORD";
            case 2 -> "TENANT";
            default -> "USER";
        };

        System.out.println("✅ Role mapped to: ROLE_" + role);

        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)),
                true
        );

        System.out.println("✅ CustomUserDetails created with ID: " + userDetails.getId());
        return userDetails;
    }
}