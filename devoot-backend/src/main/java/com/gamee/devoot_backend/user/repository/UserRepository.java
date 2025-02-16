package com.gamee.devoot_backend.user.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gamee.devoot_backend.user.entity.Admin;
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
			COUNT(DISTINCT b) as bookmarkCnt,
			COUNT(DISTINCT f1) as followingCnt,
			COUNT(DISTINCT f2) as followerCnt
		FROM User u
		LEFT JOIN Bookmark b ON b.user.id = u.id
		LEFT JOIN Follow f1 ON f1.followerId = u.id
		LEFT JOIN Follow f2 ON f2.followedId = u.id
		WHERE u.id = :userId
		""")
	Map<String, Long> getUserStatsAsMap(Long userId);

	@Query("""
		SELECT
		CASE
			WHEN f.allowed = TRUE THEN 'FOLLOWING'
			WHEN f.allowed = FALSE THEN 'PENDING'
		END
		FROM Follow f
		WHERE f.followerId = :followerId AND f.followedId = :followedId
		""")
	Optional<String> isFollowing(Long followerId, Long followedId);

	@Query("""
		SELECT EXISTS (
			SELECT 1
			FROM Admin a
			WHERE a.userId = :userId
		)
		""")
	Boolean isAdmin(Long userId);

	@Query("""
		SELECT a
		FROM Admin a
		JOIN FETCH a.user
		ORDER BY a.createdAt DESC
		""")
	List<Admin> findAllAdmin();
}
