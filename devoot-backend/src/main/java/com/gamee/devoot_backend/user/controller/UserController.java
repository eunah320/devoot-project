package com.gamee.devoot_backend.user.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.user.dao.UserRepository;
import com.gamee.devoot_backend.user.dto.TokenRequest;
import com.gamee.devoot_backend.user.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final FirebaseAuth firebaseAuth;
	private final UserRepository userRepository;

	@PostMapping("/")
	public ResponseEntity<User> authenticateGoogle(@RequestBody TokenRequest tokenRequest) {
		try {
			// Firebase ID 토큰 검증
			String token = tokenRequest.getToken();
			FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
			String email = decodedToken.getEmail();
			String uid = decodedToken.getUid();

			// DB에서 사용자 확인 또는 신규 사용자 등록
			Optional<User> existingUser = userRepository.findByUid(uid);
			User user = existingUser.orElseGet(() -> {
				User newUser = User.builder()
					.email(email)
					.uid(uid)
					.profileId(null)
					.nickname(null)
					.links(null)
					.isPublic(true)
					.imageUrl(null)
					.build();
				return userRepository.save(newUser);
			});

			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.status(401).body(null);
		}
	}
}
