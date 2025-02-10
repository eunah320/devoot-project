package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.lecture.entity.Lecture;

public record LectureDetail(
	String name,
	String lecturer,
	int currentPrice,
	int originPrice,
	String sourceName,
	String sourceUrl,
	String imgUrl,
	String curriculum,
	long bookmarkCount,
	float rating,
	boolean isBookmarked
) {
	public LectureDetail(Lecture lecture, long bookmarkCount, float rating, boolean isBookmarked) {
		this(
			lecture.getName(),
			lecture.getLecturer(),
			lecture.getCurrentPrice(),
			lecture.getOriginalPrice(),
			lecture.getSourceName(),
			lecture.getSourceUrl(),
			lecture.getImageUrl(),
			lecture.getCurriculum(),
			bookmarkCount,
			rating,
			isBookmarked
		);
	}
}
