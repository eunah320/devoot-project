package com.gamee.devoot_backend.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.user.dto.UserRegistrationDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public Optional<User> findUserByUid(String uid) {
		return userRepository.findByUid(uid);
	}

	@Transactional
	public User registerUser(String uid, String email, UserRegistrationDto userRegistrationDto) {
		User user = new User();
		user.setUid(uid);
		user.setEmail(email);
		user.setProfileId(userRegistrationDto.profileId());
		user.setNickname(userRegistrationDto.nickname());
		user.setLinks(userRegistrationDto.links());
		user.setIsPublic(userRegistrationDto.isPublic());
		user.setImageUrl(userRegistrationDto.imageUrl());
		user.setTags(userRegistrationDto.tags());

		return userRepository.save(user);
	}
}
