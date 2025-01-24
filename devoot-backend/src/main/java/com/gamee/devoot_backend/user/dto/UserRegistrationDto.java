package com.gamee.devoot_backend.user.dto;

public record UserRegistrationDto(
	String profileId,
	String nickname,
	String links,
	Boolean isPublic,
	String imageUrl,
	String tags
) {
}
