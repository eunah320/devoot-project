package com.gamee.devoot_backend.lecturereview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.lecturereview.entity.LectureReviewReport;

public interface LectureReviewReportRepository extends JpaRepository<LectureReviewReport, Long> {
	Optional<LectureReviewReport> findByLectureReviewIdAndUserId(Long lectureReviewId, Long userId);
}
