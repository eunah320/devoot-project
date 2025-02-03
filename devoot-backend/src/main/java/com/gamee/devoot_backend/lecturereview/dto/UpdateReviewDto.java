package com.gamee.devoot_backend.lecturereview.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateReviewDto(
	@NotNull(message = "lectureId must not be null.")
	Long lectureId,
	String content,

	@NotNull(message = "rating must not be null.")
	Float rating
) {
}
