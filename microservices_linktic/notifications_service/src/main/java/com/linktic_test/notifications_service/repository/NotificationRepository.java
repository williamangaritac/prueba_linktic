package com.linktic_test.notifications_service.repository;

import com.linktic_test.notifications_service.model.entities.Notification;
import com.linktic_test.notifications_service.model.entities.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar notificaciones
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByOrderNumber(String orderNumber);
    
    List<Notification> findByStatus(NotificationStatus status);
    
    List<Notification> findByRecipientEmail(String recipientEmail);
    
    Optional<Notification> findByOrderId(Long orderId);
}

