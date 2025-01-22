package com.gamee.devoot_backend.lectureReview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.lectureReview.entity.LectureReview;

public interface LectureReviewRepository extends JpaRepository<Long, LectureReview> {

}
