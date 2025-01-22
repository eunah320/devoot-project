package com.gamee.devoot_backend.lecturereview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamee.devoot_backend.lecturereview.entity.LectureReview;

public interface LectureReviewRepository extends JpaRepository<LectureReview, Long> {
	@Query(value = """
		SELECT AVG(`rating`) FROM `lecturereview` GROUP BY `lectureId` HAVING `lectureId` = :lectureId
		""", nativeQuery = true)
	Float findAvgByLectureId(@Param("lectureId") long lectureId);
}
