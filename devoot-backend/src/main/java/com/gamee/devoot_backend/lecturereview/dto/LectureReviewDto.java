package com.gamee.devoot_backend.lecturereview.dto;

import java.util.Date;

import com.gamee.devoot_backend.lecturereview.entity.LectureReview;

public record LectureReviewDto(
	long id,
	long lectureId,
	long userId,
	float rating,
	String content,
	Date createdAt,
	String profileId,
	String nickname,
	String imageUrl
) {
	public LectureReviewDto(LectureReview review, String profileId, String nickname, String imageUrl) {
		this(review.getId(),
			review.getLecture().getId(),
			review.getUserId(),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt(),
			profileId,
			nickname,
			imageUrl
		);
	}
}
