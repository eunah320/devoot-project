package com.gamee.devoot_backend.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
