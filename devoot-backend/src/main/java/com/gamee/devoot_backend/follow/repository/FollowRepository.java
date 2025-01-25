package com.gamee.devoot_backend.follow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamee.devoot_backend.follow.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	@Query("""
		SELECT f
		FROM Follow f
		WHERE f.followerId = :followerId
		AND f.followedId = :followedId
		AND  f.allowed = true
		""")
	Optional<Follow> findIfAllowed(@Param("followerId") long followerId, @Param("followedId") long followedId);
}
