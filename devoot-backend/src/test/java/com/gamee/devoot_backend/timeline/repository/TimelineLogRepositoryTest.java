package com.gamee.devoot_backend.timeline.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;
import com.gamee.devoot_backend.bookmark.repository.BookmarkLogRepository;
import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.timeline.entity.TimelineLog;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.entity.TodoLog;
import com.gamee.devoot_backend.todo.repository.TodoLogRepository;
import com.gamee.devoot_backend.todo.repository.TodoRepository;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureJson
public class TimelineLogRepositoryTest {
	@Autowired
	TimelineLogRepository timelineLogRepository;

	@Autowired
	TodoRepository todoRepository;

	@Autowired
	TodoLogRepository todoLogRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BookmarkRepository bookmarkRepository;

	@Autowired
	BookmarkLogRepository bookmarkLogRepository;

	@Autowired
	LectureRepository lectureRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	EntityManager em;

	@Test
	@DisplayName("Test findById()")
	public void testFindById() {
		// Given
		final User user = User.builder().uid("1234").build();
		userRepository.save(user);

		final Lecture lecture = Lecture.builder().build();
		lectureRepository.save(lecture);

		final Todo todo = Todo.builder()
			.userId(user.getId())
			.date(LocalDate.now())
			.lectureId(lecture.getId())
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(true)
			.nextId(null)
			.build();
		todoRepository.save(todo);

		final TodoLog todoLog = TodoLog.builder()
			.todoId(todo.getId())
			.userId(user.getId())
			.build();
		todoLogRepository.save(todoLog);

		final BookmarkLog bookmarkLog = BookmarkLog.builder()
			.beforeStatus(null)
			.afterStatus(1)
			.lectureId(lecture.getId())
			.userId(user.getId())
			.build();
		bookmarkLogRepository.save(bookmarkLog);

		em.flush();
		em.clear();

		// When
		Page<TimelineLog> logs = timelineLogRepository.getTimelineLogs(user.getId(), PageRequest.of(0, 10));

		// Then
		assertNotNull(logs);
		assertTrue(logs.hasContent(), "Logs should not be empty");
		logs.stream().forEach(log -> {
			try {
				String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(log);
				System.out.println(prettyJson);

				if (log instanceof TodoLog) {
					TodoLog todoLog1 = (TodoLog) log;
					assertEquals(todo.getId(), todoLog1.getTodoId(), "TodoLog should match the created todo");
				} else if (log instanceof BookmarkLog) {
					BookmarkLog bookmarkLog1 = (BookmarkLog) log;
					assertNotNull(bookmarkLog1.getAfterStatus(), "After status should not be null");
					assertEquals(1, bookmarkLog1.getAfterStatus(), "After status should match expected value");
				}
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
