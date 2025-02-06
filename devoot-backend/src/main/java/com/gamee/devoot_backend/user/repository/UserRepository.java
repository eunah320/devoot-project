package com.gamee.devoot_backend.user.repository;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

	@Query("""
		SELECT
			(SELECT COUNT(b) FROM Bookmark b WHERE b.user.id = :userId) AS bookmarkCnt,
			(SELECT COUNT(f) FROM Follow f WHERE f.followerId = :userId) AS followingCnt,
			(SELECT COUNT(f) FROM Follow f WHERE f.followedId = :userId) AS followerCnt
		FROM User u WHERE u.id = :userId
		""")
	Map<String, Integer> getUserStatsAsMap(@Param("id") Long userId);
}
