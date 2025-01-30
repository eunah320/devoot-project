package com.gamee.devoot_backend.todo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.follow.exception.FollowRequestPendingException;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.todo.dto.TodoContributionDetailDto;
import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.dto.TodoDetailDto;
import com.gamee.devoot_backend.todo.dto.TodoUpdateDto;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.exception.TodoNotFoundException;
import com.gamee.devoot_backend.todo.exception.TodoPermissionDeniedException;
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

	@Transactional
	public void createTodo(CustomUserDetails user, String profileId, TodoCreateDto dto) {
		checkUserMatchesProfileId(user, profileId);

		Todo newTodo = dto.toEntity();
		newTodo.setUserId(user.id());
		newTodo.setNextId(-1L);
		todoRepository.save(newTodo);

		todoRepository.findByUserIdAndFinishedAndNextId(user.id(), dto.finished(), null)
			.ifPresent(beforeTodo -> {
				beforeTodo.setNextId(newTodo.getId());
				todoRepository.save(beforeTodo);
			});

		newTodo.setNextId(null);
		todoRepository.save(newTodo);
	}

	@Transactional
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
		User followedUser = validateAccessAndFetchFollowedUser(user, profileId);

		List<Todo> todos = new ArrayList<>();

		Map<Long, Todo> todoMap = todoRepository.findTodosOf(followedUser.getId(), date).stream()
			.collect(Collectors.toMap(Todo::getId, todo -> todo));

		todoRepository.findFirstTodoOf(followedUser.getId(), date, false).ifPresent(firstUnfinishedTodo ->
			addTodosInOrder(firstUnfinishedTodo, todoMap, todos)
		);
		todoRepository.findFirstTodoOf(followedUser.getId(), date, true).ifPresent(firstFinishedTodo ->
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

	@Transactional
	public void updateTodo(CustomUserDetails user, String profileId, Long todoId, TodoUpdateDto dto) {
		checkUserMatchesProfileId(user, profileId);
		Todo todo = checkUserIsAllowedAndFetchTodo(user, todoId);
		Todo updatedTodo = dto.toEntity();
		updatedTodo.setId(todoId);

		// update order
		if (todo.getNextId() != -1) {
			Optional<Todo> beforeTodoOptional = todoRepository.findByUserIdAndFinishedAndNextId(user.id(), todo.getFinished(), todoId);
			if (beforeTodoOptional.isPresent()) {
				Todo beforeTodo = beforeTodoOptional.get();
				beforeTodo.setNextId(todo.getNextId());
				todoRepository.save(beforeTodo);
			}
			Optional<Todo> newBeforeTodoOptional = todoRepository.findByUserIdAndFinishedAndNextId(user.id(), dto.finished(), dto.nextId());
			if (newBeforeTodoOptional.isPresent()) {
				Todo newBeforeTodo = newBeforeTodoOptional.get();
				newBeforeTodo.setNextId(todo.getId());
				todoRepository.save(newBeforeTodo);
			}
			todo.setNextId(dto.nextId());
			todoRepository.save(todo);
		}

		// update contribution
		if (!todo.getFinished() && updatedTodo.getFinished()) {
			todoContributionRepository.insertOrIncrementContribution(user.id(), todo.getDate());
		}
		if (todo.getFinished() && !updatedTodo.getFinished()) {
			todoContributionRepository.decrementContribution(user.id(), todo.getDate());
			todoContributionRepository.deleteContributionIfZero(user.id(), todo.getDate());
		}
	}

	@Transactional
	public void deleteTodo(CustomUserDetails user, String profileId, Long todoId) {
		Todo todo = checkUserIsAllowedAndFetchTodo(user, todoId);
		if (todo.getFinished()) {
			todoContributionRepository.decrementContribution(user.id(), todo.getDate());
			todoContributionRepository.deleteContributionIfZero(user.id(), todo.getDate());
		}
		todoRepository.delete(todo);
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

	private Todo checkUserIsAllowedAndFetchTodo(CustomUserDetails user, Long todoId) {
		Todo todo = todoRepository.findById(todoId)
			.orElseThrow(TodoNotFoundException::new);
		if (todo.getUserId() != user.id()) {
			throw new TodoPermissionDeniedException();
		}
		return todo;
	}

}
