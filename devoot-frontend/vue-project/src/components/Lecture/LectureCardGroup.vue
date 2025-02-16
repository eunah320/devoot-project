<template>
    <div class="grid grid-cols-4 gap-x-6 gap-y-4">
        <!-- 좌우 간격 24px (gap-x-6), 상하 간격 16px (gap-y-4) -->
        <!-- 최대 8개의 카드만 표시 -->
        <LectureCard
            v-for="lecture in lectures.slice(0, 8)"
            :key="lecture.id"
            :name="lecture.name"
            :lecturer="lecture.lecturer"
            :platform="lecture.platform || '인프런'"
            :imageUrl="lecture.imageUrl"
            :tags="lecture.tags"
            :currentPrice="lecture.currentPrice"
            :originalPrice="lecture.originalPrice"
            :rating="lecture.rating"
            :reviewCount="lecture.reviewCount"
            :isBookmarked="lecture.isBookmarked"
        />
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
