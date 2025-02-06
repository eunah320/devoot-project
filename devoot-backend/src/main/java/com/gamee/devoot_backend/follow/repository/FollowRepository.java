package com.gamee.devoot_backend.follow.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamee.devoot_backend.follow.dto.FollowUserDto;
import com.gamee.devoot_backend.follow.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	@Query("""
		SELECT f
		FROM Follow f
		WHERE f.followerId = :followerId
		AND f.followedId = :followedId
		AND  f.allowed = true
		""")
	Optional<Follow> findIfAllowed(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

	Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

	// 팔로잉 :  특정 사용자가 팔로우한 사용자 목록 조회
	@Query("""
		SELECT new com.gamee.devoot_backend.follow.dto.FollowUserDto(u.profileId, u.nickname, u.imageUrl)
		FROM Follow f
		JOIN User u
		ON f.followedId = u.id
		WHERE f.followerId = :followerId AND f.allowed = true
		""")
	Page<FollowUserDto> findFollowingUsersByFollowerId(Long followerId, Pageable pageable);

	// 팔로워 : 특정 사용자를 팔로우한 사용자 목록 조회
	@Query("""
		SELECT new com.gamee.devoot_backend.follow.dto.FollowUserDto(u.profileId, u.nickname, u.imageUrl)
		FROM Follow f
		JOIN User u
		ON f.followerId = u.id
		WHERE f.followedId = :followedId AND f.allowed = true
		""")
	Page<FollowUserDto> findFollowersByFollowedId(Long followedId, Pageable pageable);
}
