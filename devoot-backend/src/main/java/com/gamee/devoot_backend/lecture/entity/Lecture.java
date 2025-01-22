package com.gamee.devoot_backend.lecture.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name="lecture")
@Entity
@Builder
public class Lecture {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private int categoryId;
	private String name;
	private String lecturer;
	private String imageUrl;
	private int originalPrice;
	private String currentPrice;
	private String curriculum;
	private String sourceUrl;
	private String sourceName;
	private String tags;
	private Date createdAt;
	private Date updatedAt;
	private String hash;
}
