package com.gamee.devoot_backend.follow;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gamee.devoot_backend.common.exception.DevootException;
import com.gamee.devoot_backend.follow.entity.Follow;
import com.gamee.devoot_backend.follow.exception.FollowErrorCode;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.follow.repository.NotificationRepository;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserErrorCode;
import com.gamee.devoot_backend.user.repository.UserRepository;

/**
 * FollowService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

	@Mock
	private FollowRepository followRepository;

	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private FollowService followService;

	/**
	 * ✅ 공개 계정을 팔로우하는 경우 테스트 (알림 저장 확인 추가)
	 */
	@Test
	public void testCreateFollower_PublicAccount() {
		// Given
		String followerProfileId = "userA";
		String followedProfileId = "userB";

		User followerUser = User.builder().id(1L).profileId(followerProfileId).build();
		User followedUser = User.builder().id(2L).profileId(followedProfileId).isPublic(true).build();

		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.of(followerUser));
		when(userRepository.findByProfileId(followedProfileId)).thenReturn(Optional.of(followedUser));
		when(followRepository.findByFollowerIdAndFollowedId(followerUser.getId(), followedUser.getId()))
			.thenReturn(Optional.empty());

		Follow savedFollow = Follow.builder()
			.id(100L)
			.followerId(followerUser.getId())
			.followedId(followedUser.getId())
			.allowed(true)
			.build();

		when(followRepository.save(any(Follow.class))).thenReturn(savedFollow);

		// When
		followService.createFollower(followerProfileId, followedProfileId);

		// Then (알림이 정상적으로 저장되었는지 검증)
		verify(notificationRepository, times(1)).save(argThat(notification ->
			notification.getToUser().equals(followedUser.getId()) &&
				notification.getFromUser().equals(followerUser.getId()) &&
				notification.getFollowId().equals(savedFollow.getId()) &&
				!notification.getHasRead()
		));
	}


	/**
	 * ✅ 비공개 계정을 팔로우하는 경우 테스트
	 */
	@Test
	public void testCreateFollower_PrivateAccount() {
		// Given
		String followerProfileId = "userA";
		String followedProfileId = "userC";

		User followerUser = User.builder().id(1L).profileId(followerProfileId).build();
		User followedUser = User.builder().id(3L).profileId(followedProfileId).isPublic(false).build();

		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.of(followerUser));
		when(userRepository.findByProfileId(followedProfileId)).thenReturn(Optional.of(followedUser));
		when(followRepository.findByFollowerIdAndFollowedId(followerUser.getId(), followedUser.getId()))
			.thenReturn(Optional.empty());

		Follow savedFollow = Follow.builder()
			.id(101L)
			.followerId(followerUser.getId())
			.followedId(followedUser.getId())
			.allowed(false) // 비공개 계정이므로 승인 대기
			.build();

		when(followRepository.save(any(Follow.class))).thenReturn(savedFollow);

		// When
		followService.createFollower(followerProfileId, followedProfileId);

		// Then (알림이 정상적으로 저장되었는지 검증)
		verify(notificationRepository, times(1)).save(argThat(notification ->
			notification.getToUser().equals(followedUser.getId()) &&
				notification.getFromUser().equals(followerUser.getId()) &&
				notification.getFollowId().equals(savedFollow.getId()) &&
				!notification.getHasRead()
		));
	}


	/**
	 * ❌ 자신을 팔로우하려는 경우 테스트
	 */
	@Test
	public void testCreateFollower_SelfFollow() {
		String profileId = "userA";

		assertThatThrownBy(() -> followService.createFollower(profileId, profileId))
			.isInstanceOf(DevootException.class)
			.hasFieldOrPropertyWithValue("errorCode", FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF);
	}

	/**
	 * ❌ 존재하지 않는 사용자를 팔로우하려는 경우 테스트
	 */
	@Test
	public void testCreateFollower_UserNotFound() {
		String followerProfileId = "userA";
		String followedProfileId = "nonexistentUser";

		User followerUser = User.builder().id(1L).profileId(followerProfileId).build();

		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.of(followerUser));
		when(userRepository.findByProfileId(followedProfileId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> followService.createFollower(followerProfileId, followedProfileId))
			.isInstanceOf(DevootException.class)
			.hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
	}

	/**
	 * ❌ 이미 팔로우하고 있는 사용자를 다시 팔로우하려는 경우 테스트
	 */
	@Test
	public void testCreateFollower_AlreadyFollowing() {
		String followerProfileId = "userA";
		String followedProfileId = "userB";

		User followerUser = User.builder().id(1L).profileId(followerProfileId).build();
		User followedUser = User.builder().id(2L).profileId(followedProfileId).isPublic(true).build();

		Follow existingFollow = Follow.builder().id(102L).followerId(followerUser.getId()).followedId(followedUser.getId()).allowed(true).build();

		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.of(followerUser));
		when(userRepository.findByProfileId(followedProfileId)).thenReturn(Optional.of(followedUser));
		when(followRepository.findByFollowerIdAndFollowedId(followerUser.getId(), followedUser.getId()))
			.thenReturn(Optional.of(existingFollow));

		assertThatThrownBy(() -> followService.createFollower(followerProfileId, followedProfileId))
			.isInstanceOf(DevootException.class)
			.hasFieldOrPropertyWithValue("errorCode", FollowErrorCode.FOLLOW_FOLLOWING_ALREADY_EXISTS);
	}
}
