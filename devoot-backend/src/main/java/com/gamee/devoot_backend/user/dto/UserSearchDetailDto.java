package com.gamee.devoot_backend.user.dto;

import com.gamee.devoot_backend.user.entity.User;

import lombok.Builder;

@Builder
public record UserSearchDetailDto(
	Long id,
	String profileId,
	String nickname,
	String imageUrl
) {
	public static UserSearchDetailDto of(User user) {
		return UserSearchDetailDto.builder()
			.id(user.getId())
			.profileId(user.getProfileId())
			.nickname(user.getNickname())
			.imageUrl(user.getImageUrl())
			.build();
	}
}
