package com.gamee.devoot_backend.bookmark.dto;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.lecture.dto.LectureShortDetailDto;

import lombok.Builder;

@Builder
public record BookmarkDetailDto(
	Long id,
	Long userId,
	Integer status,
	Long nextId,
	LectureShortDetailDto lecture
) {
	public static BookmarkDetailDto of(Bookmark bookmark) {
		return BookmarkDetailDto.builder()
			.id(bookmark.getId())
			.userId(bookmark.getUserId())
			.lecture(LectureShortDetailDto.of(bookmark.getLecture()))
			.status(bookmark.getStatus())
			.nextId(bookmark.getNextId())
			.build();
	}
}
