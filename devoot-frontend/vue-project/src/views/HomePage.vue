<template>
    <div class="space-y-12">
        <!-- 인기 강의 -->
        <section class="space-y-4">
            <h1 class="text-h1 col-span-full">인기 강의</h1>
            <LectureCardGroup :lectures="popularLectures" />
        </section>

        <!-- 신규 강의 -->
        <section class="space-y-4">
            <h1 class="text-h1 col-span-full">신규 강의</h1>
            <LectureCardGroup :lectures="newestLectures" />
        </section>

        <!-- 무료 강의 -->
        <section class="space-y-4">
            <h1 class="text-h1 col-span-full">무료 강의</h1>
            <LectureCardGroup :lectures="freeLectures" />
        </section>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import LectureCardGroup from '@/components/Lecture/LectureCardGroup.vue'
import { searchLectures } from '@/helpers/lecture' // 실제 API 호출 함수

// 강의 데이터를 저장할 ref 선언
const lectures = ref([])

// 인기 강의: rating 기준 내림차순 정렬 후 상위 8개 추출
const popularLectures = computed(() => {
    return [...lectures.value].sort((a, b) => (b.rating || 0) - (a.rating || 0)).slice(0, 8)
})

// 신규 강의: 배열의 마지막 8개를 최신순으로 정렬
const newestLectures = computed(() => {
    return [...lectures.value].slice(-8).reverse()
})

// 무료 강의: currentPrice가 0인 강의를 최대 8개 추출
const freeLectures = computed(() => {
    return lectures.value.filter((lecture) => lecture.currentPrice === 0).slice(0, 8)
})

// API를 호출하여 강의 데이터를 가져오는 함수
async function fetchLectures() {
    try {
        const response = await searchLectures({
            category: '모바일 앱 개발',
            tag: '데이터 분석,데이터 엔지니어링',
            sort: 'newest',
            query: 'Unity asdf',
            page: 1,
            size: 10,
        })

        const { content } = response.data

        // LectureCardGroup 컴포넌트에 맞게 데이터 가공
        lectures.value = content.map((item, index) => ({
            id: index,
            name: item.name,
            lecturer: item.lecturer,
            platform: item.sourceName || '인프런',
            imageUrl: item.imageUrl,
            tags: item.tags
                ? item.tags
                      .split(',')
                      .map((tag) => tag.trim())
                      .filter(Boolean)
                : [],
            currentPrice: item.currentPrice ?? 0,
            originalPrice: item.originPrice ?? 0,
            rating: item.rating ?? 0,
            reviewCount: item.reviewCount ?? 0,
            isBookmarked: false,
        }))
    } catch (error) {
        console.error('Error loading lecture data:', error)
    }
}

// 컴포넌트가 마운트되면 강의 데이터를 불러옵니다.
onMounted(() => {
    fetchLectures()
})
</script>

<style scoped>
/* 필요한 스타일 작성 */
</style>
