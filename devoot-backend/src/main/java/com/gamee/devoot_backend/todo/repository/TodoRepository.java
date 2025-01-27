package com.gamee.devoot_backend.todo.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.todo.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
	Optional<Todo> findByUserIdAndFinishedAndNextId(Long userId, Boolean finished, Long nextId);

	@Query("""
		SELECT t
		FROM Todo t
		WHERE t.userId = :userId
		AND t.date = :date
		AND t.finished = :finished
		AND t.id NOT IN (
			SELECT t2.nextId
			FROM Todo t2
			WHERE t2.userId = :userId
			AND t2.date = :date
			AND t2.finished = :finished
			AND t2.nextId IS NOT NULL
		)
		""")
	Optional<Todo> findFirstTodoOf(Long userId, LocalDate date, Boolean finished);

	@Query("""
		SELECT t
		FROM Todo t
		WHERE t.userId = :userId
		AND t.date = :date
		AND t.finished = :finished
		AND t.nextId IS NULL
		""")
	Optional<Todo> findLastTodoOf(Long userId, LocalDate date, Boolean finished);

	@Transactional
	@Modifying
	@Query("""
		UPDATE Todo t
		SET t.date = :nextDay
		WHERE t.userId = :userId
		AND t.date = :date
		AND t.finished = false
		""")
	int updateUnfinishedTodosToNextDay(Long userId, LocalDate date, LocalDate nextDay);
}
