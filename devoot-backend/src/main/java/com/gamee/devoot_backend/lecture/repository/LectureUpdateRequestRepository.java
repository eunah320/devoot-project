package com.gamee.devoot_backend.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.lecture.entity.LectureUpdateRequest;

public interface LectureUpdateRequestRepository extends JpaRepository<LectureUpdateRequest, Long> {
	void deleteByLectureId(Long lectureId);
}
