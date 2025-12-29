package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Notification;
import com.techroom.roommanagement.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    /**
     * Lấy tất cả notifications của user, sắp xếp theo thời gian tạo giảm dần
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

    /**
     * Lấy notifications của user - PHÂN TRANG
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    /**
     * Lấy notifications chưa đọc của user
     */
    List<Notification> findByUserIdAndIsRead(Integer userId, Boolean isRead);

    /**
     * Đếm số notifications chưa đọc của user
     */
    long countByUserIdAndIsRead(Integer userId, Boolean isRead);

    /**
     * Tìm notification theo id và userId (để check quyền)
     */
    Optional<Notification> findByIdAndUserId(Integer id, Integer userId);

    /**
     * Lấy notifications theo type
     */
    List<Notification> findByUserIdAndType(Integer userId, NotificationType type);

    /**
     * Lấy notifications trong khoảng thời gian
     */
    List<Notification> findByUserIdAndCreatedAtBetween(
            Integer userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * Xóa tất cả notifications của user
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);

    /**
     * Xóa notifications cũ hơn X ngày
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :date")
    void deleteOldNotifications(@Param("date") LocalDateTime date);

    /**
     * Đánh dấu tất cả notifications của user là đã đọc
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Integer userId);
}