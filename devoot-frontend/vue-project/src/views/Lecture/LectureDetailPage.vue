<template>
    <div class="col-span-12">
        <DetailHeader />
        <CurriculumSection />
        <LectureReviewCard />
    </div>
</template>

<script setup>
import CurriculumSection from '@/components/Lecture/CurriculumSection.vue'
import DetailHeader from '@/components/Lecture/DetailHeader.vue'

// 추후 LectureReviewSection을 불러오고는 아래 LectureReviewCard는 삭제
import LectureReviewCard from '@/components/Lecture/LectureReviewCard.vue'
</script>

<style scoped></style>
