<template>
    <div class="home-page">
        <h1 class="page-title">강의 목록</h1>
        <div class="lecture-list">
            <!-- LectureCard 컴포넌트 사용 -->
            <LectureCard
                v-for="(lecture, index) in lectures"
                :key="index"
                :title="lecture.title"
                :platform="lecture.platform"
                :rating="lecture.rating"
                :reviews="lecture.reviews"
                :price="lecture.price"
                :isFree="lecture.isFree"
                :isBookmarked="lecture.isBookmarked"
                @update:isBookmarked="toggleBookmark(index)"
            />
        </div>
    </div>
</template>

<script>
import LectureCard from '@/components/Lecture/LectureCard.vue'

export default {
    name: 'HomePage',
    components: {
        LectureCard,
    },
    data() {
        return {
            lectures: [
                {
                    title: '백엔드 개발자 취업 토탈 가이드(back-end, 멘토링 경험기반)',
                    platform: '인프런',
                    rating: 5.0,
                    reviews: 100,
                    price: 23980,
                    isFree: false,
                    isBookmarked: true,
                },
                {
                    title: '백엔드 개발자 취업 토탈 가이드(back-end, 멘토링 경험기반)',
                    platform: '인프런',
                    rating: 5.0,
                    reviews: 100,
                    price: 0,
                    isFree: true,
                    isBookmarked: false,
                },
            ],
        }
    },
    methods: {
        toggleBookmark(index) {
            this.lectures[index].isBookmarked = !this.lectures[index].isBookmarked
        },
    },
}
</script>

<style scoped>
.home-page {
    padding: 16px;
}

.page-title {
    font-size: 1.5em;
    margin-bottom: 16px;
}

.lecture-list {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
}
</style>
