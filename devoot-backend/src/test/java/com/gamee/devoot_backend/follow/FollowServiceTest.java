package com.gamee.devoot_backend.follow;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.gamee.devoot_backend.common.exception.DevootException;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.follow.dto.FollowUserDto;
import com.gamee.devoot_backend.follow.entity.Follow;
import com.gamee.devoot_backend.follow.exception.FollowCannotFollowSelfException;
import com.gamee.devoot_backend.follow.exception.FollowErrorCode;
import com.gamee.devoot_backend.follow.exception.FollowRelationshipNotFound;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.notification.repository.NotificationRepository;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserErrorCode;
import com.gamee.devoot_backend.user.exception.UserNotFoundException;
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
			.hasFieldOrPropertyWithValue("errorCode", FollowErrorCode.FOLLOW_RELATIONSHIP_ALREADY_EXISTS);
	}

	// 언팔로우

	/**
	 * ✅ 정상 언팔로우 테스트:
	 * 두 사용자가 모두 존재하고, 팔로우 관계가 존재할 때 해당 관계와 관련 알림 삭제
	 */
	@Test
	@DisplayName("Test deleteFollower() - successful unfollow")
	public void testDeleteFollower_Success() {
		// Given
		String followerProfileId = "userA";
		String followedProfileId = "userB";

		User followerUser = User.builder()
			.id(1L)
			.profileId(followerProfileId)
			.build();
		User followedUser = User.builder()
			.id(2L)
			.profileId(followedProfileId)
			.build();

		// 공통 사용자 조회 모킹
		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.of(followerUser));
		when(userRepository.findByProfileId(followedProfileId)).thenReturn(Optional.of(followedUser));

		// 존재하는 팔로우 관계 모킹
		Follow existingFollow = Follow.builder()
			.id(100L)
			.followerId(followerUser.getId())
			.followedId(followedUser.getId())
			.allowed(true)
			.build();
		when(followRepository.findByFollowerIdAndFollowedId(followerUser.getId(), followedUser.getId()))
			.thenReturn(Optional.of(existingFollow));

		// When
		followService.deleteFollower(followerProfileId, followedProfileId);

		// Then
		verify(followRepository, times(1)).delete(existingFollow);
		// 알림 삭제도 호출되었는지 검증 (deleteByFollowId 메서드가 있다고 가정)
		verify(notificationRepository, times(1)).deleteByFollowId(existingFollow.getId());
	}

	/**
	 * ❌ 팔로우 관계가 존재하지 않는 경우 테스트:
	 * 두 사용자가 존재하지만 팔로우 관계가 없으면 FollowRelationshipNotFound 예외 발생
	 */
	@Test
	@DisplayName("Test deleteFollower() - when follow relationship does not exist")
	public void testDeleteFollower_RelationshipNotFound() {
		// Given
		String followerProfileId = "userA";
		String followedProfileId = "userB";

		User followerUser = User.builder()
			.id(1L)
			.profileId(followerProfileId)
			.build();
		User followedUser = User.builder()
			.id(2L)
			.profileId(followedProfileId)
			.build();

		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.of(followerUser));
		when(userRepository.findByProfileId(followedProfileId)).thenReturn(Optional.of(followedUser));
		when(followRepository.findByFollowerIdAndFollowedId(followerUser.getId(), followedUser.getId()))
			.thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> followService.deleteFollower(followerProfileId, followedProfileId))
			.isInstanceOf(FollowRelationshipNotFound.class);

		verify(followRepository, times(1)).findByFollowerIdAndFollowedId(followerUser.getId(), followedUser.getId());
		verify(followRepository, never()).delete(any(Follow.class));
		verify(notificationRepository, never()).deleteByFollowId(anyLong());
	}

	/**
	 * ❌ 자신을 언팔로우하려는 경우 테스트:
	 * 팔로워와 팔로잉 대상이 동일하면 FollowCannotFollowSelfException 예외 발생
	 */
	@Test
	@DisplayName("Test deleteFollower() - when trying to unfollow oneself")
	public void testDeleteFollower_SelfUnfollow() {
		// Given
		String profileId = "userA";

		// When & Then
		assertThatThrownBy(() -> followService.deleteFollower(profileId, profileId))
			.isInstanceOf(FollowCannotFollowSelfException.class);

		verify(userRepository, never()).findByProfileId(any());
		verify(followRepository, never()).findByFollowerIdAndFollowedId(anyLong(), anyLong());
		verify(followRepository, never()).delete(any(Follow.class));
		verify(notificationRepository, never()).deleteByFollowId(anyLong());
	}

	/**
	 * ❌ 존재하지 않는 사용자를 언팔로우하려는 경우 테스트:
	 * 팔로워는 존재하지만 대상 사용자가 없으면 UserNotFoundException 예외 발생
	 */
	@Test
	@DisplayName("Test deleteFollower() - when following a non-existent user")
	public void testDeleteFollower_UserNotFound() {
		// Given
		String followerProfileId = "userA";
		String followedProfileId = "nonexistentUser";

		User followerUser = User.builder()
			.id(1L)
			.profileId(followerProfileId)
			.build();

		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.of(followerUser));
		when(userRepository.findByProfileId(followedProfileId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> followService.deleteFollower(followerProfileId, followedProfileId))
			.isInstanceOf(UserNotFoundException.class);

		verify(userRepository, times(1)).findByProfileId(followedProfileId);
		verify(followRepository, never()).findByFollowerIdAndFollowedId(anyLong(), anyLong());
		verify(followRepository, never()).delete(any(Follow.class));
		verify(notificationRepository, never()).deleteByFollowId(anyLong());
	}

	// 팔로워 팔로잉 리스트

	/**
	 * ✅ A의 팔로잉 목록을 가져오는 테스트
	 * A가 팔로우한 사용자 목록을 가져올 때, 정상적으로 B가 팔로잉 목록에 포함되어 있는지 확인
	 */
	@Test
	@DisplayName("Test getFollowingUsers() - A's following list")
	public void testGetFollowingUsers_A() {
		// Given
		String profileId = "userA";
		int page = 1, size = 20;
		int adjustedPage = page - 1; // 서비스에서 page - 1 적용

		User userA = User.builder().id(1L).profileId(profileId).build();
		User userB = User.builder().id(2L).profileId("userB").nickname("NicknameB").imageUrl("imageUrlB").build();

		Follow follow = Follow.builder()
			.followerId(userA.getId())
			.followedId(userB.getId())
			.followerUser(userA)
			.followedUser(userB)
			.allowed(true)
			.build();

		// Mock 설정
		when(userRepository.findByProfileId(profileId)).thenReturn(Optional.of(userA));
		when(followRepository.findFollowingUsersByFollowerId(eq(userA.getId()), eq(PageRequest.of(adjustedPage, size))))
			.thenReturn(new PageImpl<>(List.of(follow)));  // A는 B를 팔로우

		// When
		CustomPage<FollowUserDto> result = followService.getFollowingUsers(profileId, page, size);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().profileId()).isEqualTo("userB");
		assertThat(result.getContent().getFirst().nickname()).isEqualTo("NicknameB");
		assertThat(result.getContent().getFirst().imageUrl()).isEqualTo("imageUrlB");

		// Print for debugging
		result.getContent().forEach(dto -> System.out.println("A is following: " + dto.profileId()));
	}

	/**
	 * ❌ 존재하지 않는 사용자를 팔로잉 목록에서 조회할 때 UserNotFoundException 예외가 발생하는지 테스트
	 */
	@Test
	@DisplayName("Test getFollowingUsers() - UserNotFoundException")
	public void testGetFollowingUsers_UserNotFound() {
		// Given
		String profileId = "nonExistentUser";

		// Mock 설정
		when(userRepository.findByProfileId(profileId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> followService.getFollowingUsers(profileId, 1, 20))
			.isInstanceOf(UserNotFoundException.class);
	}

	/**
	 * ✅ B의 팔로워 목록을 가져오는 테스트
	 * B를 팔로우한 사용자 목록을 조회할 때, A와 C가 팔로워 목록에 포함되어 있는지 확인
	 */
	@Test
	@DisplayName("Test getFollowers() - B's follower list")
	public void testGetFollowers_B() {
		// Given
		String profileId = "userB";
		int page = 1, size = 20;
		int adjustedPage = page - 1;

		User userB = User.builder().id(2L).profileId(profileId).build();
		User userA = User.builder().id(1L).profileId("userA").nickname("NicknameA").imageUrl("imageUrlA").build();
		User userC = User.builder().id(3L).profileId("userC").nickname("NicknameC").imageUrl("imageUrlC").build();

		Follow followEntity1 = Follow.builder()
			.followerId(userA.getId())
			.followedId(userB.getId())
			.followerUser(userA)
			.followedUser(userB)
			.allowed(true)
			.build();

		Follow followEntity2 = Follow.builder()
			.followerId(userC.getId())
			.followedId(userB.getId())
			.followerUser(userC)
			.followedUser(userB)
			.allowed(true)
			.build();

		// Mock 설정
		when(userRepository.findByProfileId(profileId)).thenReturn(Optional.of(userB));
		when(followRepository.findFollowersByFollowedId(eq(userB.getId()), eq(PageRequest.of(adjustedPage, size))))
			.thenReturn(new PageImpl<>(List.of(followEntity1, followEntity2)));  // B의 팔로워는 A와 C

		// When
		CustomPage<FollowUserDto> result = followService.getFollowers(profileId, page, size);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).profileId()).isEqualTo("userA");
		assertThat(result.getContent().get(0).nickname()).isEqualTo("NicknameA");
		assertThat(result.getContent().get(0).imageUrl()).isEqualTo("imageUrlA");

		assertThat(result.getContent().get(1).profileId()).isEqualTo("userC");
		assertThat(result.getContent().get(1).nickname()).isEqualTo("NicknameC");
		assertThat(result.getContent().get(1).imageUrl()).isEqualTo("imageUrlC");

		// Print for debugging
		result.getContent().forEach(dto -> System.out.println("B's followers: " + dto.profileId()));
	}

	/**
	 * ❌ 존재하지 않는 사용자를 팔로워 목록에서 조회할 때 UserNotFoundException 예외가 발생하는지 테스트
	 */
	@Test
	@DisplayName("Test getFollowers() - UserNotFoundException")
	public void testGetFollowers_UserNotFound() {
		// Given
		String profileId = "nonExistentUser";

		// Mock 설정
		when(userRepository.findByProfileId(profileId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> followService.getFollowers(profileId, 1, 20))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("Test getFollowers() - Empty followers list")
	public void testGetFollowers_EmptyList() {
		// Given
		String profileId = "userA"; // A는 팔로워가 없음
		int page = 1, size = 20;
		int adjustedPage = page - 1;

		User userA = User.builder().id(1L).profileId(profileId).build();

		// Mock 설정
		when(userRepository.findByProfileId(profileId)).thenReturn(Optional.of(userA));

		Page<Follow> emptyPage = new PageImpl<>(List.of(), PageRequest.of(adjustedPage, size), 0);

		doReturn(emptyPage).when(followRepository).findFollowersByFollowedId(eq(userA.getId()), any(PageRequest.class));

		// When
		CustomPage<FollowUserDto> result = followService.getFollowers(profileId, page, size);

		// 디버깅 출력 추가
		System.out.println("Followers count: " + result.getTotalElements());

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEmpty();
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getTotalPages()).isEqualTo(0);
	}
}
