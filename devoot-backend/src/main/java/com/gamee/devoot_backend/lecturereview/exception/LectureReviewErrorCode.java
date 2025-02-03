package com.gamee.devoot_backend.lecturereview.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LectureReviewErrorCode implements ErrorCode {
	PERMISSION_DENIED(HttpStatus.FORBIDDEN, "REVIEW_403_1", "You do not have permission to access this resource.");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
