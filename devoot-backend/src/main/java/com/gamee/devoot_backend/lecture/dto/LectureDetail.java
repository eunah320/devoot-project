package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LectureDetail {
	private String name;
	private String lecturer;
	private int currentPrice;
	private int originPrice;
	private String sourceName;
	private String sourceUrl;
	private String imgUrl;
	private String curriculum;
	private int bookmarkCount;
	private float rating;
	public LectureDetail(Lecture lecture, int bookmarkCount, float rating) {
		name = lecture.getName();
		lecturer = lecture.getLecturer();
		currentPrice = lecture.getCurrentPrice();
		originPrice = lecture.getOriginalPrice();
		sourceName = lecture.getSourceName();
		sourceUrl = lecture.getSourceUrl();
		imgUrl = lecture.getImageUrl();
		curriculum = lecture.getCurriculum();
		this.bookmarkCount = bookmarkCount;
		this.rating = rating;
	}
}
