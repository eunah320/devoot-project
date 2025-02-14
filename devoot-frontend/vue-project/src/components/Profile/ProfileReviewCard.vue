<template>
    <div class="flex flex-col w-full gap-3 p-6 border border-gray-200 rounded-2xl">
        <div class="flex justify-between">
            <div class="flex gap-[5px]">
                <p class="flex items-center text-gray-300 text-caption">구름</p>
                <span class="mx-1 text-gray-300">·</span>

                <p class="flex items-center text-gray-300 text-caption">소프트웨어강의</p>
            </div>
            <div class="flex gap-2">
                <!-- 본인 리뷰인 경우 : 수정하기 -->
                <div
                    class="relative flex flex-row items-center gap-1 cursor-pointer group"
                    @click="editReview"
                >
                    <Edit class="w-4 h-4" />
                    <p class="hidden text-gray-300 text-caption md:inline">수정하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 cursor-default left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        수정하기
                    </div>
                </div>

                <!-- 삭제하기 -->
                <div
                    class="relative flex flex-row items-center gap-1 cursor-pointer group"
                    @click="deleteReview"
                >
                    <Trash class="w-4 h-4" />
                    <p class="hidden text-gray-300 md:inline text-caption">삭제하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 cursor-default left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        삭제하기
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
        <div class="text-right text-gray-300 text-caption">{{ formattedDate }}</div>
    </div>
</template>

<script setup>
import Edit from '@/assets/icons/edit.svg'
import Trash from '@/assets/icons/trash.svg'
import Star from '@/assets/icons/star_filled.svg'

import { computed } from 'vue'

const props = defineProps({
    review: {
        type: Object,
        required: true,
    },
})

// 날짜 포맷 변경
const formattedDate = computed(() => {
    const date = new Date(props.review.createdAt)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')

    return `${year}.${month}.${day}`
})

// 별 개수 계산
const fullStars = computed(() => Math.floor(props.review.rating))
const hasHalfStar = computed(() => props.review.rating % 1 !== 0)
const emptyStars = computed(() => 5 - fullStars.value - (hasHalfStar.value ? 1 : 0))

//==========================
// 리뷰 삭제 / 수정 / 신고
//==========================
const emit = defineEmits(['edit-review', 'delete-review', 'update-self-review']) // 부모 컴포넌트에 이벤트 전달

const editReview = () => {
    emit('edit-review', props.review) // ProfileReviewSection으로 이벤트 전달
}

// // 리뷰 삭제
// const deleteReview = async () => {
//     const isConfirmed = window.confirm('리뷰를 삭제하시겠습니까?')
//     if (isConfirmed) {
//         try {
//             await deleteLectureReview(userStore.token, props.review.id)
//             console.log('✅ 리뷰 삭제 성공')
//             alert('리뷰가 삭제되었습니다.')
//             emit('delete-review', props.review.id) // 🔥 리뷰 섹션에서 fetchReviews() 호출
//             emit('update-review') // 🔥 부모에서 selfReview 업데이트하도록 요청
//         } catch (error) {
//             console.error('❌ 리뷰 삭제 중 오류 발생:', error)
//             alert('삭제에 실패했습니다. 나중에 다시 시도해주세요.')
//         }
//     }
// }

const deleteReview = () => {
    emit('delete-review', props.review) // ProfileReviewSection으로 이벤트 전달
}
</script>

<style></style>
