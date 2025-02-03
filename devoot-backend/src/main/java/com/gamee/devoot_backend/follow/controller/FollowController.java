package com.gamee.devoot_backend.follow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FollowController {
	private final FollowService followService;

	/**
	 * 사용자가 특정 프로필을 팔로우하는 메서드.
	 * @param userDetails
	 * 		현재 로그인한 사용자 정보.
	 * @param profileId
	 * 		팔로우할 대상 사용자의 프로필 ID.
	 * @return ResponseEntity
	 * 		팔로우 성공 시 201(CREATED) 상태 코드 반환.
	 */
	@PostMapping("/{profileId}/follow")
	public ResponseEntity<?> createFollower(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable String profileId
	) {
		followService.createFollower(userDetails.profileId(), profileId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 사용자가 특정 프로필을 언팔로우하는 메서드.
	 * @param userDetails
	 * 		현재 로그인한 사용자 정보.
	 * @param profileId
	 * 		팔로우할 대상 사용자의 프로필 ID.
	 * @return ResponseEntity - 언팔로우 성공 시 200(OK) 상태 코드 반환.
	 */
	@DeleteMapping("/{profileId}/follow")
	public ResponseEntity<?> deleteFollower(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable String profileId
	) {
		followService.deleteFollower(userDetails.profileId(), profileId);
		return ResponseEntity.ok().build();
	}
}
