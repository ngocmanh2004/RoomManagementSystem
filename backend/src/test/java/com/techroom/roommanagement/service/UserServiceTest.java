package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.RegisterRequest;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.ContractRepository;
import com.techroom.roommanagement.repository.LandlordRepository;
import com.techroom.roommanagement.repository.RefreshTokenRepository;
import com.techroom.roommanagement.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private LandlordRepository landlordRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("nghiahaysuy1");
        registerRequest.setPassword("123456");
        registerRequest.setFullName("Nghĩa hay suy 1");
        registerRequest.setEmail("nghiahaysuy1@gmail.com");
        registerRequest.setPhone("0909080709");
        registerRequest.setRole(2);

        user = new User();
        user.setId(1);
        user.setUsername("nghiahaysuy1");
        user.setEmail("nghiahaysuy1@gmail.com");
        user.setRole(2);
        user.setStatus(User.Status.ACTIVE);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("✅ [HOÀN TẤT]: " + testInfo.getDisplayName());
        System.out.println("----------------------------------------------------------------");
    }

    @Test
    @DisplayName("Test API Thêm User - Thành công (Dữ liệu hợp lệ)")
    void testCreateUser_Success() {
        // 1. GIVEN
        when(userRepository.findByUsername("nghiahaysuy1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nghiahaysuy1@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(registerRequest);

        assertNotNull(createdUser);
        assertEquals("nghiahaysuy1", createdUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));

        System.out.println("   -> Kết quả: Tạo user thành công với ID: " + createdUser.getId());
    }

    @Test
    @DisplayName("Test API Thêm User - Thất bại (Trùng Username)")
    void testCreateUser_Fail_UsernameExists() {
        when(userRepository.findByUsername("nghiahaysuy1")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(registerRequest);
        });

        assertEquals("Tên đăng nhập đã tồn tại!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));

        System.out.println("   -> Kết quả: Đã bắt được lỗi mong muốn: " + exception.getMessage());
    }

    @Test
    @DisplayName("Test API Thêm User - Thất bại (Trùng Email)")
    void testCreateUser_Fail_EmailExists() {
        when(userRepository.findByUsername("nghiahaysuy1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nghiahaysuy1@gmail.com")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(registerRequest);
        });

        assertEquals("Email đã tồn tại!", exception.getMessage());

        System.out.println("   -> Kết quả: Đã bắt được lỗi mong muốn: " + exception.getMessage());
    }

    @Test
    @DisplayName("Test API Xóa User - Thành công (Không có ràng buộc)")
    void testDeleteUser_Success() {
        // GIVEN
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(contractRepository.existsByTenantUserId(userId)).thenReturn(false);
        when(landlordRepository.existsByUserId(userId)).thenReturn(false);

        userService.deleteUser(userId);

        verify(refreshTokenRepository, times(1)).deleteByUserId(userId);
        verify(userRepository, times(1)).deleteById(userId);

        System.out.println("   -> Kết quả: Đã xóa thành công User ID: " + userId);
    }

    @Test
    @DisplayName("Test API Xóa User - Thất bại (Đang có Hợp đồng)")
    void testDeleteUser_Fail_HasContract() {
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(contractRepository.existsByTenantUserId(userId)).thenReturn(true); // Có hợp đồng

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });

        assertTrue(exception.getMessage().contains("Không thể xóa người dùng này"));
        verify(userRepository, never()).deleteById(userId);

        System.out.println("   -> Kết quả: Đã chặn xóa thành công. Lỗi: " + exception.getMessage());
    }

    // TEST US 12.5: KHÓA TÀI KHOẢN

    @Test
    @DisplayName("Test API Cập nhật trạng thái - Khóa tài khoản (BAN)")
    void testUpdateUserStatus_Ban() {
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUserStatus(userId, User.Status.BANNED);

        assertEquals(User.Status.BANNED, user.getStatus());
        verify(userRepository, times(1)).save(user);
        verify(refreshTokenRepository, times(1)).deleteByUserId(userId);

        System.out.println("   -> Kết quả: Đã khóa tài khoản và xóa Token của User ID: " + userId);
    }
}