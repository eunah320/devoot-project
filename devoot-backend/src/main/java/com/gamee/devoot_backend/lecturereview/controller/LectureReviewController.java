package com.gamee.devoot_backend.lecturereview.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto;
import com.gamee.devoot_backend.lecturereview.service.LectureReviewService;

/**
 * 강의 리뷰와 관련된 요청
 */
@RestController
@RequestMapping("/api/reviews")
public class LectureReviewController {
	@Autowired
	private LectureReviewService lectureReviewService;

	/**
	 * 특정 강의의 리뷰 목록을 반환
	 * @param lectureId
	 * - 리뷰를 가져올 강의 id
	 * @param page
	 * - 보여줄 리뷰 페이지 번호
	 * @return
	 * - 리뷰와 페이지 정보가 담긴 Page 객체
	 */
	@GetMapping("/lectures/{lectureId}")
	public ResponseEntity<Page<LectureReviewDto>> getReviewListByLectureId(@PathVariable(value = "lectureId") Long lectureId, @RequestParam(value = "page", defaultValue = "1") int page) {
		Page<LectureReviewDto> lectureReviewDtoPage = lectureReviewService.getLectureReviewList(lectureId, page);
		return ResponseEntity.status(HttpStatus.OK).body(lectureReviewDtoPage);
	}

	/**
	 * 단일 유저가 작성한 리뷰 목록을 반환
	 * @param userId
	 * - 리뷰를 작성한 사용자의 id
	 * @param page
	 * - 리뷰를 표시할 page 정보
	 * @return
	 * - 리뷰와 페이지 정보가 담긴 Page 객체
	 */
	@GetMapping("/profiles/{userId}")
	public ResponseEntity<Page<LectureReviewDto>> getReviewListByUserId(@PathVariable(value = "userId")Long userId, @RequestParam(value = "page", defaultValue = "1") int page) {
		Page<LectureReviewDto> lectureReviewDtoPage = lectureReviewService.getLectureReviewByUserId(userId, page);
		return ResponseEntity.status(HttpStatus.OK).body(lectureReviewDtoPage);
	}

	@PostMapping
	public ResponseEntity<Map<String, String>> uploadReview(@RequestBody Map<String, Object> requestMap) {
		// userId 받아오는 부분 구현 필요
		long userId = 4;
		long lectureId = Long.parseLong((String)requestMap.get("lectureId"));
		float rating = Float.parseFloat((String)requestMap.get("rating"));
		String content = (String)requestMap.get("content");
		lectureReviewService.saveLectureReview(userId, lectureId, rating, content);

		return ResponseEntity.ok(null);
	}
}
