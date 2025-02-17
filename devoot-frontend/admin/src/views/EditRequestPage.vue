<template>
    <div class="flex flex-col gap-4">
        <div class="content-center h-20 text-h3">강의 수정 요청</div>
        <div class="grid grid-cols-12 gap-6">
            <LectureCard v-for="lecture in lectures" :key="lecture.id" :lecture="lecture" />
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'

import LectureCard from '@/components/LectureCard.vue'
import getEditRequest from '@/helpers/api'

const userStore = useUserStore()
const lectures = ref([]) // 강의 목록
const errorMessage = ref(null) // 에러 메세지 저장

// 관리자 유저 가져오는 함수
const fetchEditRequest = async () => {
    if (!userStore.token) {
        console.log('토큰 없음. API 요청 안함')
        return
    }

    try {
        const response = await getEditRequest(userStore.token)
        lectures.value = response.data
    } catch (error) {
        if (error.response?.status === 403) {
            errorMessage.value = '🚨 관리자 계정이 아닙니다. 관리자 페이지에 접근이 불가능합니다.'
        } else {
            errorMessage.value = '🚨 데이터를 불러오는 중 오류가 발생했습니다.'
        }
        console.error('🚨 강의 수정 요청 API 요청 실패:', error)
    }
}

// 🔥 Token 값이 변경될 때 fetchAdminUser 실행
watch(
    () => userStore.token,
    (newToken) => {
        if (newToken) {
            fetchEditRequest()
        }
    },
    { immediate: true }
)
</script>

<style scoped></style>
