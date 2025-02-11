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

<script setup>
import { ref, onMounted } from 'vue'
import LectureCardGroup from '@/components/Lecture/LectureCardGroup.vue'
import { getLecture } from '@/helpers/lecture'

const popularLectures = ref([])
const newestLectures = ref([])
const freeLectures = ref([])

// API 호출하여 강의 데이터를 불러오는 함수
const loadLectures = async () => {
    popularLectures.value = await getLecture({ order: 'popular' })
    newestLectures.value = await getLecture({ order: 'newest' })
    freeLectures.value = await getLecture({ currentPrice: 0 })
}

// 컴포넌트가 마운트될 때 강의 데이터를 불러옴
onMounted(() => {
    loadLectures()
})
</script>

<style scoped></style>
