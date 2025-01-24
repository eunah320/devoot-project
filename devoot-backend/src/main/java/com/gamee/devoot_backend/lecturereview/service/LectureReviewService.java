package com.gamee.devoot_backend.lecturereview.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.common.PageSizeDefine;
import com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto;
import com.gamee.devoot_backend.lecturereview.entity.LectureReview;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;
import com.gamee.devoot_backend.user.dao.UserRepository;

@Service
public class LectureReviewService {
	@Autowired
	private LectureReviewRepository lectureReviewRepository;
	@Autowired
	private UserRepository userRepository;

	/**
	 * 강의에 대한 리뷰들을 가져온다.
	 * @param lectureId
	 * 		- 리뷰를 가져올 강의 id
	 * @param page
	 * 		- 리뷰를 가져올 페이지 번호
	 * @return
	 * 		- 리뷰, 페이지 정보
	 */
	public Page<LectureReviewDto> getLectureReviewList(long lectureId, int page) {
		Pageable pageable = PageRequest.of(page - 1, PageSizeDefine.REVIEW_LECTURE);
		return lectureReviewRepository.selectAllByLectureId(lectureId, pageable);
	}

	public Page<LectureReviewDto> getLectureReviewByUserId(long userId, int page) {
		Pageable pageable = PageRequest.of(page - 1, PageSizeDefine.REVIEW_PROFILE);
		return lectureReviewRepository.selectAllByUserId(userId, pageable);
	}

	public void saveLectureReview(long userId, long lectureId, float rating, String content) {
		LectureReview lectureReview = LectureReview.builder()
			.lectureId(lectureId)
			.userId(userId)
			.rating(rating)
			.content(content)
			.build();
		lectureReview = lectureReviewRepository.save(lectureReview);
	}

	public void updateLectureReview(long userId, long id, float rating, String content) {
		Optional<LectureReview> reviewOptional = lectureReviewRepository.findById(id);
		if (reviewOptional.isPresent()) {
			LectureReview review = reviewOptional.get();
			if (userId == review.getUserId()) {
				review.setRating(rating);
				review.setContent(content);
				lectureReviewRepository.save(review);
			} else {
				// Permission Denied
				System.out.println("permission denied 예외 추가 필요");
			}
		} else {
			// not exist
			System.out.println("존재하지 않는 리뷰 예외 추가 필요");
		}
	}

	public void deleteLectureReview(long id) {
		lectureReviewRepository.deleteById(id);
	}
}
