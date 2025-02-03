<template>
    <div class="grid grid-cols-2 gap-4 p-4">
        <LectureCard
            v-for="lecture in lectures"
            :key="lecture.id"
            :name="lecture.name"
            :lecturer="lecture.lecturer"
            :platform="lecture.lecturer"
            :imageUrl="lecture.imageUrl"
            :tags="lecture.tags"
            :currentPrice="lecture.currentPrice"
            :originalPrice="lecture.originalPrice"
            :rating="lecture.rating"
            :reviewCount="lecture.reviewCount"
            :isBookmarked="lecture.isBookmarked"
        />
    </div>
</template>

<script>
import LectureCard from '@/components/Lecture/LectureCard.vue'

export default {
    name: 'HomePage',
    components: {
        LectureCard,
    },
    data() {
        return {
            lectures: [], // 강의 데이터를 저장할 배열
        }
    },
    created() {
        // JSON 파일에서 데이터 로드
        fetch('/lecturecard_dummy_data.json')
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Failed to load lecture data')
                }
                return response.json()
            })
            .then((data) => {
                this.lectures = data // 데이터를 lectures 배열에 저장
            })
            .catch((error) => {
                console.error('Error loading lecture data:', error)
            })
    },
}
</script>

<style scoped>
/* 추가적인 스타일이 필요하다면 여기에 작성 */
</style>
