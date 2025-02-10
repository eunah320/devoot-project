<template>
    <div class="flex flex-col col-span-12 gap-9">
        <DetailHeader />
        <div class="overflow-hidden border border-gray-200 rounded-2xl">
            <TabMenu v-model="selectedTab" tab-left="커리큘럼" tab-right="리뷰" />
            <CurriculumSection v-if="selectedTab === 'left'" />
            <LectureReviewSection v-if="selectedTab === 'right'" />
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import CurriculumSection from '@/components/Lecture/CurriculumSection.vue'
import DetailHeader from '@/components/Lecture/DetailHeader.vue'
import TabMenu from '@/components/Common/TabMenu.vue'
import LectureReviewSection from '@/components/Lecture/LectureReviewSection.vue'

// 선택된 탭 상태
const selectedTab = ref('left') // 기본값: 'left'
</script>

<style scoped></style>
