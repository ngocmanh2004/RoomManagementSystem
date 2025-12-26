package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByUserId(Integer userId, Pageable pageable);
    Optional<Notification> findByIdAndUserId(Integer id, Integer userId);

}
