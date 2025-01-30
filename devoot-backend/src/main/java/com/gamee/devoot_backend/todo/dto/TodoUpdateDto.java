package com.gamee.devoot_backend.todo.dto;

import java.util.Optional;

import com.gamee.devoot_backend.todo.entity.Todo;

public record TodoUpdateDto(
	Boolean finished,
	Long nextId
) {
	public Todo toEntity() {
		Todo todo = new Todo();

		Optional.ofNullable(finished).ifPresent(todo::setFinished);
		todo.setNextId(nextId);

		return todo;
	}
}
