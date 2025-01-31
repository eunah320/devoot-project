package com.gamee.devoot_backend.todo.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class TodoControllerIntegrationTest {

	TodoCreateDto createDto = new TodoCreateDto(1L, LocalDate.now(), "lecture", "sublecture", "www.hello.com", false);
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		// Mock the CustomUserDetails
		CustomUserDetails user = CustomUserDetails.builder()
			.id(1L)
			.profileId("testProfileId")
			.build();
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, "password"));
	}

	@Test
	@DisplayName("Test Exception when profile id mismatch")
	public void testException_whenProfileIdMismatch() throws Exception {
		// Given
		String diffProfileId = "diffProfileId";
		TodoCreateDto dto = new TodoCreateDto(1L, LocalDate.now(), "lecture", "sublecture", "www.hello.com", false);

		// When & Then
		mockMvc.perform(post("/api/users/{profileId}}/todos", diffProfileId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
				.with(csrf())
			)
			.andExpect(status().isForbidden())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(result -> {
				printResponse(result);
			});
	}

	private void printResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
		String jsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object json = objectMapper.readValue(jsonResponse, Object.class); // Deserialize
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json); // Pretty print

		System.out.println(prettyJson);
	}
}
