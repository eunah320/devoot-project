package com.gamee.devoot_backend.user.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409_1", "User already exists"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_1", "User not found");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
