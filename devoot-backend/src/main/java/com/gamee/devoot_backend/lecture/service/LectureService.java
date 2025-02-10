package com.gamee.devoot_backend.lecture.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.lecture.dto.LectureDetail;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.entity.LectureReport;
import com.gamee.devoot_backend.lecture.exception.LectureAlreadyReportedException;
import com.gamee.devoot_backend.lecture.exception.LectureNotFoundException;
import com.gamee.devoot_backend.lecture.repository.LectureReportRepository;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;

@Service
public class LectureService {
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private LectureReportRepository lectureReportRepository;
	@Autowired
	private LectureReviewRepository lectureReviewRepository;
	@Autowired
	private BookmarkRepository bookmarkRepository;

	public LectureDetail getLectureDetail(Long id) {
		Optional<Lecture> lectureOptional = lectureRepository.findById(id);
		if (lectureOptional.isPresent()) {
			Lecture lecture = lectureOptional.get();
			Float rating = lectureReviewRepository.findAvgByLectureId(lecture.getId());
			if (rating == null) {
				rating = 0f;
			}
			long count = bookmarkRepository.countByLectureId(lecture.getId());
			return new LectureDetail(lecture, count, rating);
		}
		throw new LectureNotFoundException();
	}

	public void reportLecture(Long userId, Long lectureId) {
		lectureRepository.findById(lectureId)
			.orElseThrow(() -> new LectureNotFoundException());

		lectureReportRepository.findByLectureIdAndUserId(lectureId, userId)
			.ifPresent(report -> {
				throw new LectureAlreadyReportedException();
			});

		lectureReportRepository.save(
			LectureReport.builder()
				.lectureId(lectureId)
				.userId(userId)
				.build()
		);
	}
}
