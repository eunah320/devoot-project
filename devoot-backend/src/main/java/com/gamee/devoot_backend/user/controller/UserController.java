package com.gamee.devoot_backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.user.dto.UserRegistrationDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final FirebaseAuth firebaseAuth;
	private final UserService userService;

	@PostMapping
	public void loginUser(@RequestHeader("Authorization") String authorization) {
	}

	/**
	 * 사용자 회원가입 메서드.
	 *
	 * @param authorization
	 * 		클라이언트에서 전달된 인증 헤더 (Bearer 토큰 형식).
	 * @param userRegistrationDto
	 * 		사용자의 회원가입 정보를 담은 객체.
	 * @return 성공 시 등록된 사용자 정보 반환, 실패 시 에러 메시지 반환.
	 * @throws FirebaseAuthException 유효하지 않은 Firebase 토큰이 전달된 경우 발생.
	 */
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestHeader("Authorization") String authorization,
		@RequestBody UserRegistrationDto userRegistrationDto) {

		try {
			String token = authorization.substring(7);
			FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
			String uid = decodedToken.getUid();
			String email = decodedToken.getEmail();

			User user = userService.registerUser(uid, email, userRegistrationDto);
			return ResponseEntity.ok(user);

		} catch (FirebaseAuthException e) {
			return ResponseEntity.status(401).body("Invalid Firebase Token");
		}
	}
}
