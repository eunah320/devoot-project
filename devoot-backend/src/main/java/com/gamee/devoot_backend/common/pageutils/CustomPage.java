package com.gamee.devoot_backend.common.pageutils;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class CustomPage<T> {
	private final List<T> content;
	private final long totalElements;
	private final int totalPages;
	public CustomPage(Page<T> page) {
		content = page.getContent();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
	}
}
