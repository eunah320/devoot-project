package com.gamee.devoot_backend.lecturereview.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "lecturereview")
@Entity
@Builder
public class LectureReview {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long lectureId;
	private long userId;
	private float rating;
	private String content;
	private Date createdAt;
}
