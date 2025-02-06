package com.gamee.devoot_backend.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gamee.devoot_backend.user.dto.UserSearchDetailDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	UserRepository userRepository;

	@InjectMocks
	UserService userService;

	@Test
	@DisplayName("Test searchByPrefix()")
	void testSearchByPrefix() {
		// Given
		User user1 = User.builder().uid("1").profileId("devoot1").nickname("devoot").build();
		User user2 = User.builder().uid("2").profileId("devoot2").nickname("ssafy").build();
		User user3 = User.builder().uid("3").profileId("ssafy3").nickname("devoot").build();

		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);

		when(userRepository.searchByPrefix("devoot"))
			.thenReturn(List.of(user1, user2, user3));

		// When
		List<UserSearchDetailDto> userSearchDetailDtos = userService.searchByPrefix("devoot");

		// Then
		assertEquals(userSearchDetailDtos.size(), 3);

		System.out.println(userSearchDetailDtos);

	}

}
