package com.gamee.devoot_backend.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.lecture.entity.Lecture;


public interface LectureRepository extends JpaRepository<Lecture, Long> {

}
