package com.gamee.devoot_backend.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.bookmark.entity.BookmarkChangeHistory;

public interface BookmarkChangeHistoryRepository extends JpaRepository<BookmarkChangeHistory, Long> {
}
