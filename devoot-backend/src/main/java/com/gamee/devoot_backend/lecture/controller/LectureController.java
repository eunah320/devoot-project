package com.gamee.devoot_backend.lecture.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.lecture.dto.LectureDetail;
import com.gamee.devoot_backend.lecture.service.LectureService;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {
	@Autowired
	private LectureService lectureService;

	@GetMapping("/{lectureId}")
	public ResponseEntity<Map<String, Object>> getLectureDetail(@PathVariable(value = "lectureId", required = false) String lectureIdStr) {
		Map<String, Object> resultMap = new HashMap<>();
		LectureDetail lectureDetail = lectureService.getLectureDetail(Long.parseLong(lectureIdStr));
		if (lectureDetail == null) {
			resultMap.put("error", "존재하지 않는 강의입니다.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMap);
		}
		resultMap.put("lectureDetail", lectureDetail);
		return ResponseEntity.status(HttpStatus.OK).body(resultMap);
	}

	@GetMapping("/{lectureId}/curriculum")
	public ResponseEntity<Map<String, String>> getLectureCurriculum(@PathVariable(value = "lectureId", required = false) String lectureIdStr) {
		Map<String, String> resultMap = new HashMap<>();
		LectureDetail lectureDetail = lectureService.getLectureDetail(Long.parseLong(lectureIdStr));
		if (lectureDetail == null) {
			resultMap.put("error", "존재하지 않는 강의입니다.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMap);
		}
		resultMap.put("curriculum", lectureDetail.getCurriculum());
		return ResponseEntity.status(HttpStatus.OK).body(resultMap);
	}
}
