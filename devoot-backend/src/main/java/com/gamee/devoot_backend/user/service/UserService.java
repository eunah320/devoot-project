package com.gamee.devoot_backend.user.service;

import java.util.Map;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.dto.UserDetailDto;
import com.gamee.devoot_backend.user.dto.UserRegistrationDto;
import com.gamee.devoot_backend.user.dto.UserSearchDetailDto;
import com.gamee.devoot_backend.user.dto.UserUpdateDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserAlreadyExistsException;
import com.gamee.devoot_backend.user.exception.UserNotFoundException;
import com.gamee.devoot_backend.user.exception.UserProfileIdAlreadyExistsException;
import com.gamee.devoot_backend.user.exception.UserProfileIdMismatchException;
import com.gamee.devoot_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public boolean existsUserByUid(String uid) {
		return userRepository.existsByUid(uid);
	}

	public boolean existsByProfileId(String profileId, CustomUserDetails userDetails) {
		if (userDetails != null && (userDetails.profileId().equals(profileId))) {
			return false;
		}
		return userRepository.existsByProfileId(profileId);
	}

	@Transactional
	public User registerUser(String uid, UserRegistrationDto userRegistrationDto, MultipartFile file) {
		if (existsUserByUid(uid)) {
			throw new UserAlreadyExistsException();
		}

		if (userRegistrationDto.profileId() != null
			&& existsByProfileId(userRegistrationDto.profileId(), null)) {
			throw new UserProfileIdAlreadyExistsException();
		}
		String imageUrl = handleProfileImage(file);

		User newUser = User.builder()
			.uid(uid)
			.email(userRegistrationDto.email())
			.profileId(userRegistrationDto.profileId())
			.nickname(userRegistrationDto.nickname())
			.links(userRegistrationDto.links())
			.isPublic(userRegistrationDto.isPublic())
			.imageUrl(imageUrl)
			.tags(userRegistrationDto.tags())
			.build();

		return userRepository.save(newUser);
	}

	public UserDetailDto getUserInfo(CustomUserDetails userDetails, String profileId) {
		User user = userRepository.findByProfileId(profileId)
			.orElseThrow(() -> new UserNotFoundException(profileId));

		Map<String, Long> userStats = userRepository.getUserStatsAsMap(user.getId());

		String isFollowing = null;
		if (!user.getId().equals(userDetails.id())) {
			isFollowing = userRepository.isFollowing(userDetails.id(), user.getId())
				.orElse("NOTFOLLOWING");
		}

		return UserDetailDto.of(
			user,
			userStats.get("followingCnt"),
			userStats.get("followerCnt"),
			userStats.get("bookmarkCnt"),
			isFollowing
		);

	}

	@Transactional
	public User updateUser(Long userId, UserUpdateDto userUpdateDto, MultipartFile file) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (!user.getProfileId().equals(userUpdateDto.profileId())
			&& userRepository.existsByProfileId(userUpdateDto.profileId())) {
			throw new UserProfileIdAlreadyExistsException();
		}

		user.setProfileId(userUpdateDto.profileId());
		user.setNickname(userUpdateDto.nickname());
		user.setLinks(userUpdateDto.links());
		user.setIsPublic(userUpdateDto.isPublic());
		user.setTags(userUpdateDto.tags());

		if (file != null && !file.isEmpty()) {
			String imageUrl = handleProfileImage(file);
			user.setImageUrl(imageUrl);
		}
		return userRepository.save(user);
	}

	public void checkUserMatchesProfileId(CustomUserDetails user, String profileId) {
		if (!user.profileId().equals(profileId)) {
			throw new UserProfileIdMismatchException();
		}
	}

	private String handleProfileImage(MultipartFile file) {
		if (file != null && !file.isEmpty()) {
			// TODO: S3 연동 로직 추가 예정
			System.out.println("Received file: " + file.getOriginalFilename());
		}

		// 임시 URL 반환
		return "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS7LpapIl8DITfz4_Y2z7pqs7FknPkjReAZCg&s";
	}

	public CustomPage<UserSearchDetailDto> searchByPrefix(String query, int page, int size) {
		return new CustomPage<>(
			userRepository.searchByPrefix(query, PageRequest.of(page - 1, size))
				.map(UserSearchDetailDto::of)
		);
	}
}
