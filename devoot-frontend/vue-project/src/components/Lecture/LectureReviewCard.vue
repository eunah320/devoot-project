<template>
    <div id="lecture-review" class="flex flex-col w-full gap-3 p-6">
        <!-- 헤더 -->
        <div id="header" class="flex flex-row items-center">
            <!-- 작성자 정보 -->
            <div
                id="review-info"
                class="flex flex-row items-center h-10 min-w-0 gap-1 overflow-hidden text-caption w-fit flex-nowrap"
            >
                <!-- 프로필 이미지 -->
                <img :src="review.imageUrl" alt="프로필 이미지" class="w-10 h-10 rounded-full" />
                <div class="flex flex-col min-w-0 gap-1 overflow-hidden">
                    <!-- 프로필 아이디/닉네임 -->
                    <div id="reviewer-name" class="flex flex-row gap-1">
                        <p class="text-black truncate">{{ review.nickname }}</p>
                        <p class="text-gray-300 truncate">{{ review.profileId }}</p>
                    </div>
                    <!-- 작성일 -->
                    <p class="text-gray-300 truncate">{{ formattedDate }}</p>
                </div>
            </div>

            <!-- 간격용 -->
            <div class="flex-1"></div>

            <!-- 수정하기 / 삭제하기 / 신고하기 -->
            <div
                id="review-action"
                class="flex flex-row items-center gap-2 text-gray-300 text-caption"
            >
                <!-- 본인 리뷰인 경우 : 수정하기 -->
                <div class="relative flex flex-row items-center gap-1 group">
                    <Edit class="w-4 h-4" />
                    <p class="hidden md:inline">수정하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        수정하기
                    </div>
                </div>

                <!-- 삭제하기 -->
                <div class="relative flex flex-row items-center gap-1 group">
                    <Trash class="w-4 h-4" />
                    <p class="hidden md:inline">삭제하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        삭제하기
                    </div>
                </div>

                <!-- 본인 리뷰가 아닌 경우 : 신고하기 -->
                <div class="relative flex flex-row items-center gap-1 group">
                    <Report class="w-4 h-4" />
                    <p class="hidden md:inline">신고하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        신고하기
                    </div>
                </div>
            </div>
        </div>
        <!-- 내용 -->
        <div class="flex flex-col gap-2">
            <!-- 별점 -->
            <div id="rating" class="flex flex-row items-center gap-1">
                <div id="stars" class="flex flex-row items-center">
                    <!-- 꽉 찬 별 -->
                    <div v-for="n in fullStars" :key="'full-' + n">
                        <Star class="w-6 h-6 text-yellow-300" />
                    </div>

                    <!-- 절반 별 -->
                    <div v-if="hasHalfStar" class="relative w-6 h-6">
                        <Star class="absolute top-0 left-0 w-full h-full text-gray-200" />
                        <Star
                            class="absolute top-0 left-0 z-10 w-full h-full text-yellow-300 clip-half"
                        />
                    </div>

                    <!-- 빈 별 -->
                    <div v-for="n in emptyStars" :key="'empty-' + n">
                        <Star class="w-6 h-6 text-gray-200" />
                    </div>
                </div>
                <p class="text-black text-body">{{ review.rating }}</p>
            </div>
            <!-- 내용 -->
            <div class="text-black text-body">{{ review.content }}</div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'

import Edit from '@/assets/icons/edit.svg'
import Trash from '@/assets/icons/trash.svg'
import Report from '@/assets/icons/report.svg'
import Star from '@/assets/icons/star_filled.svg'

const review = {
    id: 4123,
    lectureId: 1234,
    userId: 12345,
    rating: 2.5,
    content:
        '이 강의는 가볍게 들을 수 있습니다. 이 강의는 가볍게 들을 수 있습니다.이 강의는 가볍게 들을 수 있습니다.이 강의는 가볍게 들을 수 있습니다.이 강의는 가볍게 들을 수 있습니다.이 강의는 가볍게 들을 수 있습니다.이 강의는 가볍게 들을 수 있습니다.이 강의는 가볍게 들을 수 있습니다.이 강의는 가볍게 들을 수 있습니다.이 강의는 가볍게 들을 수 있습니다.',
    createdAt: '2025-01-17 13:05:12',
    profileId: 'customid123',
    nickname: '테스트닉네임',
    imageUrl:
        'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png',
}
// const props = defineProps({
//     review: {
//         type: Object,
//         required: true,
//     },
// })

// 날짜 포맷 변경
const formattedDate = computed(() => {
    const date = new Date(review.createdAt)
    return `${date.getFullYear()}년 ${String(date.getMonth() + 1).padStart(2, '0')}월 ${String(date.getDate()).padStart(2, '0')}일`
})

// 별 개수 계산
const fullStars = computed(() => Math.floor(review.rating))
const hasHalfStar = computed(() => review.rating % 1 !== 0)
const emptyStars = computed(() => 5 - fullStars.value - (hasHalfStar.value ? 1 : 0))
</script>

<style scoped>
/* 왼쪽 절반을 보이게 설정 */
.clip-half {
    clip-path: inset(0 50% 0 0); /* 상, 우, 하, 좌 */
}
</style>
