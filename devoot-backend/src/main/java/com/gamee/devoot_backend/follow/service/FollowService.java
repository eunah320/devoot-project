package com.gamee.devoot_backend.follow.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.follow.entity.Follow;
import com.gamee.devoot_backend.follow.entity.Notification;
import com.gamee.devoot_backend.follow.exception.FollowCannotFollowSelfException;
import com.gamee.devoot_backend.follow.exception.FollowFollowingAlreadyExists;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.follow.repository.NotificationRepository;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserNotFoundException;
import com.gamee.devoot_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository followRepository;
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createFollower(String followerProfileId, String followedProfileId) {
		if (followerProfileId.equals(followedProfileId)) {
			throw new FollowCannotFollowSelfException();
		}

		// 팔로워 유저 조회
		User followerUser = userRepository.findByProfileId(followerProfileId)
			.orElseThrow(UserNotFoundException::new);
		// 팔로잉 대상 유저 조회
		User followedUser = userRepository.findByProfileId(followedProfileId)
			.orElseThrow(UserNotFoundException::new);
		Long followerId = followerUser.getId();
		Long followedId = followedUser.getId();

		// 이미 팔로우하고 있는지 확인
		followRepository.findByFollowerIdAndFollowedId(followerId, followedId)
			.ifPresent(follow -> {
				throw new FollowFollowingAlreadyExists();
			});
		// 팔로우 생성
		Boolean isAllowed = followedUser.getIsPublic();
		Follow follow =  Follow.builder()
			.followerId(followerId)
			.followedId(followedId)
			.allowed(isAllowed)
			.build();
		Follow savedFollow = followRepository.save(follow);

		// 알림 생성
		Notification notification = Notification.builder()
			.toUser(followedId)
			.fromUser(followerId)
			.followId(savedFollow.getId())
			.hasRead(false)
			.build();
		notificationRepository.save(notification);
	}
}
