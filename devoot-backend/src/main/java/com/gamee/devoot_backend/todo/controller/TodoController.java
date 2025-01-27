package com.gamee.devoot_backend.todo.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.service.TodoService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/users/{profileId}/todos")
@RestController
@RequiredArgsConstructor
public class TodoController {
	private final TodoService todoService;

	@PostMapping
	public ResponseEntity<?> createTodo(
			@AuthenticationPrincipal CustomUserDetails user,
			@PathVariable String profileId,
			@RequestBody TodoCreateDto dto) {
		todoService.createTodo(user, profileId, dto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/move-undone")
	public ResponseEntity<?> moveUndone(
			@AuthenticationPrincipal CustomUserDetails user,
			@PathVariable String profileId,
			@RequestParam(value = "date", required = true) LocalDate date) {
		todoService.moveUndone(user, profileId, date);
		return ResponseEntity.ok().build();
	}
}
