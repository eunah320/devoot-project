package com.gamee.devoot_backend.todo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.follow.exception.FollowRequestPendingException;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.todo.dto.TodoContributionDetailDto;
import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.dto.TodoDetailDto;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.repository.TodoContributionRepository;
import com.gamee.devoot_backend.todo.repository.TodoRepository;
import com.gamee.devoot_backend.user.dao.UserRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserNotFoundException;
import com.gamee.devoot_backend.user.exception.UserProfileIdMismatchException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
	private final TodoRepository todoRepository;
	private final FollowRepository followRepository;
	private final UserRepository userRepository;
	private final TodoContributionRepository todoContributionRepository;

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
		checkUserHasReadAccess(user, profileId);

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

	public List<TodoContributionDetailDto> getTodoContributionsOf(CustomUserDetails user, String profileId, Integer year) {
		User followedUser = validateAccessAndFetchFollowedUser(user, profileId);

		return todoContributionRepository.findAllByUserIdAndYear(followedUser.getId(), year)
			.stream()
			.map(TodoContributionDetailDto::of)
			.toList();
	}
	private void addTodosInOrder(Todo startTodo, Map<Long, Todo> todoMap, List<Todo> todos) {
		Todo currentTodo = startTodo;
		while (currentTodo != null) {
			todos.add(currentTodo);
			currentTodo = todoMap.get(currentTodo.getNextId());
		}
	}

	private User validateAccessAndFetchFollowedUser(CustomUserDetails user, String profileId) {
		User followedUser = userRepository.findByProfileId(profileId)
			.orElseThrow(() -> new UserNotFoundException(String.format("User of %s not found", profileId)));

		// 자기 자신이 아니고, 상대방이 공개 계정이 아닐 경우에만 follow 요청 확인
		if (!user.id().equals(followedUser.getId()) && !followedUser.getIsPublic()) {
			followRepository.findIfAllowed(user.id(), followedUser.getId())
				.orElseThrow(FollowRequestPendingException::new);
		}

		return followedUser;
	}

	private void checkUserMatchesProfileId(CustomUserDetails user, String profileId) {
		if (!user.profileId().equals(profileId)) {
			throw new UserProfileIdMismatchException();
		}
	}

}
