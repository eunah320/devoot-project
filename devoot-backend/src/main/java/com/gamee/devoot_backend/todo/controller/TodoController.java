package com.gamee.devoot_backend.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.service.TodoService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/todos")
@RestController
@RequiredArgsConstructor
public class TodoController {
	private final TodoService todoService;

	@PostMapping
	public ResponseEntity<?> createTodo(@AuthenticationPrincipal CustomUserDetails user, @RequestBody TodoCreateDto dto) {
		todoService.createTodo(user, dto);
		return ResponseEntity.ok().build();
	}
}
