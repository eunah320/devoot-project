package com.gamee.devoot_backend.lecture.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.common.enums.SortType;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.lecture.document.LectureDocument;
import com.gamee.devoot_backend.lecture.dto.LectureDetail;
import com.gamee.devoot_backend.lecture.dto.LectureSearchDetailDto;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.entity.LectureReport;
import com.gamee.devoot_backend.lecture.exception.LectureAlreadyReportedException;
import com.gamee.devoot_backend.lecture.exception.LectureNotFoundException;
import com.gamee.devoot_backend.lecture.exception.SearchExecutionErrorException;
import com.gamee.devoot_backend.lecture.repository.LectureReportRepository;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;

@Service
public class LectureService {
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private LectureReportRepository lectureReportRepository;
	@Autowired
	private LectureReviewRepository lectureReviewRepository;
	@Autowired
	private BookmarkRepository bookmarkRepository;
	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	public LectureDetail getLectureDetail(Long id, CustomUserDetails user) {
		Optional<Lecture> lectureOptional = lectureRepository.findById(id);
		if (lectureOptional.isPresent()) {
			Lecture lecture = lectureOptional.get();
			float rating = 0;
			if (lecture.getReviewCnt() != 0) {
				rating = lecture.getRatingSum() / (float)lecture.getReviewCnt();
			}
			long count = bookmarkRepository.countByLectureId(lecture.getId());
			if (user != null) {
				Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByUserIdAndLectureId(user.id(), id);
				if (bookmarkOptional.isPresent()) {
					return new LectureDetail(lecture, count, rating, true, bookmarkOptional.get().getId());
				}
			}
			return new LectureDetail(lecture, count, rating, false, -1);
		}
		throw new LectureNotFoundException();
	}

	public void reportLecture(Long userId, Long lectureId) {
		lectureRepository.findById(lectureId)
			.orElseThrow(() -> new LectureNotFoundException());

		lectureReportRepository.findByLectureIdAndUserId(lectureId, userId)
			.ifPresent(report -> {
				throw new LectureAlreadyReportedException();
			});

		lectureReportRepository.save(
			LectureReport.builder()
				.lectureId(lectureId)
				.userId(userId)
				.build()
		);
	}

	@SuppressWarnings({"checkstyle:WhitespaceAround", "checkstyle:RegexpSinglelineJava"})
	public CustomPage<LectureSearchDetailDto> search(
		int page,
		int size,
		String category,
		String tag,
		String sort,
		String query
	) {
		List<Query> mustQueries = new ArrayList<>();
		List<Query> filterQueries = new ArrayList<>();

		if (query != null && !query.isBlank()) {
			MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
				.fields("name", "lecturer")
				.query(query)
				.build();
			mustQueries.add(multiMatchQuery._toQuery());
		} else if (category != null && !category.isBlank()) {
			TermQuery termQuery = new TermQuery.Builder()
				.field("categoryName")
				.value(category)
				.build();
			filterQueries.add(termQuery._toQuery());
		}

		if (tag != null && !tag.isBlank()) {
			String[] tags = tag.split(",");
			for (String t : tags) {
				MatchQuery matchQuery = new MatchQuery.Builder()
					.field("tags")
					.query(t.trim())
					.build();
				filterQueries.add(matchQuery._toQuery());
			}
		}

		BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
		if (!mustQueries.isEmpty()) {
			boolQueryBuilder.must(mustQueries);
		}
		if (!filterQueries.isEmpty()) {
			boolQueryBuilder.filter(filterQueries);
		}
		Query finalQuery = boolQueryBuilder.build()._toQuery();

		Sort springSort = getSort(sort);
		NativeQuery nativeQuery = NativeQuery.builder()
			.withQuery(finalQuery)
			.withPageable(PageRequest.of(page - 1, size, springSort))
			.build();
		SearchHits<LectureDocument> searchHits;
		try {
			searchHits = elasticsearchOperations.search(nativeQuery, LectureDocument.class);
		} catch (Exception e) {
			throw new SearchExecutionErrorException();
		}

		List<LectureSearchDetailDto> dtos = searchHits.getSearchHits().stream().map(hit -> {
			LectureDocument doc = hit.getContent();
			return LectureSearchDetailDto.builder()
				.category(doc.getCategoryName())
				.tags(doc.getTags())
				.name(doc.getName())
				.lecturer(doc.getLecturer())
				.currentPrice(doc.getCurrentPrice() != null ? doc.getCurrentPrice() : 0)
				.originPrice(doc.getOriginalPrice() != null ? doc.getOriginalPrice() : 0)
				.sourceName(doc.getSourceName())
				.imageUrl(doc.getImageUrl())
				.rating(doc.getPopularity() != null ? doc.getPopularity() : 0f)
				.build();
		}).collect(Collectors.toList());
		long totalHits = searchHits.getTotalHits();

		return new CustomPage<>(new PageImpl<>(dtos, PageRequest.of(page - 1, size, springSort), totalHits));
	}

	private Sort getSort(String sort) {
		SortType sortType;
		try {
			sortType = (sort == null || sort.isBlank()) ? SortType.POPULAR : SortType.valueOf(sort.toUpperCase());
		} catch (IllegalArgumentException e) {
			sortType = SortType.POPULAR;
		}
		return switch (sortType) {
			case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
			case PRICE_DESC -> Sort.by(Sort.Direction.DESC, "currentPrice");
			case PRICE_ASC -> Sort.by(Sort.Direction.ASC, "currentPrice");
			default -> Sort.by(Sort.Direction.DESC, "popularity");
		};
	}
}
