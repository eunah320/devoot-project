package com.gamee.devoot_backend.lecturereview.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gamee.devoot_backend.lecturereview.entity.LectureReview;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class LectureReviewControllerTest {
	@Autowired
	LectureReviewRepository lectureReviewRepository;

	@Test
	public void saveTest() {
		LectureReview lectureReview = LectureReview.builder()
			.lectureId(1219)
			.userId(7)
			.rating(1.5f)
			.content("이 강의는 매우 형편없습니다!!")
			.build();
		lectureReview = lectureReviewRepository.save(lectureReview);
		System.out.println(lectureReview);
	}

	@Test
	public void modifyTest() {
		Optional<LectureReview> review = lectureReviewRepository.findById(10L);
		System.out.println(review);
		Optional<LectureReview> reviewOptional = lectureReviewRepository.update(10, 5.0f, "사실정말재미있는!");
		if (reviewOptional.isPresent()) {
			review = reviewOptional.get();
			System.out.println(review);
		}
	}
}
