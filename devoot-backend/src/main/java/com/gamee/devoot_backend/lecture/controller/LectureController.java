package com.gamee.devoot_backend.lecture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.lecture.service.LectureService;

@RestController("/api/lectures")
public class LectureController {
	@SuppressWarnings("checkstyle:RegexpMultiline")
	@Autowired
	private LectureService lectureService;
}
