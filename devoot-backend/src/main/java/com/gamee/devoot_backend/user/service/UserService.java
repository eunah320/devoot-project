package com.gamee.devoot_backend.user.service;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gamee.devoot_backend.user.dto.UserRegistrationDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserAlreadyExistsException;
import com.gamee.devoot_backend.user.exception.UserProfileIdAlreadyExistsException;
import com.gamee.devoot_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public boolean existsUserByUid(String uid) {
		return userRepository.existsByUid(uid);
	}

	public Optional<User> findUserByUid(String uid) {
		return userRepository.findByUid(uid);
	}

	public boolean existsByProfileId(String profileId) {
		return userRepository.existsByProfileId(profileId);
	}

	@Transactional
	public User registerUser(String uid, String email, UserRegistrationDto userRegistrationDto, MultipartFile file) {
		if (existsUserByUid(uid)) {
			throw new UserAlreadyExistsException();
		}

		if (userRegistrationDto.profileId() != null
			&& existsByProfileId(userRegistrationDto.profileId())) {
			throw new UserProfileIdAlreadyExistsException();
		}
		String imageUrl = handleProfileImage(file);

		User newUser = User.builder()
			.uid(uid)
			.email(email)
			.profileId(userRegistrationDto.profileId())
			.nickname(userRegistrationDto.nickname())
			.links(userRegistrationDto.links())
			.isPublic(userRegistrationDto.isPublic())
			.imageUrl(imageUrl)
			.tags(userRegistrationDto.tags())
			.build();

		return userRepository.save(newUser);
	}

	private String handleProfileImage(MultipartFile file) {
		if (file != null && !file.isEmpty()) {
			// TODO: S3 연동 로직 추가 예정
			System.out.println("Received file: " + file.getOriginalFilename());
		}

		// 임시 URL 반환
		return "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS7LpapIl8DITfz4_Y2z7pqs7FknPkjReAZCg&s";
	}
}
