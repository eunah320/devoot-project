package com.gamee.devoot_backend.user.dao;

import com.gamee.devoot_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProfileId(String profileId);
}
