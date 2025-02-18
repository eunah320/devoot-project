package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record LectureSearchDetailDto(
	String category,
	String tags,
	String name,
	String lecturer,
	Integer currentPrice,
	Integer originPrice,
	String sourceName,
	String imageUrl,
	Integer reviewCnt,
	Float rating
) {
	public static LectureSearchDetailDto of(Lecture lecture) {
		float ratingValue = (lecture.getReviewCnt() != null && lecture.getReviewCnt() > 0)
			? (lecture.getRatingSum() / lecture.getReviewCnt())
			: 0f;

		return LectureSearchDetailDto.builder()
			.category(lecture.getCategory())
			.tags(lecture.getTags())
			.name(lecture.getName())
			.lecturer(lecture.getLecturer())
			.currentPrice(lecture.getCurrentPrice())
			.originPrice(lecture.getOriginalPrice())
			.sourceName(lecture.getSourceName())
			.imageUrl(lecture.getImageUrl())
			.reviewCnt(lecture.getReviewCnt())
			.rating(ratingValue)
			.build();
	}
}
