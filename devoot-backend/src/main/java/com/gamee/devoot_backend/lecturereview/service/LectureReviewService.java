package com.gamee.devoot_backend.lecturereview.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.common.pageutils.PageSizeDefine;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.lecture.exception.LectureNotFoundException;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto;
import com.gamee.devoot_backend.lecturereview.entity.LectureReview;
import com.gamee.devoot_backend.lecturereview.exception.LectureReviewNotFoundException;
import com.gamee.devoot_backend.lecturereview.exception.ReviewPermissionDeniedException;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.repository.UserRepository;

@Service
public class LectureReviewService {
	@Autowired
	private LectureReviewRepository lectureReviewRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FollowRepository followRepository;
	@Autowired
	private LectureRepository lectureRepository;

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

	public Page<LectureReviewDto> getLectureReviewByProfileId(String profileId, int page, long currentUserId) {
		Pageable pageable = PageRequest.of(page - 1, PageSizeDefine.REVIEW_PROFILE);
		Optional<User> userOptional = userRepository.findByProfileId(profileId);
		long userId = -1;
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			userId = user.getId();
			if (userId != currentUserId
				&& !user.getIsPublic()
				&& followRepository.findIfAllowed(currentUserId, userId).isEmpty()) {
				throw new ReviewPermissionDeniedException();
			}
		}
		return lectureReviewRepository.selectAllByUserId(userId, pageable);
	}

	public void saveLectureReview(long userId, long lectureId, float rating, String content) {
		lectureRepository.findById(lectureId)
			.orElseThrow(() -> new LectureNotFoundException());
		LectureReview lectureReview = LectureReview.builder()
			.lectureId(lectureId)
			.userId(userId)
			.rating(rating)
			.content(content)
			.build();
		lectureReviewRepository.save(lectureReview);
		lectureRepository.incrementReviewStats(lectureId, rating);
	}

	public void updateLectureReview(long userId, long id, float rating, String content) {
		LectureReview review = checkUserIsAllowedAndFetchReview(userId, id);

		lectureRepository.updateReviewStats(review.getLectureId(), review.getRating(), rating);

		review.setRating(rating);
		review.setContent(content);
		lectureReviewRepository.save(review);
	}

	public void deleteLectureReview(long id, long userId) {
		LectureReview review = checkUserIsAllowedAndFetchReview(userId, id);

		lectureReviewRepository.deleteById(id);
		lectureRepository.decrementReviewStats(review.getLectureId(), review.getRating());
	}

	LectureReview checkUserIsAllowedAndFetchReview(Long userId, Long id) {
		LectureReview lectureReview = lectureReviewRepository.findById(id)
			.orElseThrow(() -> new LectureReviewNotFoundException());
		if (!userId.equals(lectureReview.getUserId())) {
			throw new ReviewPermissionDeniedException();
		}
		return lectureReview;
	}
}
