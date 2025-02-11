package com.gamee.devoot_backend.lecture.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.common.exception.JsonParsingException;
import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record LectureShortDetailDto(
	Long id,
	String name,
	String sourceName,
	String tags,
	String imageUrl,
	JsonNode curriculum
) {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static LectureShortDetailDto of(Lecture lecture) {
		return LectureShortDetailDto.builder()
			.id(lecture.getId())
			.name(lecture.getName())
			.sourceName(lecture.getSourceName())
			.imageUrl(lecture.getImageUrl())
			.tags(lecture.getTags())
			.curriculum(parseCurriculum(lecture.getCurriculum()))
			.build();
	}

	private static JsonNode parseCurriculum(String curriculum) {
		try {
			return objectMapper.readTree(curriculum);
		} catch (Exception e) {
			throw new JsonParsingException();
		}
	}
}
