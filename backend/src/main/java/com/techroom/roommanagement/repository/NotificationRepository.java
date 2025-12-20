package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserId(Integer userId);
    Optional<Notification> findByIdAndUserId(Long id, Long userId);

}
