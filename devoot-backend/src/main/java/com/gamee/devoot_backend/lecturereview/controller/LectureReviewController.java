package com.gamee.devoot_backend.lecturereview.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.lecture.service.LectureService;
import com.gamee.devoot_backend.lecturereview.entity.LectureReview;
import com.gamee.devoot_backend.lecturereview.service.LectureReviewService;

@RestController
@RequestMapping("/api/lecture/reviews")
public class LectureReviewController {
	@Autowired
	private LectureReviewService lectureReviewService;

	@GetMapping
	public ResponseEntity<Map<String, Object>> getReviewList(@RequestParam(value = "lectureId", required = false) Long lectureId) {
		Map<String, Object> resultMap = new HashMap<>();
		List<LectureReview> lectureReviewList = lectureReviewService.getLectureReviewList(lectureId);
		resultMap.put("review", lectureReviewList);
		return ResponseEntity.status(HttpStatus.OK).body(resultMap);
	}
}
