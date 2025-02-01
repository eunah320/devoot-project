package com.gamee.devoot_backend.user.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.dto.UserRegistrationDto;
import com.gamee.devoot_backend.user.dto.UserUpdateDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.firebase.FirebaseService;
import com.gamee.devoot_backend.user.service.UserService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final FirebaseService firebaseService;
	private final UserService userService;

	/**
	 * 회원가입 시 profile ID 중복 체크 메서드.
	 *
	 * @param profileId
	 * 		중복 확인하고자하는 profile ID.
	 * @return
	 * 	    true: profile ID가 사용 가능 (중복되지 않음).
	 *   	false: profile ID가 이미 사용 중 (중복됨).
	 */
	@GetMapping("/check-profile-id")
	public ResponseEntity<Boolean> checkProfileId(
		@RequestParam String profileId
	) {
		boolean isAvailable = !userService.existsByProfileId(profileId, null);
		return ResponseEntity.ok(isAvailable);
	}

	/**
	 * 로그인 시 profile ID 중복 체크 메서드.
	 *
	 * @param profileId
	 * 		중복 확인하고자하는 profile ID.
	 * @param userDetails
	 * 		현재 인증된 사용자 정보를 나타내는 객체.
	 * @return
	 * 		true: profile ID 사용 가능 (본인의 ID or 중복되지 않음).
	 * 		false: profile ID가 이미 다른 사용자에 의해 사용 중 (중복됨).
	 */
	@GetMapping("/check-profile-id/authenticated")
	public ResponseEntity<Boolean> checkProfileIdAuthenticated(
		@RequestParam String profileId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		boolean isAvailable = !userService.existsByProfileId(profileId, userDetails);
		return ResponseEntity.ok(isAvailable);
	}

	/**
	 * 사용자 회원가입 메서드.
	 *
	 * @param authorizationHeader
	 * 		Firebase 토큰 포함, 해당 토큰을 검증하여 사용자 인증.
	 * @param userRegistrationDto
	 * 		등록하려는 사용자의 상세 정보.
	 * @return 생성된 사용자 정보(CustomUserDetails)를 포함한 HTTP 응답.
	 * 		성공 시 상태코드 201 Created.
	 */
	@PostMapping(value = "/register", consumes = { "multipart/form-data" })
	public ResponseEntity<?> registerUser(
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
		@RequestPart("user") @Valid UserRegistrationDto userRegistrationDto,
		@RequestPart(value = "file", required = false) MultipartFile file) {
		var decoded = firebaseService.parseToken(authorizationHeader);

		User newUser = userService.registerUser(decoded.uid(), userRegistrationDto, file);
		CustomUserDetails userDetails = new CustomUserDetails(newUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(userDetails);
	}

	/**
	 * 현재 인증된 사용자의 정보 조회하는 메서드.
	 *
	 * @param userDetails
	 * 		현재 인증된 사용자 정보를 나타내는 객체.
	 * @return 현재 인증된 사용자의 정보(CustomUserDetails).
	 * 		성공 시 상태코드 200 OK 반환.
	 */
	@GetMapping("/me")
	public ResponseEntity<CustomUserDetails> getMyInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(userDetails);
	}

	/**
	 * 현재 인증된 사용자의 상세 정보 수정하는 메서드.
	 *
	 * @param userDetails
	 * 		현재 인증된 사용자 정보를 나타내는 객체.
	 * @param userUpdateDto
	 * 		수정할 사용자 정보.
	 * @param file
	 * 		프로필 이미지(옵션).
	 * @return 수정된 사용자 정보(CustomUserDetails)
	 */
	@PatchMapping(value = "/me", consumes = {"multipart/form-data"})
	public ResponseEntity<CustomUserDetails> updateMyInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestPart("user") @Valid UserUpdateDto userUpdateDto,
		@RequestPart(value = "file", required = false) MultipartFile file
	) {
		User updatedUser = userService.updateUser(userDetails.id(), userUpdateDto, file);
		return ResponseEntity.ok(new CustomUserDetails(updatedUser));
	}
}
