<!-- src/components/Lecture/LectureCardGroup.vue -->
<template>
    <!-- 부모 컨테이너에 overflow-x-auto와 min-w-max를 적용하여
         그리드의 전체 너비가 카드 고정 크기에 맞춰지도록 함 -->
    <div class="overflow-x-auto">
        <div class="grid grid-cols-2 min-w-max md:grid-cols-4 gap-x-6 gap-y-4">
            <!-- 최대 8개 카드만 표시 -->
            <LectureCard
                v-for="(lecture, index) in lectures.slice(0, 8)"
                :key="lecture.id"
                :id="lecture.id"
                :name="lecture.name"
                :lecturer="lecture.lecturer"
                :platform="lecture.platform"
                :imageUrl="lecture.imageUrl"
                :tags="lecture.tags"
                :currentPrice="lecture.currentPrice"
                :originalPrice="lecture.originalPrice"
                :rating="lecture.rating"
                :reviewCount="lecture.reviewCount"
                :isBookmarked="lecture.isBookmarked"
            />
        </div>
    </div>
</template>

<script>
import LectureCard from './LectureCard.vue'

export default {
    name: 'LectureCardGroup',
    components: {
        LectureCard,
    },
    props: {
        lectures: {
            type: Array,
            required: true,
        },
    },
}
</script>

<style scoped></style>
