package com.gamee.devoot_backend.common;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.common.exception.JsonParsingException;

@Component
public class Util {
	public static final ObjectMapper objectMapper = new ObjectMapper();

	public static JsonNode parseToJson(String jsonString) throws JsonParsingException {
		try {
			return objectMapper.readTree(jsonString);
		} catch (Exception e) {
			throw new JsonParsingException();
		}
	}
}
