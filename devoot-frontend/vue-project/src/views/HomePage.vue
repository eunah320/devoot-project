<template>
    <div class="p-6 space-y-12">
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

<script>
import LectureCardGroup from '@/components/Lecture/LectureCardGroup.vue'

export default {
    name: 'HomePage',
    components: {
        LectureCardGroup,
    },
    data() {
        return {
            lectures: [],
        }
    },
    computed: {
        popularLectures() {
            return [...this.lectures].sort((a, b) => b.rating - a.rating).slice(0, 8)
        },
        newestLectures() {
            return [...this.lectures].slice(-8).reverse()
        },
        freeLectures() {
            return this.lectures.filter((lecture) => lecture.currentPrice === 0).slice(0, 8)
        },
    },
    created() {
        fetch('/lecturecard_dummy_data.json')
            .then((response) => response.json())
            .then((data) => (this.lectures = data))
            .catch((error) => console.error('Error loading lecture data:', error))
    },
}
</script>

<style scoped>
/* h1 태그가 전체 열을 차지하도록 명시적 스타일 추가 */
h1.text-h1 {
    grid-column: span 12 / span 12;
}
</style>
