package com.gamee.devoot_backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gamee.devoot_backend.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByProfileId(String profileId);

	Optional<User> findByUid(String uid);

	boolean existsByUid(String uid);

	boolean existsByProfileId(String profileId);

	@Query("""
		SELECT u FROM User u
		WHERE u.profileId LIKE :prefix%
		OR u.nickname LIKE :prefix%
		""")
	Page<User> searchByPrefix(String prefix, Pageable pageable);
}
