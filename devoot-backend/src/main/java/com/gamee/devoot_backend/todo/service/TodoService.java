package com.gamee.devoot_backend.todo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.dto.TodoDetailDto;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.repository.TodoRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.exception.UserProfileIdMismatchException;

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

	public List<TodoDetailDto> getTodosOf(CustomUserDetails user, String profileId, LocalDate date) {
		checkUserHasReadAccessOf(user, profileId);

		List<Todo> todos = new ArrayList<>();

		Map<Long, Todo> todoMap = todoRepository.findTodosOf(user.id(), date).stream()
			.collect(Collectors.toMap(Todo::getId, todo -> todo));

		todoRepository.findFirstTodoOf(user.id(), date, false).ifPresent(firstUnfinishedTodo ->
			addTodosInOrder(firstUnfinishedTodo, todoMap, todos)
		);
		todoRepository.findFirstTodoOf(user.id(), date, true).ifPresent(firstFinishedTodo ->
			addTodosInOrder(firstFinishedTodo, todoMap, todos)
		);

		return todos.stream()
			.map(TodoDetailDto::of)
			.toList();
	}

	private void addTodosInOrder(Todo startTodo, Map<Long, Todo> todoMap, List<Todo> todos) {
		Todo currentTodo = startTodo;
		while (currentTodo != null) {
			todos.add(currentTodo);
			currentTodo = todoMap.get(currentTodo.getNextId());
		}
	}

	private void checkUserHasReadAccessOf(CustomUserDetails user, String profileId) {

	}

	private void checkUserMatchesProfileId(CustomUserDetails user, String profileId) {
		if (!user.profileId().equals(profileId)) {
			throw new UserProfileIdMismatchException();
		}
	}

}
