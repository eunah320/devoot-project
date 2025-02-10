<template>
    <div class="flex flex-col w-full p-6 gap-6 max-h-[400px] overflow-y-auto">
        <ProfileReviewCard
            v-for="(review, index) in reviewData.content"
            :key="index"
            :review="review"
        />
    </div>
</template>

<script setup>
import ProfileReviewCard from './ProfileReviewCard.vue'
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore() // Pinia 스토어 가져오기
const userReviews = ref([])
const loadUserReviews = async () => {
    try {
        const mock_server_url = 'http://localhost:8080'
        // const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
        const profileId = userStore.userId // 여기에 실제 사용자 ID를 넣어야 함
        const API_URL = `${mock_server_url}/api/users/${profileId}}/reviews`
        // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함

        const response = await axios.get(
            API_URL,
            {},
            {
                headers: {
                    'Content-Type': 'application/json', //필수 헤더 추가
                    Authorization: `Bearer ${userStore.token}`, // Bearer 토큰 추가
                },
            }
        )

        userReviews.value = response.data
        console.log('유저리뷰', userReviews.value)
    } catch (error) {
        console.error('에러:', error)
    }
}

onMounted(() => {
    loadUserReviews() // JSON 데이터 가져오기
})
</script>

<style></style>
