package com.gamee.devoot_backend.follow.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowErrorCode implements ErrorCode {
	FOLLOW_FOLLOWING_ALREADY_EXISTS(HttpStatus.CONFLICT, "FOLLOW_409_1", "Already following this user."),
	FOLLOW_CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "FOLLOW_400_1", "Can't follow yourself.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
