package com.gamee.devoot_backend.lecture.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.lecture.dto.LectureDetail;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.exception.LectureNotFoundException;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

@Service
public class LectureService {
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private LectureReviewRepository lectureReviewRepository;
	@Autowired
	private BookmarkRepository bookmarkRepository;

	public LectureDetail getLectureDetail(Long id, CustomUserDetails user) {
		Optional<Lecture> lectureOptional = lectureRepository.findById(id);
		if (lectureOptional.isPresent()) {
			Lecture lecture = lectureOptional.get();
			float rating = 0;
			if (lecture.getReviewCnt() != 0) {
				rating = lecture.getRatingSum() / (float)lecture.getReviewCnt();
			}
			long count = bookmarkRepository.countByLectureId(lecture.getId());
			if (user == null || bookmarkRepository.findByUserIdAndLectureId(user.id(), id).isEmpty()) {
				return new LectureDetail(lecture, count, rating, false);
			}
			return new LectureDetail(lecture, count, rating, true);
		}
		throw new LectureNotFoundException();
	}
}
