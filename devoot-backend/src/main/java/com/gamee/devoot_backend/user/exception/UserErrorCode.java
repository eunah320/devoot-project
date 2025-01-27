package com.gamee.devoot_backend.user.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409_1", "User already exists"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_1", "User not found"),
	USER_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "USER_401_1", "Firebase token has expired. Please reauthenticate."),
	USER_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "USER_401_2", "Invalid Firebase token. Please provide a valid token."),
	USER_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "USER_401_3", "Firebase authentication failed.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	public static UserErrorCode fromFirebaseErrorCode(String errorCode) {
		if (errorCode == null) return USER_AUTHENTICATION_FAILED;

		switch (errorCode) {
			case "ID_TOKEN_EXPIRED":
				return USER_TOKEN_EXPIRED;
			case "INVALID_ID_TOKEN":
				return USER_INVALID_TOKEN;
			default:
				return USER_AUTHENTICATION_FAILED;
		}
	}
}
