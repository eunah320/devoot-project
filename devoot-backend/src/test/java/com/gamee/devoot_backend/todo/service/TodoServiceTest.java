package com.gamee.devoot_backend.todo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.repository.TodoRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {
	@Mock
	TodoRepository todoRepository;

	@InjectMocks
	TodoService todoService;

	CustomUserDetails user = new CustomUserDetails(1L, null, null, true);

	TodoCreateDto createDto = new TodoCreateDto(1L, LocalDate.now(), "lecture", "sublecture", "www.hello.com", false);

	@Test
	@DisplayName("Test createTodo() - when there is no before todos")
	public void testCreateTodo1() {
		// Given
		Todo newTodo = createDto.toEntity();
		newTodo.setUserId(user.id());
		when(todoRepository.findByUserIdAndFinishedAndNextId(user.id(), false, null))
			.thenReturn(Optional.empty());

		// When
		todoService.createTodo(user, createDto);

		// Then
		verify(todoRepository).save(newTodo);
		verify(todoRepository, times(1)).save(any(Todo.class));
	}

	@Test
	@DisplayName("Test createTodo() - when before todos found")
	public void testCreateTodo2() {
		// Given
		Todo newTodo = createDto.toEntity();
		newTodo.setUserId(user.id());

		Todo beforeTodo = Todo.builder()
			.userId(user.id())
			.finished(createDto.finished())
			.nextId(null)
			.build();

		when(todoRepository.findByUserIdAndFinishedAndNextId(user.id(), createDto.finished(), null))
			.thenReturn(Optional.of(beforeTodo));

		// When
		todoService.createTodo(user, createDto);

		// Then
		verify(todoRepository).save(newTodo);
		verify(todoRepository).save(beforeTodo);
		verify(todoRepository, times(2)).save(any(Todo.class));

		assertEquals(newTodo.getId(), beforeTodo.getNextId());
	}

	@Test
	@DisplayName("Test moveUndone() - when there are no todos to move")
	public void testMoveUndone1() {
		LocalDate date = LocalDate.now();
		LocalDate nextDay = date.plusDays(1);

		// Given
		when(todoRepository.findLastTodoOf(user.id(), date, false))
			.thenReturn(Optional.empty());
		when(todoRepository.findFirstTodoOf(user.id(), nextDay, false))
			.thenReturn(Optional.empty());

		// When
		todoService.moveUndone(user, date);

		// Then
		verify(todoRepository, never()).save(any());
		verify(todoRepository, never()).updateUnfinishedTodosToNextDay(anyLong(), any(), any());
	}

	@Test
	@DisplayName("Test moveUndone() - when there are todos to move, but no todos on the next day")
	public void testMoveUndone2() {
		LocalDate date = LocalDate.now();
		LocalDate nextDay = date.plusDays(1);

		Todo lastNewTodo = Todo.builder()
			.userId(user.id())
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(null)
			.build();

		// Given
		when(todoRepository.findLastTodoOf(user.id(), date, false))
			.thenReturn(Optional.of(lastNewTodo));
		when(todoRepository.findFirstTodoOf(user.id(), nextDay, false))
			.thenReturn(Optional.empty());

		// When
		todoService.moveUndone(user, date);

		// Then
		verify(todoRepository, never()).save(any());
		verify(todoRepository, times(1)).updateUnfinishedTodosToNextDay(user.id(), date, nextDay);
	}

	@Test
	@DisplayName("Test moveUndone() - when there are todos to move and todos of next day")
	public void testMoveUndone3() {
		LocalDate date = LocalDate.now();
		LocalDate nextDay = LocalDate.now().plusDays(1);

		Todo lastNewTodo = Todo.builder()
			.userId(user.id())
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(null)
			.build();
		Todo firstExistingTodo = Todo.builder()
			.userId(user.id())
			.date(nextDay)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(null)
			.build();

		// Given
		when(todoRepository.findLastTodoOf(user.id(), date, false))
			.thenReturn(Optional.of(lastNewTodo));
		when(todoRepository.findFirstTodoOf(user.id(), nextDay, false))
			.thenReturn(Optional.of(lastNewTodo));

		// When
		todoService.moveUndone(user, date);

		// Then
		assertEquals(firstExistingTodo.getId(), lastNewTodo.getNextId());
		verify(todoRepository).save(lastNewTodo);
		verify(todoRepository).updateUnfinishedTodosToNextDay(user.id(), date, nextDay);
	}
}
