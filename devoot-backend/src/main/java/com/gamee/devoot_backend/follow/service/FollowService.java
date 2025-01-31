package com.gamee.devoot_backend.follow.service;

import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.follow.exception.FollowRequestPendingException;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserNotFoundException;
import com.gamee.devoot_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	public User validateAccessAndFetchFollowedUser(CustomUserDetails user, String profileId) {
		User followedUser = userRepository.findByProfileId(profileId)
			.orElseThrow(() -> new UserNotFoundException(String.format("User of %s not found", profileId)));

		// 자기 자신이 아니고, 상대방이 공개 계정이 아닐 경우에만 follow 요청 확인
		if (!user.id().equals(followedUser.getId()) && !followedUser.getIsPublic()) {
			followRepository.findIfAllowed(user.id(), followedUser.getId())
				.orElseThrow(FollowRequestPendingException::new);
		}

		return followedUser;
	}
}
