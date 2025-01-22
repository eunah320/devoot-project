package com.gamee.devoot_backend.lecturereview.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.lecturereview.entity.LectureReview;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;

@Service
public class LectureReviewService {
	@Autowired
	private LectureReviewRepository lectureReviewRepository;
	public List<LectureReview> getLectureReviewList(Long lectureId) {
		if (lectureId == null) {
			return new ArrayList<>();
		}
		return lectureReviewRepository.findAllByLectureId(lectureId);
	}
}
