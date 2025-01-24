package com.gamee.devoot_backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUid(String uid);
}
