package com.gamee.devoot_backend.user.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.user.dao.UserRepository;
import com.gamee.devoot_backend.user.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
	private final FirebaseAuth firebaseAuth;
	private final UserRepository userRepository;

	@PostMapping("/login")
	public ResponseEntity<User> authenticateGoogle(@RequestBody String idToken) {
		try {
			// Firebase ID 토큰 검증
			FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
			String email = decodedToken.getEmail();

			// DB에서 사용자 확인 또는 신규 사용자 등록
			Optional<User> existingUser = userRepository.findByEmail(email);
			User user = existingUser.orElseGet(() -> {
				User newUser = User.builder()
					.email(email)
					.profileId(decodedToken.getUid()) // UID를 profileId로 설정
					.nickname(null) // 기본값
					.links(null) // 기본값
					.isPublic(true) // 기본값
					.imageUrl(null) // 기본값
					.build();
				return userRepository.save(newUser);
			});

			return ResponseEntity.ok(user);

		} catch (Exception e) {
			return ResponseEntity.status(401).body(null);
		}
	}

	@PutMapping("/user/{id}")
	public ResponseEntity<User> updateUserInfo(@PathVariable Long id, @RequestBody User userDetails) {
		return userRepository.findById(id)
			.map(user -> {
				user.setNickname(userDetails.getNickname());
				user.setLinks(userDetails.getLinks());
				user.setIsPublic(userDetails.getIsPublic());
				user.setImageUrl(userDetails.getImageUrl());
				return ResponseEntity.ok(userRepository.save(user));
			})
			.orElse(ResponseEntity.notFound().build());
	}
}
