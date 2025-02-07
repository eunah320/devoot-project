package com.gamee.devoot_backend.timeline.dto;

import java.time.LocalDateTime;

import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;
import com.gamee.devoot_backend.timeline.entity.TimelineLog;
import com.gamee.devoot_backend.todo.entity.TodoLog;
import com.gamee.devoot_backend.user.entity.User;

import lombok.Builder;

@Builder
public record TimelineLogDetailDto(
	Long id,
	User user,
	String type,
	LocalDateTime createdAt,
	Object log
) {
	public static TimelineLogDetailDto of(TimelineLog timelineLog) {
		String type = null;
		if (timelineLog instanceof BookmarkLog) {
			type = "BOOKMARK";
		} else if (timelineLog instanceof TodoLog) {
			type = "BOOKMARK";
		}

		return TimelineLogDetailDto.builder()
			.id(timelineLog.getId())
			.user(timelineLog.getUser())
			.type(type)
			.log(timelineLog.getLogData())
			.build();
	}
}
