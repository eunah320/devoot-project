package com.gamee.devoot_backend.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LectureDetail {
	private String title;
	private String lecturer;
	private int currentPrice;
	private int originPrice;
	private String sourceName;
	private String sourceUrl;
	private String imgUrl;
	private String curriculum;
	private int bookmarkCount;
	private double rating;
}
