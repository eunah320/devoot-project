package com.gamee.devoot_backend.lecturereview.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LectureReviewReportErrorCode implements ErrorCode {
	SELF_REPORT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "REVIEW_400_1", "User cannot report oneself."),
	USER_ALREADY_REPORTED(HttpStatus.CONFLICT, "REVIEW_409_1", "User already report this lecture review");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
