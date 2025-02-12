package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record LectureSearchDetailDto(
	String category,
	String tags,
	String name,
	String lecturer,
	int currentPrice,
	int originPrice,
	String sourceName,
	String imageUrl,
	Float rating
) {
	public static LectureSearchDetailDto of(Lecture lecture) {
		return LectureSearchDetailDto.builder()
			.category(lecture.getCategory())
			.tags(lecture.getTags())
			.name(lecture.getName())
			.lecturer(lecture.getLecturer())
			.currentPrice(lecture.getCurrentPrice())
			.originPrice(lecture.getOriginalPrice())
			.sourceName(lecture.getSourceName())
			.imageUrl(lecture.getImageUrl())
			.rating(lecture.getReviewCnt() == 0 ? null : lecture.getRatingSum() / lecture.getReviewCnt())
			.build();
	}
}
