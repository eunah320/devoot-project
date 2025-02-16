package com.gamee.devoot_backend.common;

import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.common.exception.JsonParsingException;

@Component
public class Util {
	public static final ObjectMapper objectMapper = new ObjectMapper();
	private static final MessageDigestPasswordEncoder encoder =
		new MessageDigestPasswordEncoder("SHA-256");

	public static JsonNode parseToJson(String jsonString) throws JsonParsingException {
		if (jsonString == null || jsonString.isEmpty()) {
			return null;
		}
		try {
			return objectMapper.readTree(jsonString);
		} catch (Exception e) {
			throw new JsonParsingException();
		}
	}

	public static String generateHash(String content) {
		return encoder.encode(content).replace("{SHA-256}", "");
	}
}
