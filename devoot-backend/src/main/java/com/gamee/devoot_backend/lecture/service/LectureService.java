package com.gamee.devoot_backend.lecture.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.lecture.dto.LectureDetail;
import com.gamee.devoot_backend.lecture.dto.LectureSearchDetailDto;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.entity.LectureReport;
import com.gamee.devoot_backend.lecture.exception.LectureAlreadyReportedException;
import com.gamee.devoot_backend.lecture.exception.LectureNotFoundException;
import com.gamee.devoot_backend.lecture.repository.LectureReportRepository;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

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

	public CustomPage<LectureSearchDetailDto> search(
		int page,
		int size,
		String category,
		String tag,
		String sort,
		String query
	) {
		Page<Lecture> lectures = lectureRepository.findAll(PageRequest.of(page - 1, size));
		return new CustomPage<>(
			lectures.map(LectureSearchDetailDto::of)
		);
	}
}
