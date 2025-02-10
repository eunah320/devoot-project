<template>
    <div class="flex flex-col gap-6 border border-gray-200 px-9 py-7 rounded-2xl">
        <div id="modal-header" class="flex flex-row">
            <p class="text-h2">댓글 수정하기</p>
            <div class="flex-1"></div>
            <Delete class="w-6 h-6 cursor-pointer" @click="emit('closeModal')" />
        </div>

        <!-- 강의 카드 -->
        <ReviewLectureCard :lecture="lecture" />

        <div id="text-container" class="flex flex-col gap-1">
            <!-- 별점 -->
            <div class="flex items-center gap-1" @mouseleave="resetHover">
                <div id="star-container" class="flex flex-row">
                    <!-- 별 렌더링 -->
                    <div
                        v-for="index in 5"
                        :key="'star-' + index"
                        class="relative w-6 h-6 cursor-pointer"
                        @mousemove="setHover(index, $event)"
                        @click="setRating(index, $event)"
                    >
                        <!-- 전체 별 -->
                        <Star class="absolute top-0 left-0 w-full h-full text-gray-200" />

                        <!-- 부분 채워진 별 -->
                        <Star
                            class="absolute top-0 left-0 w-full h-full text-yellow-300"
                            :style="{
                                clipPath: getClipPath(index),
                            }"
                        />
                    </div>
                </div>
                <p class="text-black text-body">{{ rating }}점</p>
            </div>
            <!-- 텍스트 칸 -->
            <textarea
                v-model="text"
                class="text-body bg-gray-200 w-full px-4 py-2 mt-4 overflow-y-auto border border-gray-200 rounded-lg resize-none h-96 focus:border-2 focus:border-primary-500 focus:outline-none custom-scrollbar"
                placeholder="댓글을 입력하세요"
            ></textarea>
        </div>
        <!-- 수정 버튼 -->
        <div class="flex justify-end">
            <button class="button-primary">저장하기</button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import ReviewLectureCard from './ReviewEditModalLectureCard.vue'
import { writeLectureReview, editLectureReview } from '@/helpers/lecture'

import Delete from '@/assets/icons/delete.svg'
import Star from '@/assets/icons/star_filled.svg'

defineProps({
    lecture: {
        type: Object,
        required: true,
    },
})

// `close` 이벤트를 부모 컴포넌트로 전달할 emit 정의
const emit = defineEmits(['closeModal'])

// 댓글 내용
const text = ref('안녕')
// 별점 상태
const rating = ref(3.5) // 초기 별점
const hoverRating = ref(rating.value) // 호버 중 별점

// 마우스 호버 시 별점 설정
const setHover = (index, event) => {
    const rect = event.target.getBoundingClientRect()
    const isLeftHalf = event.clientX < rect.left + rect.width / 2
    hoverRating.value = isLeftHalf ? index - 0.5 : index // 절반 단위로 업데이트
}

// 별점 클릭 시 값 고정
const setRating = (index, event) => {
    const rect = event.target.getBoundingClientRect()
    const isLeftHalf = event.clientX < rect.left + rect.width / 2
    rating.value = isLeftHalf ? index - 0.5 : index // 절반 단위 고정
    hoverRating.value = rating.value // 호버 상태도 업데이트
}

// 마우스가 별을 벗어나면 기존 값으로 복원
const resetHover = () => {
    hoverRating.value = rating.value
}

// 별의 clip-path를 계산
const getClipPath = (index) => {
    if (hoverRating.value >= index) {
        return 'inset(0 0 0 0)' // 꽉 찬 별
    } else if (hoverRating.value >= index - 0.5) {
        return 'inset(0 50% 0 0)' // 절반 별
    } else {
        return 'inset(0 100% 0 0)' // 빈 별
    }
}
</script>

<style scoped>
/* Tailwind 클래스를 적용한 스크롤바 스타일 */
.custom-scrollbar::-webkit-scrollbar {
    @apply w-2; /* 스크롤바 너비 */
}

.custom-scrollbar::-webkit-scrollbar-track {
    @apply bg-gray-100 rounded-lg; /* 트랙 배경 */
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    @apply bg-gray-200 rounded-lg; /* 스크롤 핸들 색상 */
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
    @apply bg-primary-100; /* 마우스 호버 시 색상 */
}
</style>
