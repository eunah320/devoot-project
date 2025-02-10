package com.gamee.devoot_backend.lecture.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.entity.LectureReport;
import com.gamee.devoot_backend.lecture.exception.LectureAlreadyReportedException;
import com.gamee.devoot_backend.lecture.exception.LectureNotFoundException;
import com.gamee.devoot_backend.lecture.repository.LectureReportRepository;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;

@ExtendWith(MockitoExtension.class)
public class LectureServiceTest {
	@Mock
	private LectureRepository lectureRepository;

	@Mock
	private LectureReportRepository lectureReportRepository;

	@InjectMocks
	private LectureService lectureService;

	@Test
	@DisplayName("Test reportLecture() - successful")
	public void testReportLecture1() {
		// Given
		Long lectureId = 1L, userId = 2L;

		Lecture lecture = Lecture.builder().id(lectureId).build();

		when(lectureRepository.findById(lectureId))
			.thenReturn(Optional.of(lecture));
		when(lectureReportRepository.findByLectureIdAndUserId(lectureId, userId))
			.thenReturn(Optional.empty());

		// when
		lectureService.reportLecture(userId, lectureId);

		// Then
		verify(lectureReportRepository).save(any());
	}

	@Test
	@DisplayName("Test reportLecture() - throw LectureNotFoundException")
	public void testReportLecture2() {
		// Given
		Long lectureId = 1L, userId = 2L;

		when(lectureRepository.findById(lectureId))
			.thenReturn(Optional.empty());

		// when
		assertThrows(LectureNotFoundException.class,
			() -> lectureService.reportLecture(userId, lectureId));

		// Then
		verifyNoInteractions(lectureReportRepository);
	}

	@Test
	@DisplayName("Test reportLecture() - throw LectureAlreadyReportedException")
	public void testReportLecture3() {
		// Given
		Long lectureId = 1L, userId = 2L;

		Lecture lecture = Lecture.builder().id(lectureId).build();
		LectureReport lectureReport = LectureReport.builder().id(lectureId).userId(userId).build();
		when(lectureRepository.findById(lectureId))
			.thenReturn(Optional.of(lecture));
		when(lectureReportRepository.findByLectureIdAndUserId(lectureId, userId))
			.thenReturn(Optional.of(lectureReport));

		// when
		assertThrows(LectureAlreadyReportedException.class,
			() -> lectureService.reportLecture(userId, lectureId));

		// Then
		verify(lectureReportRepository, never()).save(any());
	}
}
