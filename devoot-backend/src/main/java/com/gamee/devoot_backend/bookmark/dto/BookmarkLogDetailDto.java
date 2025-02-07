package com.gamee.devoot_backend.bookmark.dto;

import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;
import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record BookmarkLogDetailDto(
	Integer beforeStatus,
	Integer afterStatus,
	Lecture lecture
) {
	public static BookmarkLogDetailDto of(BookmarkLog bookmarkLog) {
		return BookmarkLogDetailDto.builder()
			.beforeStatus(bookmarkLog.getBeforeStatus())
			.beforeStatus(bookmarkLog.getBeforeStatus())
			.lecture(bookmarkLog.getLecture())
			.build();
	}
}
