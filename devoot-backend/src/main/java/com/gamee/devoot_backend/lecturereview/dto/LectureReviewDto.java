package com.gamee.devoot_backend.lecturereview.dto;

import java.time.LocalDateTime;

import com.gamee.devoot_backend.lecturereview.entity.LectureReview;

public record LectureReviewDto(
	long id,
	long lectureId,
	long userId,
	float rating,
	String content,
	LocalDateTime createdAt,
	String profileId,
	String nickname,
	String imageUrl,
	String reviewName,
	String sourceUrl
) {
	public LectureReviewDto(LectureReview review, String profileId, String nickname, String imageUrl, String reviewName, String sourceUrl) {
		this(review.getId(),
			review.getLecture().getId(),
			review.getUserId(),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt(),
			profileId,
			nickname,
			imageUrl,
			reviewName,
			sourceUrl
		);
	}
}
