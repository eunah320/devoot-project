package com.gamee.devoot_backend.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gamee.devoot_backend.todo.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
