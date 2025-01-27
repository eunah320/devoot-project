package com.gamee.devoot_backend.user.exception;

import com.gamee.devoot_backend.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_PROFILE_ID_MISMATCH(HttpStatus.FORBIDDEN, "USER_403_1", "Requested user doesn't match profile id");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
