package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Xóa user (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User has been soft deleted (locked)"));
    }

    // Lấy danh sách user (active only)
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) User.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> users = userService.getAllUsers(keyword, role, status, page, size);
        return ResponseEntity.ok(users);
    }
}