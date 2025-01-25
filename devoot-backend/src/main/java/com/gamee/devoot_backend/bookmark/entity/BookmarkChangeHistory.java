package com.gamee.devoot_backend.bookmark.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookmarkchangehistory")
public class BookmarkChangeHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne
	@JoinColumn(name = "lectureId", insertable = false, updatable = false)
	private Lecture lecture;
	private long lectureId;

	@ManyToOne
	@JoinColumn(name = "userId", insertable = false, updatable = false)
	private User user;
	private long userId;

	private int status;
}
