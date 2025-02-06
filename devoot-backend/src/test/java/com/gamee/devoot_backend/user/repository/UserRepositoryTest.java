package com.gamee.devoot_backend.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.gamee.devoot_backend.user.entity.User;

@DataJpaTest
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("Test searchByPrefix()")
	public void testSearchByPrefix() {
		// Given
		User user1 = User.builder().uid("1").profileId("devoot1").nickname("devoot").build();
		User user2 = User.builder().uid("2").profileId("devoot2").nickname("ssafy").build();
		User user3 = User.builder().uid("3").profileId("ssafy3").nickname("devoot").build();
		User user4 = User.builder().uid("4").profileId("ssafy4").nickname("ssafy").build();

		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);
		userRepository.save(user4);

		// When
		List<User> users = userRepository.searchByPrefix("devoot");
		Set<Long> searchedUserIds = users.stream()
			.map(User::getId)
			.collect(Collectors.toSet());

		// Then
		assertEquals(3, users.size());
		assertTrue(searchedUserIds.contains(user1.getId()));
		assertTrue(searchedUserIds.contains(user2.getId()));
		assertTrue(searchedUserIds.contains(user3.getId()));
		assertFalse(searchedUserIds.contains(user4.getId()));
	}
}
