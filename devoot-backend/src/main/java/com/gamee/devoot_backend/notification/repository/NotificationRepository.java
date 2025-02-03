package com.gamee.devoot_backend.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	void deleteByFollowId(Long id);
}
