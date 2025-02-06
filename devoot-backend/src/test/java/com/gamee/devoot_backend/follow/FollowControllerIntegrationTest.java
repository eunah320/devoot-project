package com.gamee.devoot_backend.follow;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.common.exception.DevootException;
import com.gamee.devoot_backend.follow.exception.FollowErrorCode;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserErrorCode;
import com.gamee.devoot_backend.user.firebase.FirebaseService;
import com.google.firebase.auth.FirebaseToken;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FollowControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private FirebaseService firebaseService;

	@MockitoBean
	private FollowService followService;

	private final User user = User.builder().id(1L).profileId("profileId").build();
	private final FirebaseService.DecodedToken mockToken = new FirebaseService.DecodedToken("mockUid", "devoot@gmail.com");
	private final FirebaseToken firebaseToken = mock(FirebaseToken.class);

	@BeforeEach
	void setUp() throws Exception {
		when(firebaseService.parseToken(any())).thenReturn(mockToken);
		when(firebaseService.findUserByUid(any())).thenReturn(Optional.of(user));

		CustomUserDetails userDetails = CustomUserDetails.builder()
			.id(1L)
			.profileId("profileId")
			.build();

		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "password"));
	}

	// ======================= 팔로우(follow) 테스트 =======================

	/**
	 * ✅ 정상적인 팔로우 요청 (201 Created)
	 */
	@Test
	@DisplayName("정상적인 팔로우 요청")
	public void testCreateFollower_Success() throws Exception {
		String followerProfileId = "profileId";
		String followedProfileId = "targetUser";

		doNothing().when(followService).createFollower(followerProfileId, followedProfileId);

		mockMvc.perform(post("/api/users/{profileId}/follow", followedProfileId)
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());

		verify(followService, times(1)).createFollower(followerProfileId, followedProfileId);
	}

	/**
	 * ❌ 자신을 팔로우하려는 경우 (400 Bad Request)
	 */
	@Test
	@DisplayName("자신을 팔로우하려는 경우 예외 발생")
	public void testCreateFollower_SelfFollow() throws Exception {
		doThrow(new DevootException(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF))
			.when(followService).createFollower("profileId", "profileId");

		mockMvc.perform(post("/api/users/{profileId}/follow", "profileId")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF.getCode()))
			.andExpect(jsonPath("$.message").value(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF.getMessage()));
	}

	/**
	 * ❌ 존재하지 않는 사용자를 팔로우하려는 경우 (404 Not Found)
	 */
	@Test
	@DisplayName("존재하지 않는 사용자 팔로우 시 예외 발생")
	public void testCreateFollower_UserNotFound() throws Exception {
		doThrow(new DevootException(UserErrorCode.USER_NOT_FOUND))
			.when(followService).createFollower("profileId", "nonexistentUser");

		mockMvc.perform(post("/api/users/{profileId}/follow", "nonexistentUser")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(UserErrorCode.USER_NOT_FOUND.getMessage()));
	}

	/**
	 * ❌ 이미 팔로우한 경우 (409 Conflict)
	 */
	@Test
	@DisplayName("이미 팔로우한 경우 예외 발생")
	public void testCreateFollower_AlreadyFollowing() throws Exception {
		doThrow(new DevootException(FollowErrorCode.FOLLOW_RELATIONSHIP_ALREADY_EXISTS))
			.when(followService).createFollower("profileId", "targetUser");

		mockMvc.perform(post("/api/users/{profileId}/follow", "targetUser")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(FollowErrorCode.FOLLOW_RELATIONSHIP_ALREADY_EXISTS.getCode()))
			.andExpect(jsonPath("$.message").value(FollowErrorCode.FOLLOW_RELATIONSHIP_ALREADY_EXISTS.getMessage()));
	}

	/**
	 * ❌ 토큰 없이 팔로우 요청 (401 Unauthorized)
	 */
	@Test
	@DisplayName("토큰 없이 팔로우 요청 시 예외 발생")
	public void testCreateFollower_Unauthorized() throws Exception {
		mockMvc.perform(post("/api/users/{profileId}/follow", "targetUser")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	/**
	 * 📌 응답 JSON 출력
	 */
	private void printResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
		String jsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object json = objectMapper.readValue(jsonResponse, Object.class); // Deserialize
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json); // Pretty print
		System.out.println(prettyJson);
	}

	// ======================= 언팔로우(unfollow) 테스트 =======================

	/**
	 * ✅ 정상적인 언팔로우 요청 (200 OK)
	 */
	@Test
	@DisplayName("정상적인 언팔로우 요청")
	public void testDeleteFollower_Success() throws Exception {
		String followerProfileId = "profileId";
		String followedProfileId = "targetUser";

		doNothing().when(followService).deleteFollower(followerProfileId, followedProfileId);

		mockMvc.perform(delete("/api/users/{profileId}/follow", followedProfileId)
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(followService, times(1)).deleteFollower(followerProfileId, followedProfileId);
	}

	/**
	 * ❌ 자신을 언팔로우하려는 경우 (400 Bad Request)
	 */
	@Test
	@DisplayName("자신을 언팔로우하려는 경우 예외 발생")
	public void testDeleteFollower_SelfUnfollow() throws Exception {
		doThrow(new DevootException(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF))
			.when(followService).deleteFollower("profileId", "profileId");

		mockMvc.perform(delete("/api/users/{profileId}/follow", "profileId")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF.getCode()))
			.andExpect(jsonPath("$.message").value(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF.getMessage()));
	}

	/**
	 * ❌ 존재하지 않는 사용자를 언팔로우하려는 경우 (404 Not Found)
	 */
	@Test
	@DisplayName("존재하지 않는 사용자 언팔로우 시 예외 발생")
	public void testDeleteFollower_UserNotFound() throws Exception {
		doThrow(new DevootException(UserErrorCode.USER_NOT_FOUND))
			.when(followService).deleteFollower("profileId", "nonexistentUser");

		mockMvc.perform(delete("/api/users/{profileId}/follow", "nonexistentUser")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(UserErrorCode.USER_NOT_FOUND.getMessage()));
	}

	/**
	 * ❌ 이미 존재하지 않는 팔로우 관계를 언팔로우하려는 경우 (404 Not Found)
	 */
	@Test
	@DisplayName("이미 존재하지 않는 팔로우 관계 언팔로우 시 예외 발생")
	public void testDeleteFollower_RelationshipNotFound() throws Exception {
		doThrow(new DevootException(FollowErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND))
			.when(followService).deleteFollower("profileId", "targetUser");

		mockMvc.perform(delete("/api/users/{profileId}/follow", "targetUser")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(FollowErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FollowErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND.getMessage()));
	}
}
