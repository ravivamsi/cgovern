package com.cgovern.cgovern.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.NotificationItem;

public interface NotificationRepository extends JpaRepository<NotificationItem, Long> {
    List<NotificationItem> findAllByOrderByCreatedAtDesc();
}
