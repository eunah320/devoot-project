package com.gamee.devoot_backend.lecture.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.lecture.dto.LectureDetail;
import com.gamee.devoot_backend.lecture.service.LectureService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {
	@Autowired
	private LectureService lectureService;

	@GetMapping("/{lectureId}")
	public ResponseEntity<Map<String, Object>> getLectureDetail(@PathVariable(value = "lectureId") String lectureIdStr,
															@AuthenticationPrincipal CustomUserDetails user) {
		Map<String, Object> resultMap = new HashMap<>();
		LectureDetail lectureDetail = lectureService.getLectureDetail(Long.parseLong(lectureIdStr), user);
		resultMap.put("lectureDetail", lectureDetail);
		return ResponseEntity.status(HttpStatus.OK).body(resultMap);
	}

	@GetMapping("/{lectureId}/curriculum")
	public ResponseEntity<Map<String, String>> getLectureCurriculum(@PathVariable(value = "lectureId") String lectureIdStr) {
		Map<String, String> resultMap = new HashMap<>();
		LectureDetail lectureDetail = lectureService.getLectureDetail(Long.parseLong(lectureIdStr), null);
		resultMap.put("curriculum", lectureDetail.curriculum());
		return ResponseEntity.status(HttpStatus.OK).body(resultMap);
	}

	@PostMapping("/{lectureId}/report")
	public ResponseEntity<?> reportLecture(
		@PathVariable Long lectureId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		lectureService.reportLecture(user.id(), lectureId);
		return ResponseEntity.noContent().build();
	}
}
