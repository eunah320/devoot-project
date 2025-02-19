package com.gamee.devoot_backend.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.lecture.entity.LectureCreateRequest;

public interface LectureCreateRequestRepository extends JpaRepository<LectureCreateRequest, Long> {
	@Modifying
	@Transactional
	void deleteBySourceUrl(String sourceUrl);
}
