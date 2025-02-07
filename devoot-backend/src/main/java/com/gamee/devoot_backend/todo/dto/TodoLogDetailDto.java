package com.gamee.devoot_backend.todo.dto;

import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.entity.TodoLog;

import lombok.Builder;

@Builder
public record TodoLogDetailDto(
	Todo todo
) {
	public static TodoLogDetailDto of(TodoLog todoLog) {
		return TodoLogDetailDto.builder()
			.todo(todoLog.getTodo())
			.build();
	}
}
