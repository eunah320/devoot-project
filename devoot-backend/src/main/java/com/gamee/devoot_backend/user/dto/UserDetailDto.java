package com.gamee.devoot_backend.user.dto;

import com.gamee.devoot_backend.user.entity.User;

import lombok.Builder;

@Builder
public record UserDetailDto(
	Long id,
	String email,
	String profileId,
	String nickname,
	String links,
	Boolean isPublic,
	String imageUrl,
	String tags,
	Long followingCnt,
	Long followerCnt,
	Long bookmarkCnt,
	String isFollowing
) {
	public static UserDetailDto of(User user, Long followingCnt, Long followerCnt, Long bookmarkCnt,
		String isFollowing) {
		return UserDetailDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.profileId(user.getProfileId())
			.nickname(user.getNickname())
			.links(user.getLinks())
			.isPublic(user.getIsPublic())
			.imageUrl(user.getImageUrl())
			.tags(user.getTags())
			.followingCnt(followingCnt)
			.followerCnt(followerCnt)
			.bookmarkCnt(bookmarkCnt)
			.isFollowing(isFollowing)
			.build();
	}
}
