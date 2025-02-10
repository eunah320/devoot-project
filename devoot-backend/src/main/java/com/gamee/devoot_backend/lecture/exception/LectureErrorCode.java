package com.gamee.devoot_backend.lecture.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LectureErrorCode implements ErrorCode {
	PERMISSION_DENIED(HttpStatus.FORBIDDEN, "REVIEW_403_1", "You do not have permission to access this resource."),
	PRIVATE_ACCOUNT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "LECTURE_403_1", "You do not have permission to access this user"),
	LECTURE_NOT_EXIST(HttpStatus.NOT_FOUND, "LECTURE_404_1", "Lecture does not exist."),
	USER_ALREADY_REPORTED(HttpStatus.CONFLICT, "LECTURE_409_1", "User already reported this lecture review");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
