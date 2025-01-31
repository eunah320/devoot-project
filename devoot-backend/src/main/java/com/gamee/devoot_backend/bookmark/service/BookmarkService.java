package com.gamee.devoot_backend.bookmark.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.bookmark.dto.BookmarkCreateDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkDetailDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkUpdateDto;
import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;
import com.gamee.devoot_backend.bookmark.exception.BookmarkNotFoundException;
import com.gamee.devoot_backend.bookmark.exception.BookmarkPermissionDeniedException;
import com.gamee.devoot_backend.bookmark.repository.BookmarkLogRepository;
import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
	private final BookmarkRepository bookmarkRepository;
	private final BookmarkLogRepository bookmarkLogRepository;
	private final UserService userService;
	private final FollowService followService;

	@Transactional
	public void addBookmark(CustomUserDetails user, String profileId, BookmarkCreateDto dto) {
		userService.checkUserMatchesProfileId(user, profileId);
		Bookmark bookmark = dto.toEntity();
		bookmark.setUserId(user.id());

		bookmarkRepository.findByUserIdAndStatusAndNextId(user.id(), bookmark.getStatus(), null)
			.ifPresent(beforeBookmark -> {
				bookmark.setNextId(-1L);
				bookmarkRepository.save(bookmark);

				beforeBookmark.setNextId(bookmark.getNextId());
				bookmarkRepository.save(beforeBookmark);
			});

		bookmark.setNextId(null);
		bookmarkRepository.save(bookmark);

		bookmarkLogRepository.save(BookmarkLog.builder()
			.lectureId(bookmark.getLectureId())
			.userId(user.id())
			.beforeStatus(null)
			.afterStatus(bookmark.getStatus())
			.build());
	}

	public Map<String, List<BookmarkDetailDto>> getBookmarks(CustomUserDetails user, String profileId) {
		User followedUser = followService.validateAccessAndFetchFollowedUser(user, profileId);
		Map<String, List<Bookmark>> bookmarks = new LinkedHashMap<>();
		Map<Long, Bookmark> bookmarkMap = bookmarkRepository.findBookmarksByUserId(user.id()).stream()
			.collect(Collectors.toMap(Bookmark::getId, bookmark -> bookmark));

		bookmarks.put("todo", new ArrayList<>());
		bookmarks.put("in-progress", new ArrayList<>());
		bookmarks.put("done", new ArrayList<>());

		bookmarkRepository.findFirstBookmarkOf(user.id(), 1)
			.ifPresent(bookmark -> {
				addBookmarksInOrder(bookmark, bookmarkMap, bookmarks.get("todo"));
			});
		bookmarkRepository.findFirstBookmarkOf(user.id(), 2)
			.ifPresent(bookmark -> {
				addBookmarksInOrder(bookmark, bookmarkMap, bookmarks.get("in-progress"));
			});
		bookmarkRepository.findFirstBookmarkOf(user.id(), 3)
			.ifPresent(bookmark -> {
				addBookmarksInOrder(bookmark, bookmarkMap, bookmarks.get("done"));
			});

		return bookmarks.entrySet()
			.stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> entry.getValue().stream()
					.map(BookmarkDetailDto::of)
					.collect(Collectors.toList())
			));
	}

	@Transactional
	public void updateBookmark(CustomUserDetails user, String profileId, Long bookmarkId, BookmarkUpdateDto dto) {
		userService.checkUserMatchesProfileId(user, profileId);
		Bookmark bookmark = checkUserIsAllowedAndFetchBookmark(user, bookmarkId);
		Bookmark updatedBookmark = dto.toEntity();
		updatedBookmark.setUserId(user.id());

		if (updatedBookmark.getNextId() != -1) {
			bookmarkRepository.findByUserIdAndNextId(user.id(), bookmark.getId())
				.ifPresent(beforeBookmark -> {
					beforeBookmark.setNextId(bookmark.getNextId());
					bookmarkRepository.save(beforeBookmark);
				});
			bookmarkRepository.findByUserIdAndNextId(user.id(), updatedBookmark.getId())
				.ifPresent(newBeforeBookmark -> {
					newBeforeBookmark.setNextId(bookmark.getId());
					bookmarkRepository.save(newBeforeBookmark);
				});
			bookmarkRepository.save(updatedBookmark);
		}

		if (!bookmark.getStatus().equals(updatedBookmark.getStatus())) {
			bookmarkLogRepository.save(BookmarkLog.builder()
				.lectureId(bookmark.getLectureId())
				.userId(user.id())
				.beforeStatus(bookmark.getStatus())
				.afterStatus(updatedBookmark.getStatus())
				.build());
		}
	}

	public void deleteBookmark(CustomUserDetails user, String profileId, Long bookmarkId) {
		userService.checkUserMatchesProfileId(user, profileId);
		Bookmark bookmark = checkUserIsAllowedAndFetchBookmark(user, bookmarkId);
		bookmarkRepository.delete(bookmark);
	}

	private void addBookmarksInOrder(Bookmark startBookmark, Map<Long, Bookmark> bookmarkMap, List<Bookmark> bookmarks) {
		Bookmark currentBookmark = startBookmark;
		while (currentBookmark != null) {
			bookmarks.add(currentBookmark);
			currentBookmark = bookmarkMap.get(currentBookmark.getNextId());
		}
	}

	private Bookmark checkUserIsAllowedAndFetchBookmark(CustomUserDetails user, Long bookmarkId) {
		Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
			.orElseThrow(BookmarkNotFoundException::new);
		if (!bookmark.getUserId().equals(user.id())) {
			throw new BookmarkPermissionDeniedException();
		}
		return bookmark;
	}
}
