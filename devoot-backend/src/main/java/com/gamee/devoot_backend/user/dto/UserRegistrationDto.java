package com.gamee.devoot_backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.gamee.devoot_backend.user.validator.ValidLinks;
import com.gamee.devoot_backend.user.validator.ValidTags;

public record UserRegistrationDto(
	@NotBlank(message = "profileId cannot be blank")
	String profileId,
	@NotNull(message = "nickname is required")
	@Size(min = 6, max = 20, message = "nickname must be between 6 and 20 characters")
	String nickname,
	@ValidLinks
	String links,
	@NotNull(message = "isPublic is required")
	Boolean isPublic,
	@ValidTags
	String tags
) {
}
