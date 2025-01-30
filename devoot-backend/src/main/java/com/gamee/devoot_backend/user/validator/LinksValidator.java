package com.gamee.devoot_backend.user.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LinksValidator implements ConstraintValidator<ValidLinks, String> {

	@Override
	public boolean isValid(String links,
		ConstraintValidatorContext constraintValidatorContext) {
		if (links == null) {
			setCustomMessage(constraintValidatorContext, "links cannot be null");
			return false;
		}
		if (links.trim().isEmpty()) {
			return true;
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(links.trim());

			// 필드 길이 검사
			JsonNode titleNode = rootNode.get("title");
			JsonNode urlNode = rootNode.get("url");

			if (titleNode == null || titleNode.asText().length() < 6 || titleNode.asText().length() > 20) {
				setCustomMessage(constraintValidatorContext, "title must be between 6 and 20 characters");
				return false;
			}

			if (urlNode == null || urlNode.asText().length() < 6 || urlNode.asText().length() > 20) {
				setCustomMessage(constraintValidatorContext, "url must be between 6 and 20 characters");
				return false;
			}
			return true;
		} catch (JsonProcessingException e) {
			setCustomMessage(constraintValidatorContext, "links must be a valid JSON");
			return false;
		}
	}

	private void setCustomMessage(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
	}
}
