<template>
    <div class="flex flex-col gap-6 p-6">
        <div class="flex justify-end">
            <!-- 리뷰 미작성 상태 : 리뷰 작성하기 -->
            <button class="flex flex-row w-auto gap-2 button-gray" @click="emitOpenReviewModal">
                <Edit class="w-4 h-4 text-gray-400" />
                <p>리뷰 작성하기</p>
            </button>
            <!--  리뷰 작성 상태 : 리뷰 수정하기 -->
        </div>
        <div v-if="reviews.length > 0" class="flex flex-col gap-6">
            <LectureReviewCard v-for="review in reviews" :key="review.id" :review="review" />
        </div>
        <p v-else class="text-black text-body-bold">아직 리뷰가 없습니다.</p>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getLectureReview } from '@/helpers/lecture'
import LectureReviewCard from './LectureReviewCard.vue'

import Edit from '@/assets/icons/edit.svg'

// 부모 컴포넌트로 이벤트 전달
const emit = defineEmits(['open-review-modal'])

const emitOpenReviewModal = () => {
    emit('open-review-modal')
}

// 라우트에 저장된 lectureId 저장
const route = useRoute()
const currentLectureId = ref(route.params.id)

// API에서 받아올 변수수
const totalElements = ref(null) // 리뷰 수
const totalPages = ref(null) // 페이지 수
const reviews = ref([]) // 리뷰를 담은 배열

// 페이지네이션
const pageIndex = ref(1) // 나중에 페이지네이션과 연결 해야함함

onMounted(async () => {
    try {
        const response = await getLectureReview(currentLectureId.value, pageIndex.value)
        const data = response.data
        console.log('api 응답:', data)
        console.log(data.totalElements)

        totalElements.value = data.totalElements
        totalPages.value = data.totalPages
        reviews.value = data.content
        console.log(reviews.value)
    } catch (error) {
        console.error('API 호출 중 오류 발생: ', error)
    }
})
</script>

<style scoped></style>
