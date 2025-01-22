package com.gamee.devoot_backend.lecture.service;

import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LectureService {
    @Autowired
    private LectureRepository lectureRepository;
    public Lecture getLectureDetail(long id){
        Optional<Lecture> lecture = lectureRepository.findById(id);
        return lecture.orElse(null);
    }
}
