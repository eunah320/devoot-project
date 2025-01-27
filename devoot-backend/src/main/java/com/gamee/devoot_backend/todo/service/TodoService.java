package com.gamee.devoot_backend.todo.service;

import java.time.LocalDate;
import java.util.Optional;

import com.gamee.devoot_backend.user.exception.UserProfileIdMismatchException;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.repository.TodoRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
	private final TodoRepository todoRepository;

	public void createTodo(CustomUserDetails user, String profileId, TodoCreateDto dto) {
		checkUserMatchesProfileId(user, profileId);

		Todo newTodo = dto.toEntity();
		newTodo.setUserId(user.id());
		todoRepository.save(newTodo);

		todoRepository.findByUserIdAndFinishedAndNextId(user.id(), dto.finished(), null)
			.ifPresent(beforeTodo -> {
				beforeTodo.setNextId(newTodo.getId());
				todoRepository.save(beforeTodo);
			});
	}

	public void moveUndone(CustomUserDetails user, String profileId, LocalDate date) {
		checkUserMatchesProfileId(user, profileId);

		LocalDate nextDay = date.plusDays(1);
		Optional<Todo> lastNewTodoOptional = todoRepository.findLastTodoOf(user.id(), date, false);
		Optional<Todo> firstExistingTodoOptional = todoRepository.findFirstTodoOf(user.id(), nextDay, false);

		if (lastNewTodoOptional.isPresent() && firstExistingTodoOptional.isPresent()) {
			Todo lastNewTodo = lastNewTodoOptional.get();
			Todo firstExistingTodo = firstExistingTodoOptional.get();

			lastNewTodo.setNextId(firstExistingTodo.getId());
			todoRepository.save(lastNewTodo);
		}

		if (lastNewTodoOptional.isPresent()) {
			todoRepository.updateUnfinishedTodosToNextDay(user.id(), date, nextDay);
		}
	}

	private void checkUserMatchesProfileId(CustomUserDetails user, String profileId) {
		if (!user.profileId().equals(profileId)) {
			throw new UserProfileIdMismatchException();
		}
	}

}
