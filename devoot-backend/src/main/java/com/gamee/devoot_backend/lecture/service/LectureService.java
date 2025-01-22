package com.gamee.devoot_backend.lecture.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.lecture.dto.LectureDetail;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;

@Service
public class LectureService {
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private LectureReviewRepository lectureReviewRepository;

	public LectureDetail getLectureDetail(long id) {
		Optional<Lecture> lectureOptional = lectureRepository.findById(id);
		if (lectureOptional.isPresent()) {
			Lecture lecture = lectureOptional.get();
			Float rating = lectureReviewRepository.findAvgByLectureId(lecture.getId());
			if (rating == null) {
				rating = 0f;
			}
			// 강의 찜하기 기능 구현 시 찜 개수 불러올 수 있도록 변경 필요
			//LectureDetail lectureDetail = new LectureDetail(lecture, BookmarkRepository.countByLectureId(lecture.getId()), rating);
			return new LectureDetail(lecture, 0, rating);
		}
		return null;
	}
}
