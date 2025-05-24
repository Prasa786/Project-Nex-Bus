package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserUserId(Integer userId);
    Page<Notification> findByUserUserId(Integer userId, Pageable pageable);
    boolean existsByNotificationIdAndUserUserId(Integer notificationId, Integer userId);
}