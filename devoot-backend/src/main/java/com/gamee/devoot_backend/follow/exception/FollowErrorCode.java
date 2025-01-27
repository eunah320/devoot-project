package com.gamee.devoot_backend.follow.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowErrorCode implements ErrorCode {
	FOLLOW_REQUEST_PENDING(HttpStatus.FORBIDDEN, "USER_403_1", "User's follow request is still pending approval.");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
