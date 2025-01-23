package com.gamee.devoot_backend.lecturereview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.common.PageSizeDefine;
import com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto;
import com.gamee.devoot_backend.lecturereview.entity.LectureReview;
import com.gamee.devoot_backend.lecturereview.service.LectureReviewService;

@RestController
@RequestMapping("/api/reviews")
public class LectureReviewController {
	@Autowired
	private LectureReviewService lectureReviewService;

	@GetMapping("/lectures/{lectureId}")
	public ResponseEntity<Page<LectureReviewDto>> getReviewListByLectureId(@PathVariable(value = "lectureId") Long lectureId, @RequestParam(value = "page", defaultValue = "1") int page) {
		Page<LectureReviewDto> lectureReviewDtoPage = lectureReviewService.getLectureReviewList(lectureId, page);
		return ResponseEntity.status(HttpStatus.OK).body(lectureReviewDtoPage);
	}

	@GetMapping("/profiles/{userId}")
	public ResponseEntity<Page<LectureReviewDto>> getReviewListByUserId(@PathVariable(value = "userId")Long userId, @RequestParam(value = "page", defaultValue = "1") int page) {
		Page<LectureReviewDto> lectureReviewDtoPage = lectureReviewService.getLectureReviewByUserId(userId, page);
		return ResponseEntity.status(HttpStatus.OK).body(lectureReviewDtoPage);
	}
}
