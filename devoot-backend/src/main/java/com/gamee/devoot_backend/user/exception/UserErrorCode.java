package com.gamee.devoot_backend.user.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_1", "User is not found"),
	USER_PROFILE_ID_MISMATCH(HttpStatus.FORBIDDEN, "USER_403_1", "Requested user doesn't match profile id");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
