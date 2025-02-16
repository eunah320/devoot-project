package com.gamee.devoot_backend.common.scheduler;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.lecture.document.LectureDocument;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;

@Service
public class LectureSyncScheduler {

	private static final Logger log = LoggerFactory.getLogger(LectureSyncScheduler.class);

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	private Date lastSyncTime = new Date(0);

	// @Scheduled(cron = "0 0 3 * * ?")
	@Scheduled(cron = "0 * * * * ?") // 로컬 테스트용 매분 0초 실행
	public void syncLecturesToElasticsearch() {
		Date now = new Date();
		log.info("Starting sync. Last sync time: {}", lastSyncTime);

		List<Lecture> changedLectures = lectureRepository.findByUpdatedAtAfter(lastSyncTime);
		log.info("Found {} changed lectures", changedLectures.size());

		lastSyncTime = now;
		List<LectureDocument> documents = changedLectures.stream()
			.map(lecture -> {
				float rating = (lecture.getReviewCnt() != null && lecture.getReviewCnt() > 0)
					? (lecture.getRatingSum() / lecture.getReviewCnt())
					: 0f;

				return LectureDocument.builder()
					.id(String.valueOf(lecture.getId()))
					.categoryName(lecture.getCategory())
					.name(lecture.getName())
					.lecturer(lecture.getLecturer())
					.tags(lecture.getTags())
					.currentPrice(lecture.getCurrentPrice())
					.originalPrice(lecture.getOriginalPrice())
					.imageUrl(lecture.getImageUrl())
					.curriculum(lecture.getCurriculum())
					.sourceUrl(lecture.getSourceUrl())
					.sourceName(lecture.getSourceName())
					.createdAt(lecture.getCreatedAt())
					.updatedAt(lecture.getUpdatedAt())
					.hash(lecture.getHash())
					.popularity(rating)
					.build();
			})
			.collect(Collectors.toList());

		if (!documents.isEmpty()) {
			IndexCoordinates index = IndexCoordinates.of("lectures");
			documents.forEach(document -> {
				elasticsearchOperations.save(document, index);
			});
			log.info("Synced {} documents to Elasticsearch", documents.size());
		} else {
			log.info("No documents to sync.");
		}
	}

	public void resetLastSyncTime() {
		lastSyncTime = new Date(0);
		System.out.println("lastSyncTime has been reset to epoch.");
	}
}
