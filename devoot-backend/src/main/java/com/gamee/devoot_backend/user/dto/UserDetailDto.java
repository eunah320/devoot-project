package com.gamee.devoot_backend.user.dto;

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
	Long bookmarkCnt
) {
	public static UserDetailDto of(CustomUserDetails user, Long followingCnt, Long followerCnt, Long bookmarkCnt) {
		return UserDetailDto.builder()
			.id(user.id())
			.email(user.email())
			.profileId(user.profileId())
			.nickname(user.nickname())
			.links(user.links())
			.isPublic(user.isPublic())
			.imageUrl(user.imageUrl())
			.tags(user.tags())
			.followingCnt(followingCnt)
			.followerCnt(followerCnt)
			.bookmarkCnt(bookmarkCnt)
			.build();

	}
}
