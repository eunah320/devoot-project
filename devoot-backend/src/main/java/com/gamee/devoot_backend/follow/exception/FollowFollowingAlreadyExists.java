package com.gamee.devoot_backend.follow.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class FollowFollowingAlreadyExists extends DevootException {
	public FollowFollowingAlreadyExists() {
		super(FollowErrorCode.FOLLOW_FOLLOWING_ALREADY_EXISTS);
	}
}
