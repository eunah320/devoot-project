package com.gamee.devoot_backend.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
		Page<User> userPage = userRepository.searchByPrefix("devoot", PageRequest.of(0, 10));
		Set<Long> searchedUserIds = userPage.stream()
			.map(User::getId)
			.collect(Collectors.toSet());

		// Then
		System.out.println(userPage);
		assertEquals(0, userPage.getNumber(), "Page number should be 0");
		assertEquals(3, userPage.getTotalElements(), "Total elements should be 3");
		assertTrue(searchedUserIds.contains(user1.getId()));
		assertTrue(searchedUserIds.contains(user2.getId()));
		assertTrue(searchedUserIds.contains(user3.getId()));
		assertFalse(searchedUserIds.contains(user4.getId()));
	}
}
