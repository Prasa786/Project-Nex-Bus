package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.NotificationDTO;
import com.nexbus.nexbus_backend.model.Notification;
import com.nexbus.nexbus_backend.repository.NotificationRepository;
import com.nexbus.nexbus_backend.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Page<NotificationDTO> findAll(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        Page<Notification> notifications;
        if ("ADMIN".equals(role)) {
            notifications = notificationRepository.findAll(pageable);
        } else {
            Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
            notifications = notificationRepository.findByUserUserId(userId, pageable);
        }

        return notifications.map(this::mapToDTO);
    }

    public List<NotificationDTO> findAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        List<Notification> notifications;
        if ("ADMIN".equals(role)) {
            notifications = notificationRepository.findAll();
        } else {
            Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
            notifications = notificationRepository.findByUserUserId(userId);
        }

        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public NotificationDTO findById(Integer id) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        return optionalNotification.map(this::mapToDTO).orElse(null);
    }

    public NotificationDTO markAsRead(Integer id) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if (optionalNotification.isEmpty()) {
            return null;
        }
        Notification notification = optionalNotification.get();
        if (notification.getIsRead()) {
            return null;
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return mapToDTO(notification);
    }

    public boolean isNotificationOwner(Integer notificationId, Integer userId) {
        return notificationRepository.existsByNotificationIdAndUserUserId(notificationId, userId);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUser().getUserId());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    private Notification mapToEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setNotificationId(dto.getNotificationId());
        notification.setMessage(dto.getMessage());
        notification.setIsRead(dto.getIsRead());
        return notification;
    }
}