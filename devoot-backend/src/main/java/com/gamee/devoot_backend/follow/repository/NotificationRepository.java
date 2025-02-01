package com.gamee.devoot_backend.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.follow.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
