package com.gamee.devoot_backend.follow.controller;

import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.follow.dto.FollowUserDto;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
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

	/**
	 * 사용자 A가 팔로우한 사용자 리스트 불러오는 메서드
	 * @param profileId
	 * 		사용자 A의 프로필 ID.
	 * @param page
	 *		페이지네이션 페이지.
	 * @param size
	 * 		페이지네이션 한 페이지 당 가져올 개수.
	 * @return ResponseEntity - 사용자 A가 팔로우한 사용자 리스트 페이지네이션 정보.
	 */
	@GetMapping("/{profileId}/following")
	public ResponseEntity<CustomPage<FollowUserDto>> getFollowing(
		@PathVariable String profileId,
		@RequestParam(defaultValue = "1") @Positive int page,
		@RequestParam(defaultValue = "20") @Positive int size) {
		CustomPage<FollowUserDto> followingPage = followService.getFollowingUsers(profileId, page, size);
		return ResponseEntity.ok(followingPage);
	}

	/**
	 * 사용자 A를 팔로우한 사용자 리스트 불러오는 메서드
	 * @param profileId
	 * 		사용자 A의 프로필 ID.
	 * @param page
	 *		페이지네이션 페이지.
	 * @param size
	 * 		페이지네이션 한 페이지 당 가져올 개수.
	 * @return ResponseEntity - 사용자 A를 팔로우한 사용자 리스트 페이지네이션 정보.
	 */
	@GetMapping("/{profileId}/followers")
	public ResponseEntity<CustomPage<FollowUserDto>> getFollowers(
		@PathVariable String profileId,
		@RequestParam(defaultValue = "1") @Positive int page,
		@RequestParam(defaultValue = "20") @Positive int size) {
		CustomPage<FollowUserDto> followerPage = followService.getFollowers(profileId, page, size);
		return ResponseEntity.ok(followerPage);
	}
}
