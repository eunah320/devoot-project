package com.gamee.devoot_backend.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.todo.entity.TodoLog;

public interface TodoLogRepository extends JpaRepository<TodoLog, Long> {
}
