package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record LectureShortDetailDto(
	String name,
	String sourceName,
	String tags,
	String imgUrl,
	String curriculum
) {
	public static LectureShortDetailDto of(Lecture lecture) {
		return LectureShortDetailDto.builder()
			.name(lecture.getName())
			.sourceName(lecture.getSourceName())
			.imgUrl(lecture.getImageUrl())
			.tags(lecture.getTags())
			.curriculum(lecture.getCurriculum())
			.build();
	}
}
