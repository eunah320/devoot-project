package com.gamee.devoot_backend.lecture.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "lectures")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LectureDocument {
	@Id
	private String id;

	@Field(type = FieldType.Keyword)
	private String categoryName;

	@Field(type = FieldType.Text, analyzer = "standard")
	private String name;

	@Field(type = FieldType.Text, analyzer = "standard")
	private String lecturer;

	@Field(type = FieldType.Text, analyzer = "standard")
	// @Field(type = FieldType.Keyword)
	private String tags;

	@Field(type = FieldType.Integer)
	private Integer currentPrice;

	// Non-indexed fields (index = false)
	@Field(type = FieldType.Integer, index = false)
	private Integer categoryId;

	@Field(type = FieldType.Keyword, index = false)
	private String imageUrl;

	@Field(type = FieldType.Integer, index = false)
	private Integer originalPrice;

	@Field(type = FieldType.Keyword, index = false)
	private String curriculum;

	@Field(type = FieldType.Keyword, index = false)
	private String sourceUrl;

	@Field(type = FieldType.Keyword, index = false)
	private String sourceName;

	@Field(type = FieldType.Date, index = false)
	private Date createdAt;

	@Field(type = FieldType.Date, index = false)
	private Date updatedAt;

	@Field(type = FieldType.Keyword, index = false)
	private String hash;

	@Field(type = FieldType.Float)
	private Float popularity;
}
