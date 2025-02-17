<template>
    <div class="flex flex-col gap-4">
        <div class="content-center h-20 text-h3">강의 수정 요청</div>
        <div class="grid grid-cols-12 gap-6">
            <LectureCard />
        </div>
    </div>
</template>

<script setup>
import LectureCard from '@/components/LectureCard.vue'
</script>

<style scoped></style>
