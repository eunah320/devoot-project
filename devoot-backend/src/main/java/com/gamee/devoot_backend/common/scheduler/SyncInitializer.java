package com.gamee.devoot_backend.common.scheduler;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SyncInitializer implements ApplicationRunner {

	private final LectureSyncScheduler lectureSyncScheduler;

	public SyncInitializer(LectureSyncScheduler lectureSyncScheduler) {
		this.lectureSyncScheduler = lectureSyncScheduler;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		lectureSyncScheduler.resetLastSyncTime();
	}
}
